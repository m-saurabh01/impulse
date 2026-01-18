package com.wipro.iaf.email.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wipro.iaf.email.common.service.DataCleanupService.CleanupResult;

/**
 * Scheduler for automated data cleanup tasks.
 * 
 * <p>Runs cleanup jobs at configured intervals to:
 * <ul>
 *   <li>Delete old trashed emails</li>
 *   <li>Remove abandoned drafts</li>
 *   <li>Anonymize deleted user data</li>
 *   <li>Clean orphaned attachment files</li>
 * </ul>
 * </p>
 * 
 * <p>Schedule configuration:
 * <ul>
 *   <li>Full cleanup: Daily at 2:00 AM (configurable)</li>
 *   <li>Trash cleanup: Every 6 hours</li>
 *   <li>Attachment sync: Weekly on Sundays at 3:00 AM</li>
 * </ul>
 * </p>
 * 
 * <p>Enable/disable via properties:
 * <pre>
 * cleanup.scheduler.enabled=true
 * </pre>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-18
 */
@Component
public class DataCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(DataCleanupScheduler.class);

    private final DataCleanupService cleanupService;
    private final boolean schedulerEnabled;

    public DataCleanupScheduler(
            DataCleanupService cleanupService,
            @Value("${cleanup.scheduler.enabled:true}") boolean schedulerEnabled) {

        this.cleanupService = cleanupService;
        this.schedulerEnabled = schedulerEnabled;

        if (schedulerEnabled) {
            log.info("Data cleanup scheduler ENABLED");
            log.info("  Trash retention: {} days", cleanupService.getTrashRetentionDays());
            log.info("  Draft retention: {} days", cleanupService.getDraftRetentionDays());
            log.info("  Deleted user anonymization: {} days", cleanupService.getDeletedUserRetentionDays());
        } else {
            log.info("Data cleanup scheduler DISABLED");
        }
    }

    /**
     * Full cleanup task - runs daily at 2:00 AM.
     * 
     * <p>Performs all cleanup operations:
     * <ul>
     *   <li>Trash email cleanup</li>
     *   <li>Old draft cleanup</li>
     *   <li>Deleted user anonymization</li>
     *   <li>Orphaned attachment cleanup</li>
     * </ul>
     * </p>
     */
    @Scheduled(cron = "${cleanup.schedule.full:0 0 2 * * ?}")
    public void scheduledFullCleanup() {
        if (!schedulerEnabled) {
            log.debug("Cleanup scheduler disabled, skipping full cleanup");
            return;
        }

        log.info("=== Starting scheduled FULL cleanup ===");
        long startTime = System.currentTimeMillis();

        try {
            CleanupResult result = cleanupService.runFullCleanup();
            long duration = System.currentTimeMillis() - startTime;

            log.info("=== Full cleanup completed in {}ms ===", duration);
            log.info("  Trashed emails deleted: {}", result.getTrashedEmailsDeleted());
            log.info("  Old drafts deleted: {}", result.getDraftsDeleted());
            log.info("  Users anonymized: {}", result.getDeletedUsersAnonymized());
            log.info("  Orphaned files deleted: {}", result.getOrphanedFilesDeleted());
            log.info("  Orphaned records deleted: {}", result.getOrphanedRecordsDeleted());
            log.info("  Disk space reclaimed: {}", result.getFormattedDiskSpace());

            if (!result.getErrors().isEmpty()) {
                log.warn("  Errors encountered: {}", result.getErrors().size());
                for (String error : result.getErrors()) {
                    log.warn("    - {}", error);
                }
            }
        } catch (Exception e) {
            log.error("Full cleanup failed", e);
        }
    }

    /**
     * Trash cleanup task - runs every 6 hours.
     * 
     * <p>Only cleans permanently deleted (trashed) emails 
     * that have exceeded the retention period.</p>
     */
    @Scheduled(cron = "${cleanup.schedule.trash:0 0 */6 * * ?}")
    public void scheduledTrashCleanup() {
        if (!schedulerEnabled) {
            return;
        }

        log.info("Starting scheduled TRASH cleanup...");
        try {
            CleanupResult result = cleanupService.cleanupTrashedEmails();
            log.info("Trash cleanup completed: {} emails deleted", 
                    result.getTrashedEmailsDeleted());
        } catch (Exception e) {
            log.error("Trash cleanup failed", e);
        }
    }

    /**
     * Attachment sync task - runs weekly on Sundays at 3:00 AM.
     * 
     * <p>Synchronizes attachment files with database:
     * <ul>
     *   <li>Deletes orphaned files (no DB record)</li>
     *   <li>Removes orphaned records (no file on disk)</li>
     * </ul>
     * </p>
     */
    @Scheduled(cron = "${cleanup.schedule.attachments:0 0 3 ? * SUN}")
    public void scheduledAttachmentSync() {
        if (!schedulerEnabled) {
            return;
        }

        log.info("Starting scheduled ATTACHMENT sync...");
        try {
            CleanupResult result = cleanupService.cleanupOrphanedAttachments();
            log.info("Attachment sync completed: {} files, {} records deleted, {} reclaimed", 
                    result.getOrphanedFilesDeleted(),
                    result.getOrphanedRecordsDeleted(),
                    result.getFormattedDiskSpace());
        } catch (Exception e) {
            log.error("Attachment sync failed", e);
        }
    }

    /**
     * Checks if the scheduler is enabled.
     * 
     * @return true if scheduler is enabled
     */
    public boolean isSchedulerEnabled() {
        return schedulerEnabled;
    }
}
