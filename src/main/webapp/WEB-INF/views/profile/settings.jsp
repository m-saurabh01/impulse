<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Profile & Settings">

<div class="profile-container">
    
    <!-- Success/Error Messages -->
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

    <!-- Profile Header -->
    <div class="profile-header">
        <div class="profile-avatar-large">
            ${fn:toUpperCase(fn:substring(user.email, 0, 1))}
        </div>
        <div class="profile-info">
            <h2>${user.displayNameOrEmail}</h2>
            <p class="profile-email">${user.email}</p>
            <c:if test="${not empty user.createdAt}">
                <p class="profile-since">
                    <i class="bi bi-calendar3"></i>
                    Member since 
                    <c:set var="monthNum" value="${fn:substring(user.createdAt, 5, 7)}" />
                    <c:choose>
                        <c:when test="${monthNum == '01'}">January</c:when>
                        <c:when test="${monthNum == '02'}">February</c:when>
                        <c:when test="${monthNum == '03'}">March</c:when>
                        <c:when test="${monthNum == '04'}">April</c:when>
                        <c:when test="${monthNum == '05'}">May</c:when>
                        <c:when test="${monthNum == '06'}">June</c:when>
                        <c:when test="${monthNum == '07'}">July</c:when>
                        <c:when test="${monthNum == '08'}">August</c:when>
                        <c:when test="${monthNum == '09'}">September</c:when>
                        <c:when test="${monthNum == '10'}">October</c:when>
                        <c:when test="${monthNum == '11'}">November</c:when>
                        <c:when test="${monthNum == '12'}">December</c:when>
                        <c:otherwise>${monthNum}</c:otherwise>
                    </c:choose>
                    ${fn:substring(user.createdAt, 0, 4)}
                </p>
            </c:if>
        </div>
    </div>

    <!-- Achievement Badges Section -->
    <div class="achievements-showcase" id="achievementsShowcase">
        <div class="achievements-header">
            <h3><i class="bi bi-trophy"></i> Your Achievements</h3>
            <div class="achievements-summary" id="achievementsSummary">
                <span class="badge-count">Loading...</span>
            </div>
        </div>
        <div class="achievements-grid" id="achievementsGrid">
            <!-- Populated by JavaScript -->
        </div>
    </div>

    <div class="profile-sections">
        
        <!-- Profile Information Section -->
        <div class="profile-section">
            <div class="section-header">
                <i class="bi bi-person"></i>
                <h3>Profile Information</h3>
            </div>
            <form action="${pageContext.request.contextPath}/profile/update" method="post" class="profile-form">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" value="${user.email}" disabled class="form-control disabled"/>
                    <small class="form-hint">Email cannot be changed</small>
                </div>
                
                <div class="form-group">
                    <label for="displayName">Display Name</label>
                    <input type="text" id="displayName" name="displayName" 
                           value="${user.displayName}" 
                           placeholder="Enter your display name"
                           class="form-control"/>
                    <small class="form-hint">This name will be shown to other users</small>
                </div>
                
                <div class="form-group">
                    <label for="signature">Email Signature</label>
                    <div class="signature-editor-container">
                        <textarea id="signature" name="signature">${user.signature}</textarea>
                    </div>
                    <small class="form-hint">This signature will be automatically added to your outgoing emails. Use rich formatting to make it stand out!</small>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn-primary-gradient" onclick="saveSignature(event)">
                        <i class="bi bi-check-lg"></i>
                        Save Changes
                    </button>
                </div>
            </form>
        </div>

        <!-- Security Section -->
        <div class="profile-section">
            <div class="section-header">
                <i class="bi bi-shield-lock"></i>
                <h3>Security</h3>
            </div>
            <form action="${pageContext.request.contextPath}/profile/changePassword" method="post" class="profile-form">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                
                <div class="form-group">
                    <label for="currentPassword">Current Password</label>
                    <input type="password" id="currentPassword" name="currentPassword" 
                           required
                           class="form-control"/>
                </div>
                
                <div class="form-group">
                    <label for="newPassword">New Password</label>
                    <input type="password" id="newPassword" name="newPassword" 
                           required minlength="6"
                           class="form-control"/>
                    <small class="form-hint">Minimum 6 characters</small>
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">Confirm New Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" 
                           required
                           class="form-control"/>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn-danger-gradient">
                        <i class="bi bi-key"></i>
                        Change Password
                    </button>
                </div>
            </form>
        </div>

        <!-- Danger Zone Section -->
        <div class="profile-section danger-zone">
            <div class="section-header">
                <i class="bi bi-exclamation-triangle"></i>
                <h3>Danger Zone</h3>
            </div>
            <div class="danger-zone-content">
                <div class="danger-warning">
                    <i class="bi bi-trash3"></i>
                    <div>
                        <h4>Delete Account Permanently</h4>
                        <p>Once you delete your account, there is no going back. All your emails, contacts, labels, and data will be permanently erased.</p>
                    </div>
                </div>
                <button type="button" class="btn-delete-account" onclick="showDeleteModal()">
                    <i class="bi bi-trash3"></i>
                    Delete My Account
                </button>
            </div>
        </div>

    </div>
</div>

<!-- Delete Account Confirmation Modal -->
<div class="modal-overlay" id="deleteAccountModal" style="display: none;">
    <div class="modal-container delete-modal">
        <div class="modal-header">
            <h3><i class="bi bi-exclamation-triangle-fill text-danger"></i> Delete Account</h3>
            <button type="button" class="modal-close" onclick="hideDeleteModal()">&times;</button>
        </div>
        <div class="modal-body">
            <div class="delete-warning-box">
                <i class="bi bi-shield-exclamation"></i>
                <p><strong>This action cannot be undone!</strong></p>
                <p>This will permanently delete:</p>
                <ul>
                    <li><i class="bi bi-envelope"></i> All your emails and attachments</li>
                    <li><i class="bi bi-tag"></i> All your labels and organization</li>
                    <li><i class="bi bi-people"></i> All your contacts</li>
                    <li><i class="bi bi-trophy"></i> All your achievements</li>
                    <li><i class="bi bi-person"></i> Your account and profile</li>
                </ul>
            </div>
            <form action="${pageContext.request.contextPath}/profile/deleteAccount" method="post" id="deleteAccountForm">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="form-group">
                    <label for="deletePassword">Enter your password to confirm:</label>
                    <div class="password-wrapper">
                        <input type="password" id="deletePassword" name="password" 
                               required
                               class="form-control"
                               placeholder="Your current password">
                        <button type="button" class="password-toggle" onclick="toggleDeletePassword()" aria-label="Show password">
                            <i class="bi bi-eye" id="deletePasswordIcon"></i>
                        </button>
                    </div>
                </div>
                <div class="modal-actions">
                    <button type="button" class="btn-cancel" onclick="hideDeleteModal()">Cancel</button>
                    <button type="submit" class="btn-delete-confirm">
                        <i class="bi bi-trash3"></i>
                        Yes, Delete My Account
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- TinyMCE for Signature Editor -->
<script src="${pageContext.request.contextPath}/assets/tinymce/tinymce.min.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    tinymce.init({
        selector: '#signature',
        height: 250,
        menubar: false,
        plugins: 'link lists',
        toolbar: 'undo redo | bold italic underline | forecolor backcolor | alignleft aligncenter alignright | bullist numlist | link | removeformat',
        content_style: 'body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; font-size: 14px; line-height: 1.5; color: #333; }',
        placeholder: 'Create your email signature with rich formatting...',
        branding: false,
        statusbar: false,
        skin: 'oxide',
        content_css: false,
        setup: function(editor) {
            editor.on('init', function() {
                // Style the editor container
                var container = editor.getContainer();
                if (container) {
                    container.style.borderRadius = '8px';
                    container.style.border = '1px solid #ddd';
                }
            });
        }
    });
});

function saveSignature(event) {
    // Sync TinyMCE content to textarea before form submit
    if (tinymce.get('signature')) {
        tinymce.get('signature').save();
    }
}
</script>

<style>
.signature-editor-container {
    border-radius: 8px;
    overflow: hidden;
}

.signature-editor-container .tox-tinymce {
    border-radius: 8px !important;
}

.signature-editor-container .tox-toolbar__primary {
    background: #f8f9fa !important;
}

/* Achievement Styles */
.achievements-showcase {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    border: 1px solid rgba(102, 126, 234, 0.2);
    border-radius: 16px;
    padding: 24px;
    margin-bottom: 24px;
}

.achievements-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.achievements-header h3 {
    margin: 0;
    font-size: 1.25rem;
    color: #333;
    display: flex;
    align-items: center;
    gap: 10px;
}

.achievements-header h3 i {
    color: #f59e0b;
}

.achievements-summary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 8px 16px;
    border-radius: 20px;
    font-size: 0.9rem;
    font-weight: 600;
}

.achievements-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 16px;
}

.achievement-badge {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16px 12px;
    border-radius: 12px;
    background: white;
    border: 2px solid transparent;
    cursor: pointer;
    transition: all 0.3s ease;
}

.achievement-badge.unlocked {
    border-color: #10b981;
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
}

.achievement-badge.locked {
    opacity: 0.5;
    filter: grayscale(100%);
}

.achievement-badge:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.achievement-badge .badge-icon {
    font-size: 2rem;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 8px;
}

.achievement-badge .badge-name {
    font-size: 0.75rem;
    font-weight: 600;
    text-align: center;
    color: #333;
    line-height: 1.2;
}

.achievement-badge .badge-checkmark {
    position: absolute;
    top: -6px;
    right: -6px;
    width: 22px;
    height: 22px;
    background: #10b981;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 0.75rem;
    border: 2px solid white;
}

.achievement-tooltip {
    position: fixed;
    background: #1e293b;
    color: white;
    padding: 12px 16px;
    border-radius: 8px;
    max-width: 250px;
    z-index: 10000;
    font-size: 0.85rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    pointer-events: none;
}

.achievement-tooltip .tooltip-title {
    font-weight: 600;
    margin-bottom: 4px;
}

.achievement-tooltip .tooltip-desc {
    opacity: 0.85;
    font-size: 0.8rem;
}

.achievement-tooltip .tooltip-points {
    margin-top: 8px;
    padding-top: 8px;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
    color: #fbbf24;
    font-weight: 600;
}

/* Danger Zone Styles - Matching page design */
.danger-zone {
    background: white !important;
    border: 1px solid #e9ecef !important;
    box-shadow: 0 2px 12px rgba(0,0,0,0.08) !important;
}

.danger-zone .section-header {
    background: linear-gradient(135deg, #fef2f2 0%, #fff 100%);
    border-bottom: 1px solid #fecaca;
}

.danger-zone .section-header i {
    color: #f87171;
    font-size: 20px;
}

.danger-zone .section-header h3 {
    color: var(--text-primary);
    font-weight: 600;
}

.danger-zone-content {
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.danger-warning {
    display: flex;
    gap: 16px;
    padding: 20px;
    background: linear-gradient(135deg, #fefce8 0%, #fef9c3 100%);
    border-radius: 12px;
    border: 1px solid #fde047;
    align-items: flex-start;
}

.danger-warning > i {
    font-size: 24px;
    color: #ca8a04;
    flex-shrink: 0;
    margin-top: 2px;
}

.danger-warning h4 {
    margin: 0 0 8px;
    color: var(--text-primary);
    font-size: 15px;
    font-weight: 600;
}

.danger-warning p {
    margin: 0;
    color: var(--text-secondary);
    font-size: 13px;
    line-height: 1.5;
}

.btn-delete-account {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%);
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    box-shadow: 0 4px 12px rgba(153, 27, 27, 0.3);
    width: fit-content;
}

.btn-delete-account:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(153, 27, 27, 0.4);
}

.btn-delete-account:active {
    transform: translateY(0);
}

/* Delete Account Modal - Refined */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(4px);
    z-index: 10000;
    display: flex;
    align-items: center;
    justify-content: center;
    animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

.modal-container {
    background: white;
    border-radius: 16px;
    width: 90%;
    max-width: 480px;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    animation: slideUp 0.3s ease;
    overflow: hidden;
}

@keyframes slideUp {
    from { transform: translateY(20px); opacity: 0; }
    to { transform: translateY(0); opacity: 1; }
}

.delete-modal .modal-header {
    padding: 20px 24px;
    background: linear-gradient(135deg, #fef2f2 0%, #fff 100%);
    border-bottom: 1px solid #e9ecef;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.delete-modal .modal-header h3 {
    margin: 0;
    display: flex;
    align-items: center;
    gap: 10px;
    color: var(--text-primary);
    font-size: 18px;
    font-weight: 600;
}

.delete-modal .modal-header h3 i {
    color: #f87171;
}

.modal-close {
    background: none;
    border: none;
    font-size: 24px;
    color: #9ca3af;
    cursor: pointer;
    padding: 0;
    line-height: 1;
    width: 32px;
    height: 32px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
}

.modal-close:hover {
    background: #f3f4f6;
    color: #374151;
}

.modal-body {
    padding: 24px;
}

.delete-warning-box {
    background: linear-gradient(135deg, #fefce8 0%, #fef9c3 100%);
    border: 1px solid #fde047;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 24px;
    text-align: center;
}

.delete-warning-box > i {
    font-size: 40px;
    color: #ca8a04;
    margin-bottom: 12px;
    display: block;
}

.delete-warning-box p {
    margin: 0 0 8px;
    color: var(--text-primary);
    font-size: 14px;
}

.delete-warning-box p strong {
    color: #92400e;
}

.delete-warning-box ul {
    text-align: left;
    margin: 16px 0 0;
    padding: 0 0 0 8px;
    list-style: none;
}

.delete-warning-box li {
    padding: 8px 0;
    color: var(--text-secondary);
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 13px;
    border-bottom: 1px solid rgba(253, 224, 71, 0.5);
}

.delete-warning-box li:last-child {
    border-bottom: none;
}

.delete-warning-box li i {
    color: #ca8a04;
    width: 20px;
}

.modal-body .form-group {
    margin-bottom: 24px;
}

.modal-body .form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: var(--text-primary);
    font-size: 14px;
}

.modal-body .password-wrapper {
    position: relative;
}

.modal-body .password-wrapper .form-control {
    padding-right: 45px;
}

.modal-body .password-toggle {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    border: none;
    background: transparent;
    color: #9ca3af;
    cursor: pointer;
    padding: 4px;
    transition: color 0.2s ease;
}

.modal-body .password-toggle:hover {
    color: var(--primary-color);
}

.modal-actions {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    padding-top: 8px;
}

.btn-cancel {
    background: white;
    color: var(--text-secondary);
    border: 1px solid #e5e7eb;
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 500;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.btn-cancel:hover {
    background: #f9fafb;
    border-color: #d1d5db;
    color: var(--text-primary);
}

.btn-delete-confirm {
    background: #dc2626;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 500;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: all 0.2s ease;
}

.btn-delete-confirm:hover {
    background: #b91c1c;
}

.text-danger {
    color: #f87171;
}
</style>

<script>
// Load and display achievements
document.addEventListener('DOMContentLoaded', function() {
    loadAchievements();
});

async function loadAchievements() {
    try {
        const response = await fetch('${pageContext.request.contextPath}/profile/achievements');
        if (!response.ok) throw new Error('Failed to load achievements');
        
        const achievements = await response.json();
        renderAchievements(achievements);
        updateSummary(achievements);
    } catch (error) {
        console.error('Error loading achievements:', error);
        document.getElementById('achievementsGrid').innerHTML = 
            '<p style="color: #666; text-align: center; grid-column: 1/-1;">Unable to load achievements</p>';
    }
}

function renderAchievements(achievements) {
    const grid = document.getElementById('achievementsGrid');
    
    if (!achievements || achievements.length === 0) {
        grid.innerHTML = '<p style="color: #666; text-align: center; grid-column: 1/-1;">No achievements available yet</p>';
        return;
    }
    
    grid.innerHTML = achievements.map(item => {
        const a = item.achievement;
        const isUnlocked = item.unlocked;
        
        return '<div class="achievement-badge ' + (isUnlocked ? 'unlocked' : 'locked') + '" ' +
               'data-name="' + escapeHtml(a.name) + '" ' +
               'data-desc="' + escapeHtml(a.description) + '" ' +
               'data-points="' + a.points + '" ' +
               'data-unlocked="' + isUnlocked + '">' +
               '<div class="badge-icon" style="background: ' + a.color + '20; color: ' + a.color + '">' +
               '<i class="bi ' + a.icon + '"></i>' +
               '</div>' +
               '<span class="badge-name">' + escapeHtml(a.name) + '</span>' +
               (isUnlocked ? '<span class="badge-checkmark"><i class="bi bi-check"></i></span>' : '') +
               '</div>';
    }).join('');
    
    // Add tooltip handlers
    document.querySelectorAll('.achievement-badge').forEach(badge => {
        badge.addEventListener('mouseenter', showTooltip);
        badge.addEventListener('mouseleave', hideTooltip);
    });
}

function updateSummary(achievements) {
    const unlockedCount = achievements.filter(a => a.unlocked).length;
    const totalCount = achievements.length;
    const totalPoints = achievements
        .filter(a => a.unlocked)
        .reduce((sum, a) => sum + a.achievement.points, 0);
    
    document.getElementById('achievementsSummary').innerHTML = 
        '<span class="badge-count">' + unlockedCount + '/' + totalCount + ' unlocked | ' + totalPoints + ' points</span>';
}

function showTooltip(e) {
    const badge = e.currentTarget;
    const tooltip = document.createElement('div');
    tooltip.className = 'achievement-tooltip';
    tooltip.id = 'achievementTooltip';
    
    const isUnlocked = badge.dataset.unlocked === 'true';
    
    tooltip.innerHTML = 
        '<div class="tooltip-title">' + badge.dataset.name + '</div>' +
        '<div class="tooltip-desc">' + badge.dataset.desc + '</div>' +
        '<div class="tooltip-points">' + 
        (isUnlocked ? '<i class="bi bi-check-circle"></i> Unlocked | ' : '<i class="bi bi-lock"></i> Locked | ') +
        badge.dataset.points + ' points</div>';
    
    document.body.appendChild(tooltip);
    
    const rect = badge.getBoundingClientRect();
    tooltip.style.left = (rect.left + rect.width / 2 - tooltip.offsetWidth / 2) + 'px';
    tooltip.style.top = (rect.bottom + 10) + 'px';
}

function hideTooltip() {
    const tooltip = document.getElementById('achievementTooltip');
    if (tooltip) tooltip.remove();
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Delete Account Modal Functions
function showDeleteModal() {
    document.getElementById('deleteAccountModal').style.display = 'flex';
    document.body.style.overflow = 'hidden';
    document.getElementById('deletePassword').value = '';
    document.getElementById('deletePassword').focus();
}

function hideDeleteModal() {
    document.getElementById('deleteAccountModal').style.display = 'none';
    document.body.style.overflow = '';
}

function toggleDeletePassword() {
    const input = document.getElementById('deletePassword');
    const icon = document.getElementById('deletePasswordIcon');
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('bi-eye');
        icon.classList.add('bi-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
    }
}

// Close modal on escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        hideDeleteModal();
    }
});

// Close modal when clicking outside
document.getElementById('deleteAccountModal').addEventListener('click', function(e) {
    if (e.target === this) {
        hideDeleteModal();
    }
});
</script>

</layout:mailLayout>
