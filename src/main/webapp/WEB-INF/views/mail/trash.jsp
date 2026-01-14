<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Trash">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state animate-fade-up">
            <i class="bi bi-trash3"></i>
            <h5>Trash is empty</h5>
            <p>Deleted emails will appear here.</p>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list-container">
            <div class="mail-list">
                <!-- Empty Trash Row -->
                <div class="empty-trash-row" onclick="emptyTrash()">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                    <span>Empty Trash</span>
                    <span class="trash-count">${page.totalElements}</span>
                </div>
                
                <c:forEach items="${page.content}" var="r" varStatus="loop">
                    <c:set var="avatarColor" value="${(loop.index % 6) + 1}" />
                    <c:set var="initials" value="${fn:toUpperCase(fn:substring(r.email.sender.email, 0, 2))}" />
                    
                    <div class="mail-row" 
                         data-email-id="${r.email.id}"
                         onclick="selectEmail(this, ${r.email.id}, 'trash')">
                        
                        <div class="mail-avatar color-${avatarColor}">${initials}</div>
                        
                        <div class="mail-content">
                            <div class="mail-sender">${r.email.sender.email}</div>
                            <div class="mail-subject">${not empty r.email.subject ? r.email.subject : '(No subject)'}</div>
                        </div>
                        
                        <div class="mail-meta">
                            <span class="mail-time">${fn:substring(r.email.createdAt, 5, 10)}</span>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <div class="preview-pane" id="previewPane">
                <div class="preview-empty">
                    <i class="bi bi-envelope-open"></i>
                    <h5>Select an item to read</h5>
                    <p>Nothing is selected</p>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

</layout:mailLayout>
