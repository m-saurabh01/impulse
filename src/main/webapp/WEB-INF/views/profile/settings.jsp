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
</script>

</layout:mailLayout>
