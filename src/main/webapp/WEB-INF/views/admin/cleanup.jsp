<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Data Cleanup">

<style>
    .cleanup-container {
        max-width: 1000px;
        margin: 0 auto;
        padding: 2rem;
    }

    .cleanup-header {
        display: flex;
        align-items: center;
        gap: 1rem;
        margin-bottom: 2rem;
    }

    .cleanup-header i {
        font-size: 2.5rem;
        color: var(--primary-color, #6c5ce7);
    }

    .cleanup-header h2 {
        margin: 0;
        color: var(--text-primary, #212529);
    }

    .cleanup-header p {
        margin: 0.25rem 0 0 0;
        color: var(--text-secondary, #6c757d);
    }

    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2rem;
    }

    .stat-card {
        background: #ffffff;
        border: 1px solid var(--border-color, #e0e0e0);
        border-radius: 12px;
        padding: 1.5rem;
        text-align: center;
        transition: all 0.3s ease;
    }

    .stat-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }

    .stat-card i {
        font-size: 2rem;
        margin-bottom: 0.5rem;
    }

    .stat-card.trash i { color: #ef4444; }
    .stat-card.drafts i { color: #f59e0b; }
    .stat-card.users i { color: #8b5cf6; }
    .stat-card.files i { color: #3b82f6; }
    .stat-card.records i { color: #6366f1; }
    .stat-card.disk i { color: #10b981; }

    .stat-value {
        font-size: 2rem;
        font-weight: 700;
        color: var(--text-primary, #212529);
        line-height: 1.2;
    }

    .stat-label {
        color: var(--text-secondary, #6c757d);
        font-size: 0.85rem;
        margin-top: 0.25rem;
    }

    .cleanup-section {
        background: #ffffff;
        border: 1px solid var(--border-color, #e0e0e0);
        border-radius: 12px;
        padding: 1.5rem;
        margin-bottom: 1.5rem;
    }

    .section-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 1rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid var(--border-color, #e0e0e0);
    }

    .section-header h3 {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin: 0;
        color: var(--text-primary, #212529);
    }

    .section-header h3 i {
        color: var(--primary-color, #6c5ce7);
    }

    .config-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
    }

    .config-item {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .config-label {
        color: var(--text-secondary, #6c757d);
        font-size: 0.85rem;
    }

    .config-value {
        color: var(--text-primary, #212529);
        font-weight: 600;
        font-size: 1.1rem;
    }

    .scheduler-status {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.25rem 0.75rem;
        border-radius: 20px;
        font-size: 0.85rem;
        font-weight: 500;
    }

    .scheduler-status.enabled {
        background: rgba(16, 185, 129, 0.1);
        color: #10b981;
    }

    .scheduler-status.disabled {
        background: rgba(239, 68, 68, 0.1);
        color: #ef4444;
    }

    .cleanup-actions {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
    }

    .cleanup-btn {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.5rem;
        padding: 1.25rem;
        border: 1px solid var(--border-color, #e0e0e0);
        border-radius: 12px;
        background: #ffffff;
        color: var(--text-primary, #212529);
        cursor: pointer;
        transition: all 0.3s ease;
    }

    .cleanup-btn:hover {
        background: #f8f9fa;
        border-color: var(--primary-color, #6c5ce7);
        transform: translateY(-2px);
    }

    .cleanup-btn i {
        font-size: 1.5rem;
        color: var(--primary-color, #6c5ce7);
    }

    .cleanup-btn span {
        font-weight: 500;
        color: var(--text-primary, #212529);
    }

    .cleanup-btn small {
        color: var(--text-secondary, #6c757d);
        font-size: 0.75rem;
        text-align: center;
    }

    .cleanup-btn.danger {
        border-color: rgba(239, 68, 68, 0.3);
    }

    .cleanup-btn.danger:hover {
        border-color: #ef4444;
        background: rgba(239, 68, 68, 0.05);
    }

    .cleanup-btn.danger i {
        color: #ef4444;
    }

    .full-cleanup-section {
        background: linear-gradient(135deg, rgba(108, 92, 231, 0.08), rgba(139, 92, 246, 0.08));
        border-color: rgba(108, 92, 231, 0.3);
    }

    .full-cleanup-section p {
        color: var(--text-secondary, #6c757d);
        margin-bottom: 1.5rem;
    }

    .full-cleanup-btn {
        background: linear-gradient(135deg, #6c5ce7, #8b5cf6);
        color: #ffffff;
        border: none;
        padding: 1rem 2rem;
        border-radius: 10px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 0.75rem;
        transition: all 0.3s ease;
    }

    .full-cleanup-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(108, 92, 231, 0.4);
    }

    .full-cleanup-btn i {
        font-size: 1.25rem;
        color: #ffffff;
    }

    .alert {
        padding: 1rem 1.25rem;
        border-radius: 10px;
        margin-bottom: 1.5rem;
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .alert-success {
        background: rgba(16, 185, 129, 0.1);
        border: 1px solid rgba(16, 185, 129, 0.2);
        color: #10b981;
    }

    .alert-danger {
        background: rgba(239, 68, 68, 0.1);
        border: 1px solid rgba(239, 68, 68, 0.2);
        color: #ef4444;
    }

    .alert-warning {
        background: rgba(245, 158, 11, 0.1);
        border: 1px solid rgba(245, 158, 11, 0.2);
        color: #f59e0b;
    }

    .info-box {
        background: rgba(59, 130, 246, 0.1);
        border: 1px solid rgba(59, 130, 246, 0.2);
        border-radius: 10px;
        padding: 1rem;
        margin-top: 1rem;
    }

    .info-box p {
        margin: 0;
        color: #3b82f6;
        font-size: 0.9rem;
    }

    .info-box i {
        margin-right: 0.5rem;
    }
</style>

<div class="cleanup-container">
    
    <!-- Header -->
    <div class="cleanup-header">
        <i class="bi bi-gear-wide-connected"></i>
        <div>
            <h2>Data Cleanup</h2>
            <p>Manage database and file system cleanup tasks</p>
        </div>
    </div>

    <!-- Flash Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success animate-fade-in">
            <i class="bi bi-check-circle"></i>
            ${successMessage}
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger animate-fade-in">
            <i class="bi bi-exclamation-circle"></i>
            ${errorMessage}
        </div>
    </c:if>
    <c:if test="${not empty warningMessage}">
        <div class="alert alert-warning animate-fade-in">
            <i class="bi bi-exclamation-triangle"></i>
            ${warningMessage}
        </div>
    </c:if>

    <!-- Stats Preview -->
    <div class="stats-grid">
        <div class="stat-card trash">
            <i class="bi bi-trash3"></i>
            <div class="stat-value">${stats.trashedEmailsDeleted}</div>
            <div class="stat-label">Trashed Emails</div>
        </div>
        <div class="stat-card drafts">
            <i class="bi bi-file-earmark"></i>
            <div class="stat-value">${stats.draftsDeleted}</div>
            <div class="stat-label">Old Drafts</div>
        </div>
        <div class="stat-card users">
            <i class="bi bi-person-x"></i>
            <div class="stat-value">${stats.deletedUsersAnonymized}</div>
            <div class="stat-label">Deleted Users</div>
        </div>
        <div class="stat-card files">
            <i class="bi bi-file-earmark-x"></i>
            <div class="stat-value">${stats.orphanedFilesDeleted}</div>
            <div class="stat-label">Orphaned Files</div>
        </div>
        <div class="stat-card records">
            <i class="bi bi-database-x"></i>
            <div class="stat-value">${stats.orphanedRecordsDeleted}</div>
            <div class="stat-label">Orphaned Records</div>
        </div>
        <div class="stat-card disk">
            <i class="bi bi-hdd"></i>
            <div class="stat-value">${stats.formattedDiskSpace}</div>
            <div class="stat-label">Reclaimable Space</div>
        </div>
    </div>

    <!-- Configuration Section -->
    <div class="cleanup-section">
        <div class="section-header">
            <h3><i class="bi bi-sliders"></i> Configuration</h3>
            <c:choose>
                <c:when test="${schedulerEnabled}">
                    <span class="scheduler-status enabled">
                        <i class="bi bi-check-circle-fill"></i> Auto-Cleanup Enabled
                    </span>
                </c:when>
                <c:otherwise>
                    <span class="scheduler-status disabled">
                        <i class="bi bi-x-circle-fill"></i> Auto-Cleanup Disabled
                    </span>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="config-grid">
            <div class="config-item">
                <span class="config-label">Trash Retention</span>
                <span class="config-value">${trashRetentionDays} days</span>
            </div>
            <div class="config-item">
                <span class="config-label">Draft Retention</span>
                <span class="config-value">${draftRetentionDays} days</span>
            </div>
            <div class="config-item">
                <span class="config-label">User Anonymization</span>
                <span class="config-value">${deletedUserRetentionDays} days</span>
            </div>
        </div>
        <div class="info-box">
            <p><i class="bi bi-info-circle"></i> 
            Retention periods are configured in <code>application.properties</code>. 
            Items older than the retention period will be permanently deleted.</p>
        </div>
    </div>

    <!-- Individual Cleanup Actions -->
    <div class="cleanup-section">
        <div class="section-header">
            <h3><i class="bi bi-tools"></i> Individual Cleanup Tasks</h3>
        </div>
        <div class="cleanup-actions">
            <form action="${pageContext.request.contextPath}/admin/cleanup/run-trash" method="post" style="display: contents;">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="cleanup-btn danger">
                    <i class="bi bi-trash3"></i>
                    <span>Clean Trash</span>
                    <small>Delete trashed emails older than ${trashRetentionDays} days</small>
                </button>
            </form>
            
            <form action="${pageContext.request.contextPath}/admin/cleanup/run-drafts" method="post" style="display: contents;">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="cleanup-btn">
                    <i class="bi bi-file-earmark-x"></i>
                    <span>Clean Old Drafts</span>
                    <small>Delete drafts older than ${draftRetentionDays} days</small>
                </button>
            </form>
            
            <form action="${pageContext.request.contextPath}/admin/cleanup/run-attachments" method="post" style="display: contents;">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="cleanup-btn">
                    <i class="bi bi-paperclip"></i>
                    <span>Sync Attachments</span>
                    <small>Remove orphaned files & records</small>
                </button>
            </form>
            
            <form action="${pageContext.request.contextPath}/admin/cleanup/run-anonymize" method="post" style="display: contents;">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="cleanup-btn">
                    <i class="bi bi-person-x"></i>
                    <span>Anonymize Users</span>
                    <small>Anonymize users deleted ${deletedUserRetentionDays}+ days ago</small>
                </button>
            </form>
        </div>
    </div>

    <!-- Full Cleanup -->
    <div class="cleanup-section full-cleanup-section">
        <div class="section-header">
            <h3><i class="bi bi-lightning-charge"></i> Full Cleanup</h3>
        </div>
        <p style="color: var(--text-muted); margin-bottom: 1.5rem;">
            Run all cleanup tasks at once. This includes trash cleanup, draft cleanup, 
            user anonymization, and attachment synchronization.
        </p>
        <form action="${pageContext.request.contextPath}/admin/cleanup/run-full" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button type="submit" class="full-cleanup-btn">
                <i class="bi bi-play-fill"></i>
                Run Full Cleanup
            </button>
        </form>
    </div>

</div>

</layout:mailLayout>
