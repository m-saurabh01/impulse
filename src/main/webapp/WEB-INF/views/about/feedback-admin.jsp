<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Feedback Admin">

<div class="feedback-admin-container animate-fade-up">
    
    <div class="feedback-admin-header">
        <div class="header-left">
            <h2><i class="bi bi-chat-square-text-fill"></i> Feedback Dashboard</h2>
            <span class="unread-badge">${unreadCount} unread</span>
        </div>
        <a href="${pageContext.request.contextPath}/about" class="btn-outline">
            <i class="bi bi-arrow-left"></i> Back to About
        </a>
    </div>

    <!-- Filters -->
    <div class="feedback-filters">
        <a href="${pageContext.request.contextPath}/about/feedback/admin" 
           class="filter-btn ${empty selectedType ? 'active' : ''}">All</a>
        <a href="${pageContext.request.contextPath}/about/feedback/admin?type=feedback" 
           class="filter-btn ${selectedType == 'feedback' ? 'active' : ''}"><i class="bi bi-chat-dots"></i> Feedback</a>
        <a href="${pageContext.request.contextPath}/about/feedback/admin?type=suggestion" 
           class="filter-btn ${selectedType == 'suggestion' ? 'active' : ''}"><i class="bi bi-lightbulb"></i> Suggestions</a>
        <a href="${pageContext.request.contextPath}/about/feedback/admin?type=feature-request" 
           class="filter-btn ${selectedType == 'feature-request' ? 'active' : ''}"><i class="bi bi-stars"></i> Feature Requests</a>
        <a href="${pageContext.request.contextPath}/about/feedback/admin?type=bug-report" 
           class="filter-btn ${selectedType == 'bug-report' ? 'active' : ''}"><i class="bi bi-bug"></i> Bug Reports</a>
    </div>

    <!-- Feedback List -->
    <c:choose>
        <c:when test="${empty feedbackPage.content}">
            <div class="empty-feedback">
                <i class="bi bi-inbox"></i>
                <h4>No feedback yet</h4>
                <p>Feedback from users will appear here</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="feedback-list">
                <c:forEach items="${feedbackPage.content}" var="fb">
                    <div class="feedback-item ${not fb.read ? 'unread' : ''}" data-id="${fb.id}">
                        <div class="feedback-item-header">
                            <div class="feedback-meta">
                                <span class="feedback-type type-${fn:toLowerCase(fn:replace(fb.type, '_', '-'))}">
                                    <c:choose>
                                        <c:when test="${fb.type == 'FEEDBACK'}"><i class="bi bi-chat-dots"></i></c:when>
                                        <c:when test="${fb.type == 'SUGGESTION'}"><i class="bi bi-lightbulb"></i></c:when>
                                        <c:when test="${fb.type == 'FEATURE_REQUEST'}"><i class="bi bi-stars"></i></c:when>
                                        <c:when test="${fb.type == 'BUG_REPORT'}"><i class="bi bi-bug"></i></c:when>
                                    </c:choose>
                                    ${fn:replace(fb.type, '_', ' ')}
                                </span>
                                <c:if test="${not fb.read}">
                                    <span class="new-badge">NEW</span>
                                </c:if>
                            </div>
                            <span class="feedback-date">
                                ${fn:substring(fb.createdAt, 0, 10)} ${fn:substring(fb.createdAt, 11, 16)}
                            </span>
                        </div>
                        
                        <h4 class="feedback-subject">${fb.subject}</h4>
                        
                        <div class="feedback-sender">
                            <strong>${fb.name}</strong> &lt;${fb.email}&gt;
                        </div>
                        
                        <div class="feedback-message" id="message-${fb.id}">${fb.message}</div>
                        
                        <div class="feedback-actions">
                            <c:if test="${not fb.read}">
                                <button class="action-btn" onclick="markAsRead(${fb.id})" title="Mark as read">
                                    <i class="bi bi-check2"></i> Mark Read
                                </button>
                            </c:if>
                            <button class="action-btn" onclick="replyToFeedback(${fb.id}, '${fb.email}', '${fn:replace(fb.subject, "'", "\\'")}', '${fb.name}')" title="Reply">
                                <i class="bi bi-reply"></i> Reply
                            </button>
                            <button class="action-btn action-btn-danger" onclick="deleteFeedback(${fb.id})" title="Delete">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Pagination -->
            <c:if test="${feedbackPage.totalPages > 1}">
                <div class="pagination">
                    <c:if test="${feedbackPage.number > 0}">
                        <a href="?page=${feedbackPage.number - 1}${not empty selectedType ? '&type='.concat(selectedType) : ''}" class="page-btn">
                            <i class="bi bi-chevron-left"></i> Previous
                        </a>
                    </c:if>
                    <span class="page-info">Page ${feedbackPage.number + 1} of ${feedbackPage.totalPages}</span>
                    <c:if test="${feedbackPage.number < feedbackPage.totalPages - 1}">
                        <a href="?page=${feedbackPage.number + 1}${not empty selectedType ? '&type='.concat(selectedType) : ''}" class="page-btn">
                            Next <i class="bi bi-chevron-right"></i>
                        </a>
                    </c:if>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>

<style>
.feedback-admin-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 40px 24px;
}

.feedback-admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}

.header-left h2 {
    margin: 0;
    font-size: 24px;
    color: #1a1a2e;
}

.header-left h2 i {
    color: #6c5ce7;
}

.unread-badge {
    background: #e74c3c;
    color: white;
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 600;
}

.btn-outline {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    color: #495057;
    text-decoration: none;
    font-size: 14px;
    transition: all 0.2s;
}

.btn-outline:hover {
    background: #f8f9fa;
    border-color: #6c5ce7;
    color: #6c5ce7;
}

/* Filters */
.feedback-filters {
    display: flex;
    gap: 8px;
    margin-bottom: 24px;
    flex-wrap: wrap;
}

.filter-btn {
    padding: 8px 16px;
    border-radius: 20px;
    border: 1px solid #e0e0e0;
    background: white;
    color: #495057;
    text-decoration: none;
    font-size: 13px;
    transition: all 0.2s;
}

.filter-btn:hover {
    border-color: #6c5ce7;
    color: #6c5ce7;
}

.filter-btn.active {
    background: #6c5ce7;
    border-color: #6c5ce7;
    color: white;
}

/* Empty State */
.empty-feedback {
    text-align: center;
    padding: 80px 24px;
    background: #f8f9fa;
    border-radius: 16px;
}

.empty-feedback i {
    font-size: 48px;
    color: #dee2e6;
    margin-bottom: 16px;
}

.empty-feedback h4 {
    margin: 0 0 8px;
    color: #495057;
}

.empty-feedback p {
    margin: 0;
    color: #6c757d;
}

/* Feedback List */
.feedback-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.feedback-item {
    background: white;
    border-radius: 12px;
    padding: 24px;
    border: 1px solid #e0e0e0;
    transition: all 0.2s;
}

.feedback-item.unread {
    border-left: 4px solid #6c5ce7;
    background: #fafbff;
}

.feedback-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.feedback-meta {
    display: flex;
    align-items: center;
    gap: 10px;
}

.feedback-type {
    padding: 4px 10px;
    border-radius: 6px;
    font-size: 12px;
    font-weight: 500;
    text-transform: capitalize;
    background: #f0f0f0;
}

.new-badge {
    background: #e74c3c;
    color: white;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 10px;
    font-weight: 700;
}

.feedback-date {
    font-size: 12px;
    color: #6c757d;
}

.feedback-subject {
    margin: 0 0 8px;
    font-size: 18px;
    color: #1a1a2e;
}

.feedback-sender {
    font-size: 13px;
    color: #6c757d;
    margin-bottom: 16px;
}

.feedback-message {
    font-size: 14px;
    color: #495057;
    line-height: 1.7;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
    white-space: pre-wrap;
    margin-bottom: 16px;
}

.feedback-actions {
    display: flex;
    gap: 8px;
}

.action-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 6px 12px;
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    background: white;
    color: #495057;
    font-size: 12px;
    cursor: pointer;
    text-decoration: none;
    transition: all 0.2s;
}

.action-btn:hover {
    background: #f0f0f0;
}

.action-btn-danger:hover {
    background: #fee2e2;
    border-color: #fca5a5;
    color: #dc2626;
}

/* Pagination */
.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 16px;
    margin-top: 32px;
}

.page-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 8px 16px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    color: #495057;
    text-decoration: none;
    font-size: 13px;
}

.page-btn:hover {
    background: #f0f0f0;
}

.page-info {
    font-size: 13px;
    color: #6c757d;
}
</style>

<script>
// csrfToken and csrfHeader are already defined in mailLayout.tag
var feedbackContextPath = '${pageContext.request.contextPath}';

function markAsRead(id) {
    var headers = { "X-Requested-With": "XMLHttpRequest" };
    headers[csrfHeader] = csrfToken;
    
    fetch(feedbackContextPath + '/about/feedback/markRead?id=' + id, {
        method: 'POST',
        headers: headers
    })
    .then(function(res) {
        if (res.ok) {
            var item = document.querySelector('.feedback-item[data-id="' + id + '"]');
            if (item) {
                item.classList.remove('unread');
                var newBadge = item.querySelector('.new-badge');
                if (newBadge) newBadge.remove();
                var markBtn = item.querySelector('.action-btn');
                if (markBtn && markBtn.textContent.includes('Mark Read')) {
                    markBtn.remove();
                }
            }
        }
    });
}

function deleteFeedback(id) {
    if (!confirm('Delete this feedback?')) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest" };
    headers[csrfHeader] = csrfToken;
    
    fetch(feedbackContextPath + '/about/feedback/delete?id=' + id, {
        method: 'POST',
        headers: headers
    })
    .then(function(res) {
        if (res.ok) {
            var item = document.querySelector('.feedback-item[data-id="' + id + '"]');
            if (item) {
                item.style.opacity = '0';
                item.style.transform = 'translateX(-20px)';
                setTimeout(function() { item.remove(); }, 300);
            }
        }
    });
}

function replyToFeedback(id, email, subject, name) {
    var messageEl = document.getElementById('message-' + id);
    var originalMessage = messageEl ? messageEl.textContent.trim() : '';
    
    // Build the quoted reply body
    var body = '<br><br>' +
        '<div style="border-left: 3px solid #6c5ce7; padding-left: 12px; margin-top: 20px; color: #666;">' +
        '<p style="margin: 0 0 8px 0;"><strong>Original feedback from ' + name + ':</strong></p>' +
        '<p style="margin: 0; white-space: pre-wrap;">' + originalMessage.replace(/</g, '&lt;').replace(/>/g, '&gt;') + '</p>' +
        '</div>';
    
    // Navigate to compose with parameters
    var url = feedbackContextPath + '/mail/compose' +
        '?to=' + encodeURIComponent(email) +
        '&subject=' + encodeURIComponent('Re: ' + subject) +
        '&body=' + encodeURIComponent(body);
    
    window.location.href = url;
}
</script>

</layout:mailLayout>
