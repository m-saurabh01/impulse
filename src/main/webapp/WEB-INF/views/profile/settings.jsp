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
</style>

</layout:mailLayout>
