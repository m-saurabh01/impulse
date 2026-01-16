<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Snoozed">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state animate-fade-up">
            <i class="bi bi-clock"></i>
            <h5>No snoozed emails</h5>
            <p>Snoozed emails will appear here until their wake-up time.</p>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list-container">
            <div class="mail-list">
                <div class="folder-header">
                    <i class="bi bi-clock"></i>
                    <span>Snoozed Emails</span>
                    <span class="header-count-badge">${page.totalElements}</span>
                </div>
                
                <c:forEach items="${page.content}" var="r" varStatus="loop">
                    <c:set var="avatarColor" value="${(loop.index % 6) + 1}" />
                    <c:set var="initials" value="${fn:toUpperCase(fn:substring(r.email.sender.email, 0, 2))}" />
                    
                    <div class="mail-row ${not r.read ? 'unread' : ''}" 
                         data-email-id="${r.email.id}">
                        
                        <div class="mail-row-content" onclick="selectEmail(this.parentElement, ${r.email.id}, 'snoozed')">
                            <div class="mail-avatar color-${avatarColor}">${initials}</div>
                            
                            <div class="mail-content">
                                <div class="mail-sender">${r.email.sender.email}</div>
                                <div class="mail-subject">${not empty r.email.subject ? r.email.subject : '(No subject)'}</div>
                                <div class="snooze-badge">
                                    <i class="bi bi-clock"></i>
                                    <span class="snooze-time" data-time="${r.snoozedUntil}">
                                        Snoozed until 
                                        <c:set var="monthNum" value="${fn:substring(r.snoozedUntil, 5, 7)}" />
                                        <c:choose>
                                            <c:when test="${monthNum == '01'}">Jan</c:when>
                                            <c:when test="${monthNum == '02'}">Feb</c:when>
                                            <c:when test="${monthNum == '03'}">Mar</c:when>
                                            <c:when test="${monthNum == '04'}">Apr</c:when>
                                            <c:when test="${monthNum == '05'}">May</c:when>
                                            <c:when test="${monthNum == '06'}">Jun</c:when>
                                            <c:when test="${monthNum == '07'}">Jul</c:when>
                                            <c:when test="${monthNum == '08'}">Aug</c:when>
                                            <c:when test="${monthNum == '09'}">Sep</c:when>
                                            <c:when test="${monthNum == '10'}">Oct</c:when>
                                            <c:when test="${monthNum == '11'}">Nov</c:when>
                                            <c:when test="${monthNum == '12'}">Dec</c:when>
                                            <c:otherwise>${monthNum}</c:otherwise>
                                        </c:choose>
                                        ${fn:substring(r.snoozedUntil, 8, 10)}, 
                                        ${fn:substring(r.snoozedUntil, 11, 16)}
                                    </span>
                                </div>
                            </div>
                            
                            <div class="mail-meta">
                                <button class="mail-actions-btn unsnooze-btn" 
                                        onclick="event.stopPropagation(); unsnoozeEmail(${r.email.id})" 
                                        title="Unsnooze">
                                    <i class="bi bi-bell"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <div class="preview-pane" id="previewPane">
                <div class="preview-empty">
                    <i class="bi bi-clock"></i>
                    <h5>Select an email to read</h5>
                    <p>These emails are snoozed and will return to your inbox at the scheduled time.</p>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<style>
.folder-header {
    padding: 12px 16px;
    background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 100%);
    color: white;
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    border-radius: 8px 8px 0 0;
}

.folder-header .email-count {
    opacity: 0.8;
    font-weight: 400;
    margin-left: auto;
}

.folder-header .header-count-badge {
    margin-left: auto;
    background: rgba(255, 255, 255, 0.25);
    padding: 2px 10px;
    border-radius: 12px;
    font-size: 0.8rem;
    font-weight: 500;
}

.snooze-badge {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    background: linear-gradient(135deg, #6c5ce7 0%, #5b4cdb 100%);
    color: white;
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 0.75rem;
    margin-top: 4px;
}

.snooze-badge i {
    font-size: 0.7rem;
}

.unsnooze-btn {
    background: rgba(108, 92, 231, 0.1) !important;
    color: #6c5ce7 !important;
    border-radius: 50%;
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
}

.unsnooze-btn:hover {
    background: #6c5ce7 !important;
    color: white !important;
    transform: scale(1.1);
}
</style>

<script>
async function unsnoozeEmail(emailId) {
    try {
        const response = await fetch('${pageContext.request.contextPath}/mail/unsnooze?emailId=' + emailId, {
            method: 'POST',
            headers: {
                '${_csrf.headerName}': '${_csrf.token}'
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            // Remove the row with animation
            const row = document.querySelector('[data-email-id="' + emailId + '"]');
            if (row) {
                row.style.transition = 'all 0.3s ease';
                row.style.transform = 'translateX(100%)';
                row.style.opacity = '0';
                setTimeout(() => {
                    row.remove();
                    // Reload if no more snoozed emails
                    if (document.querySelectorAll('.mail-row').length === 0) {
                        location.reload();
                    }
                }, 300);
            }
            
            showToast('Email unsnoozed', 'success');
        } else {
            showToast(data.error || 'Failed to unsnooze', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Failed to unsnooze email', 'error');
    }
}

function showToast(message, type) {
    const toast = document.createElement('div');
    toast.className = 'toast-notification ' + type;
    toast.innerHTML = '<i class="bi bi-' + (type === 'success' ? 'check-circle' : 'exclamation-circle') + '"></i>' + message;
    document.body.appendChild(toast);
    
    setTimeout(() => toast.classList.add('show'), 10);
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
</script>

</layout:mailLayout>
