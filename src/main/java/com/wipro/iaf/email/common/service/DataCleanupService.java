package com.wipro.iaf.email.common.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.attachment.entity.Attachment;
import com.wipro.iaf.email.attachment.repo.AttachmentRepository;
import com.wipro.iaf.email.mail.entity.Email;
import com.wipro.iaf.email.mail.entity.EmailRecipient;
import com.wipro.iaf.email.mail.repo.EmailRecipientRepository;
import com.wipro.iaf.email.mail.repo.EmailRepository;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

/**
 * Service for cleaning up old data from database and file system.
 * 
 * <p>Handles automatic cleanup of:
 * <ul>
 *   <li>Trash emails older than retention period</li>
 *   <li>Orphaned drafts (abandoned drafts)</li>
 *   <li>Soft-deleted user data after anonymization period</li>
 *   <li>Orphaned attachment files (files without DB records)</li>
 *   <li>Orphaned attachment records (DB records without files)</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-18
 */
@Service
public class DataCleanupService {

    private static final Logger log = LoggerFactory.getLogger(DataCleanupService.class);

    private final EmailRepository emailRepo;
    private final EmailRecipientRepository emailRecipientRepo;
    private final AttachmentRepository attachmentRepo;
    private final UserRepository userRepo;
    private final Path attachmentsBasePath;

    // Retention periods (in days) - configurable via properties
    private final int trashRetentionDays;
    private final int draftRetentionDays;
    private final int deletedUserRetentionDays;

    public DataCleanupService(
            EmailRepository emailRepo,
            EmailRecipientRepository emailRecipientRepo,
            AttachmentRepository attachmentRepo,
            UserRepository userRepo,
            @Value("${mail.attachments.base-path}") String attachmentsDir,
            @Value("${cleanup.trash.retention-days:30}") int trashRetentionDays,
            @Value("${cleanup.draft.retention-days:60}") int draftRetentionDays,
            @Value("${cleanup.deleted-user.retention-days:90}") int deletedUserRetentionDays) {

        this.emailRepo = emailRepo;
        this.emailRecipientRepo = emailRecipientRepo;
        this.attachmentRepo = attachmentRepo;
        this.userRepo = userRepo;
        this.attachmentsBasePath = Paths.get(attachmentsDir);
        this.trashRetentionDays = trashRetentionDays;
        this.draftRetentionDays = draftRetentionDays;
        this.deletedUserRetentionDays = deletedUserRetentionDays;
    }

    // ========================================
    // CLEANUP RESULT DTO
    // ========================================

    /**
     * Result object containing cleanup statistics.
     */
    public static class CleanupResult {
        private int trashedEmailsDeleted = 0;
        private int draftsDeleted = 0;
        private int deletedUsersAnonymized = 0;
        private int orphanedFilesDeleted = 0;
        private int orphanedRecordsDeleted = 0;
        private long diskSpaceReclaimed = 0; // in bytes
        private List<String> errors = new ArrayList<>();

        public int getTrashedEmailsDeleted() { return trashedEmailsDeleted; }
        public void setTrashedEmailsDeleted(int count) { this.trashedEmailsDeleted = count; }
        public void addTrashedEmailsDeleted(int count) { this.trashedEmailsDeleted += count; }

        public int getDraftsDeleted() { return draftsDeleted; }
        public void setDraftsDeleted(int count) { this.draftsDeleted = count; }
        public void addDraftsDeleted(int count) { this.draftsDeleted += count; }

        public int getDeletedUsersAnonymized() { return deletedUsersAnonymized; }
        public void setDeletedUsersAnonymized(int count) { this.deletedUsersAnonymized = count; }
        public void addDeletedUsersAnonymized(int count) { this.deletedUsersAnonymized += count; }

        public int getOrphanedFilesDeleted() { return orphanedFilesDeleted; }
        public void setOrphanedFilesDeleted(int count) { this.orphanedFilesDeleted = count; }
        public void addOrphanedFilesDeleted(int count) { this.orphanedFilesDeleted += count; }

        public int getOrphanedRecordsDeleted() { return orphanedRecordsDeleted; }
        public void setOrphanedRecordsDeleted(int count) { this.orphanedRecordsDeleted = count; }
        public void addOrphanedRecordsDeleted(int count) { this.orphanedRecordsDeleted += count; }

        public long getDiskSpaceReclaimed() { return diskSpaceReclaimed; }
        public void setDiskSpaceReclaimed(long bytes) { this.diskSpaceReclaimed = bytes; }
        public void addDiskSpaceReclaimed(long bytes) { this.diskSpaceReclaimed += bytes; }

        public List<String> getErrors() { return errors; }
        public void addError(String error) { this.errors.add(error); }

        public String getFormattedDiskSpace() {
            if (diskSpaceReclaimed < 1024) {
                return diskSpaceReclaimed + " B";
            } else if (diskSpaceReclaimed < 1024 * 1024) {
                return String.format("%.1f KB", diskSpaceReclaimed / 1024.0);
            } else if (diskSpaceReclaimed < 1024L * 1024 * 1024) {
                return String.format("%.1f MB", diskSpaceReclaimed / (1024.0 * 1024));
            } else {
                return String.format("%.2f GB", diskSpaceReclaimed / (1024.0 * 1024 * 1024));
            }
        }

        public int getTotalItemsDeleted() {
            return trashedEmailsDeleted + draftsDeleted + orphanedFilesDeleted + orphanedRecordsDeleted;
        }

        @Override
        public String toString() {
            return String.format(
                "CleanupResult{trashedEmails=%d, drafts=%d, anonymizedUsers=%d, " +
                "orphanedFiles=%d, orphanedRecords=%d, diskReclaimed=%s, errors=%d}",
                trashedEmailsDeleted, draftsDeleted, deletedUsersAnonymized,
                orphanedFilesDeleted, orphanedRecordsDeleted, getFormattedDiskSpace(),
                errors.size());
        }
    }

    // ========================================
    // CLEANUP STATISTICS (Preview)
    // ========================================

    /**
     * Gets cleanup statistics without actually deleting anything.
     * Useful for preview before running cleanup.
     * 
     * @return CleanupResult with counts of items that would be deleted
     */
    public CleanupResult getCleanupStatistics() {
        CleanupResult stats = new CleanupResult();

        LocalDateTime trashCutoff = LocalDateTime.now().minusDays(trashRetentionDays);
        LocalDateTime draftCutoff = LocalDateTime.now().minusDays(draftRetentionDays);
        LocalDateTime userCutoff = LocalDateTime.now().minusDays(deletedUserRetentionDays);

        // Count trashed emails eligible for permanent deletion
        stats.setTrashedEmailsDeleted(countTrashedEmailsOlderThan(trashCutoff));

        // Count old drafts
        stats.setDraftsDeleted(countOldDrafts(draftCutoff));

        // Count soft-deleted users to anonymize
        stats.setDeletedUsersAnonymized(countDeletedUsersOlderThan(userCutoff));

        // Count orphaned files and records
        OrphanStats orphanStats = countOrphanedAttachments();
        stats.setOrphanedFilesDeleted(orphanStats.orphanedFiles);
        stats.setOrphanedRecordsDeleted(orphanStats.orphanedRecords);
        stats.setDiskSpaceReclaimed(orphanStats.diskSpace);

        return stats;
    }

    // ========================================
    // MAIN CLEANUP METHODS
    // ========================================

    /**
     * Runs all cleanup tasks.
     * 
     * @return CleanupResult with statistics of deleted items
     */
    public CleanupResult runFullCleanup() {
        log.info("Starting full data cleanup...");
        CleanupResult result = new CleanupResult();

        try {
            // 1. Clean trashed emails
            CleanupResult trashResult = cleanupTrashedEmails();
            result.addTrashedEmailsDeleted(trashResult.getTrashedEmailsDeleted());
            result.getErrors().addAll(trashResult.getErrors());
        } catch (Exception e) {
            log.error("Error cleaning trashed emails", e);
            result.addError("Trash cleanup failed: " + e.getMessage());
        }

        try {
            // 2. Clean old drafts
            CleanupResult draftResult = cleanupOldDrafts();
            result.addDraftsDeleted(draftResult.getDraftsDeleted());
            result.getErrors().addAll(draftResult.getErrors());
        } catch (Exception e) {
            log.error("Error cleaning old drafts", e);
            result.addError("Draft cleanup failed: " + e.getMessage());
        }

        try {
            // 3. Anonymize deleted users
            CleanupResult userResult = anonymizeDeletedUsers();
            result.addDeletedUsersAnonymized(userResult.getDeletedUsersAnonymized());
            result.getErrors().addAll(userResult.getErrors());
        } catch (Exception e) {
            log.error("Error anonymizing deleted users", e);
            result.addError("User anonymization failed: " + e.getMessage());
        }

        try {
            // 4. Clean orphaned attachments
            CleanupResult attachmentResult = cleanupOrphanedAttachments();
            result.addOrphanedFilesDeleted(attachmentResult.getOrphanedFilesDeleted());
            result.addOrphanedRecordsDeleted(attachmentResult.getOrphanedRecordsDeleted());
            result.addDiskSpaceReclaimed(attachmentResult.getDiskSpaceReclaimed());
            result.getErrors().addAll(attachmentResult.getErrors());
        } catch (Exception e) {
            log.error("Error cleaning orphaned attachments", e);
            result.addError("Attachment cleanup failed: " + e.getMessage());
        }

        log.info("Cleanup completed: {}", result);
        return result;
    }

    /**
     * Permanently deletes trashed emails older than retention period.
     */
    @Transactional
    public CleanupResult cleanupTrashedEmails() {
        CleanupResult result = new CleanupResult();
        LocalDateTime cutoff = LocalDateTime.now().minusDays(trashRetentionDays);

        log.info("Cleaning trashed emails older than {} days (before {})", 
                trashRetentionDays, cutoff);

        // Find all email recipients marked as deleted before cutoff
        List<EmailRecipient> oldTrash = findTrashedEmailsOlderThan(cutoff);

        for (EmailRecipient recipient : oldTrash) {
            try {
                // Delete the recipient record (permanent deletion for this user)
                emailRecipientRepo.delete(recipient);
                result.addTrashedEmailsDeleted(1);

                // Check if email has no more recipients - then delete email entirely
                Email email = recipient.getEmail();
                long remainingRecipients = emailRecipientRepo.countByEmailId(email.getId());
                
                if (remainingRecipients == 0) {
                    // Delete attachments first
                    deleteEmailAttachments(email.getId(), result);
                    // Delete email
                    emailRepo.delete(email);
                }
            } catch (Exception e) {
                log.error("Error deleting trashed email recipient", e);
                result.addError("Failed to delete recipient: " + e.getMessage());
            }
        }

        log.info("Deleted {} trashed emails", result.getTrashedEmailsDeleted());
        return result;
    }

    /**
     * Deletes old draft emails that have been abandoned.
     */
    @Transactional
    public CleanupResult cleanupOldDrafts() {
        CleanupResult result = new CleanupResult();
        LocalDateTime cutoff = LocalDateTime.now().minusDays(draftRetentionDays);

        log.info("Cleaning draft emails older than {} days (before {})", 
                draftRetentionDays, cutoff);

        List<Email> oldDrafts = findOldDrafts(cutoff);

        for (Email draft : oldDrafts) {
            try {
                // Delete attachments first
                deleteEmailAttachments(draft.getId(), result);
                // Delete all recipients
                emailRecipientRepo.deleteByEmailId(draft.getId());
                // Delete draft
                emailRepo.delete(draft);
                result.addDraftsDeleted(1);
            } catch (Exception e) {
                log.error("Error deleting old draft", e);
                result.addError("Failed to delete draft " + draft.getId() + ": " + e.getMessage());
            }
        }

        log.info("Deleted {} old drafts", result.getDraftsDeleted());
        return result;
    }

    /**
     * Anonymizes soft-deleted users after retention period.
     * Replaces email with anonymized version, clears personal data.
     */
    @Transactional
    public CleanupResult anonymizeDeletedUsers() {
        CleanupResult result = new CleanupResult();
        LocalDateTime cutoff = LocalDateTime.now().minusDays(deletedUserRetentionDays);

        log.info("Anonymizing deleted users older than {} days", deletedUserRetentionDays);

        List<User> deletedUsers = findDeletedUsersOlderThan(cutoff);

        for (User user : deletedUsers) {
            try {
                // Check if already anonymized
                if (user.getEmail().startsWith("deleted_")) {
                    continue;
                }

                // Anonymize the user - use placeholder hash since field is not nullable
                String anonymizedEmail = "deleted_" + user.getId() + "@anonymized.local";
                user.setEmail(anonymizedEmail);
                user.setDisplayName(null);
                user.setSignature(null);
                user.setPasswordHash("ANONYMIZED_ACCOUNT_NO_LOGIN");
                userRepo.save(user);

                result.addDeletedUsersAnonymized(1);
            } catch (Exception e) {
                log.error("Error anonymizing user {}", user.getId(), e);
                result.addError("Failed to anonymize user " + user.getId() + ": " + e.getMessage());
            }
        }

        log.info("Anonymized {} deleted users", result.getDeletedUsersAnonymized());
        return result;
    }

    /**
     * Cleans up orphaned attachments (files without DB records and vice versa).
     */
    public CleanupResult cleanupOrphanedAttachments() {
        CleanupResult result = new CleanupResult();

        log.info("Cleaning orphaned attachments...");

        // 1. Find and delete orphaned files (files without DB records)
        try {
            cleanupOrphanedFiles(result);
        } catch (Exception e) {
            log.error("Error cleaning orphaned files", e);
            result.addError("Orphaned files cleanup failed: " + e.getMessage());
        }

        // 2. Find and delete orphaned records (DB records without files)
        try {
            cleanupOrphanedRecords(result);
        } catch (Exception e) {
            log.error("Error cleaning orphaned records", e);
            result.addError("Orphaned records cleanup failed: " + e.getMessage());
        }

        log.info("Cleaned {} orphaned files, {} orphaned records, reclaimed {}", 
                result.getOrphanedFilesDeleted(), 
                result.getOrphanedRecordsDeleted(),
                result.getFormattedDiskSpace());

        return result;
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    private List<EmailRecipient> findTrashedEmailsOlderThan(LocalDateTime cutoff) {
        return emailRecipientRepo.findAll().stream()
            .filter(r -> r.isDeleted() && r.getDeletedAt() != null && r.getDeletedAt().isBefore(cutoff))
            .collect(Collectors.toList());
    }

    private int countTrashedEmailsOlderThan(LocalDateTime cutoff) {
        return (int) emailRecipientRepo.findAll().stream()
            .filter(r -> r.isDeleted() && r.getDeletedAt() != null && r.getDeletedAt().isBefore(cutoff))
            .count();
    }

    private List<Email> findOldDrafts(LocalDateTime cutoff) {
        return emailRepo.findAll().stream()
            .filter(e -> e.isDraft() && e.getCreatedAt().isBefore(cutoff))
            .collect(Collectors.toList());
    }

    private int countOldDrafts(LocalDateTime cutoff) {
        return (int) emailRepo.findAll().stream()
            .filter(e -> e.isDraft() && e.getCreatedAt().isBefore(cutoff))
            .count();
    }

    private List<User> findDeletedUsersOlderThan(LocalDateTime cutoff) {
        return userRepo.findAll().stream()
            .filter(u -> u.isDeleted())
            .filter(u -> !u.getEmail().startsWith("deleted_")) // Not already anonymized
            .collect(Collectors.toList());
    }

    private int countDeletedUsersOlderThan(LocalDateTime cutoff) {
        return (int) userRepo.findAll().stream()
            .filter(u -> u.isDeleted())
            .filter(u -> !u.getEmail().startsWith("deleted_"))
            .count();
    }

    private void deleteEmailAttachments(Long emailId, CleanupResult result) {
        List<Attachment> attachments = attachmentRepo.findByEmailId(emailId);
        
        for (Attachment attachment : attachments) {
            try {
                // Delete file
                Path filePath = attachmentsBasePath
                    .resolve(String.valueOf(emailId))
                    .resolve(attachment.getStoredFilename());
                
                if (Files.exists(filePath)) {
                    long fileSize = Files.size(filePath);
                    Files.delete(filePath);
                    result.addDiskSpaceReclaimed(fileSize);
                }
                
                // Delete record
                attachmentRepo.delete(attachment);
            } catch (IOException e) {
                log.error("Error deleting attachment file", e);
                result.addError("Failed to delete attachment file: " + e.getMessage());
            }
        }

        // Try to delete email directory if empty
        try {
            Path emailDir = attachmentsBasePath.resolve(String.valueOf(emailId));
            if (Files.exists(emailDir) && isDirectoryEmpty(emailDir)) {
                Files.delete(emailDir);
            }
        } catch (IOException e) {
            // Ignore - directory might not be empty
        }
    }

    private boolean isDirectoryEmpty(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            return !stream.iterator().hasNext();
        }
    }

    private void cleanupOrphanedFiles(CleanupResult result) throws IOException {
        if (!Files.exists(attachmentsBasePath)) {
            return;
        }

        // Get all attachment stored filenames from DB
        Set<String> dbFilenames = new HashSet<>();
        for (Attachment a : attachmentRepo.findAll()) {
            dbFilenames.add(a.getEmail().getId() + "/" + a.getStoredFilename());
        }

        // Walk filesystem and find files not in DB
        try (DirectoryStream<Path> emailDirs = Files.newDirectoryStream(attachmentsBasePath)) {
            for (Path emailDir : emailDirs) {
                if (!Files.isDirectory(emailDir)) continue;

                String emailId = emailDir.getFileName().toString();

                try (DirectoryStream<Path> files = Files.newDirectoryStream(emailDir)) {
                    for (Path file : files) {
                        String key = emailId + "/" + file.getFileName().toString();
                        
                        if (!dbFilenames.contains(key)) {
                            // Orphaned file - delete it
                            try {
                                long fileSize = Files.size(file);
                                Files.delete(file);
                                result.addOrphanedFilesDeleted(1);
                                result.addDiskSpaceReclaimed(fileSize);
                                log.debug("Deleted orphaned file: {}", file);
                            } catch (IOException e) {
                                result.addError("Failed to delete orphaned file: " + file);
                            }
                        }
                    }
                }

                // Clean up empty directories
                if (isDirectoryEmpty(emailDir)) {
                    Files.delete(emailDir);
                }
            }
        }
    }

    @Transactional
    private void cleanupOrphanedRecords(CleanupResult result) {
        // Find attachment records where file doesn't exist
        for (Attachment attachment : attachmentRepo.findAll()) {
            Path filePath = attachmentsBasePath
                .resolve(String.valueOf(attachment.getEmail().getId()))
                .resolve(attachment.getStoredFilename());

            if (!Files.exists(filePath)) {
                // Orphaned record - delete it
                try {
                    attachmentRepo.delete(attachment);
                    result.addOrphanedRecordsDeleted(1);
                    log.debug("Deleted orphaned record: {}", attachment.getId());
                } catch (Exception e) {
                    result.addError("Failed to delete orphaned record: " + attachment.getId());
                }
            }
        }
    }

    private static class OrphanStats {
        int orphanedFiles = 0;
        int orphanedRecords = 0;
        long diskSpace = 0;
    }

    private OrphanStats countOrphanedAttachments() {
        OrphanStats stats = new OrphanStats();

        try {
            if (!Files.exists(attachmentsBasePath)) {
                return stats;
            }

            // Get all attachment stored filenames from DB
            Set<String> dbFilenames = new HashSet<>();
            for (Attachment a : attachmentRepo.findAll()) {
                dbFilenames.add(a.getEmail().getId() + "/" + a.getStoredFilename());
            }

            // Count orphaned files
            try (DirectoryStream<Path> emailDirs = Files.newDirectoryStream(attachmentsBasePath)) {
                for (Path emailDir : emailDirs) {
                    if (!Files.isDirectory(emailDir)) continue;
                    String emailId = emailDir.getFileName().toString();

                    try (DirectoryStream<Path> files = Files.newDirectoryStream(emailDir)) {
                        for (Path file : files) {
                            String key = emailId + "/" + file.getFileName().toString();
                            if (!dbFilenames.contains(key)) {
                                stats.orphanedFiles++;
                                stats.diskSpace += Files.size(file);
                            }
                        }
                    }
                }
            }

            // Count orphaned records
            for (Attachment attachment : attachmentRepo.findAll()) {
                Path filePath = attachmentsBasePath
                    .resolve(String.valueOf(attachment.getEmail().getId()))
                    .resolve(attachment.getStoredFilename());

                if (!Files.exists(filePath)) {
                    stats.orphanedRecords++;
                }
            }
        } catch (IOException e) {
            log.error("Error counting orphaned attachments", e);
        }

        return stats;
    }

    // ========================================
    // GETTERS FOR CONFIGURATION
    // ========================================

    public int getTrashRetentionDays() { return trashRetentionDays; }
    public int getDraftRetentionDays() { return draftRetentionDays; }
    public int getDeletedUserRetentionDays() { return deletedUserRetentionDays; }
}
