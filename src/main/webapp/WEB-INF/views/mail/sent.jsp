<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Sent">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state animate-fade-up">
            <i class="bi bi-send"></i>
            <h5>No sent mail</h5>
            <p>Emails you send will appear here.</p>
            <a href="${pageContext.request.contextPath}/mail/compose" class="btn btn-primary">
                Compose an email
            </a>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list-container">
            <div class="mail-list">
                <c:forEach items="${page.content}" var="r" varStatus="loop">
                    <c:set var="avatarColor" value="${(loop.index % 6) + 1}" />
                    <c:set var="recipientEmail" value="${r.email.firstToRecipient}" />
                    <c:set var="initials" value="${fn:toUpperCase(fn:substring(recipientEmail, 0, 2))}" />
                    
                    <div class="mail-row sent-row" 
                         data-email-id="${r.email.id}"
                         onclick="selectEmail(this, ${r.email.id}, 'sent')">
                        
                        <div class="mail-avatar color-${avatarColor}">${not empty initials ? initials : 'TO'}</div>
                        
                        <div class="mail-content">
                            <div class="mail-sender">To: ${not empty recipientEmail ? recipientEmail : 'Recipients'}</div>
                            <div class="mail-subject">${not empty r.email.subject ? r.email.subject : '(No subject)'}</div>
                        </div>
                        
                        <div class="mail-meta">
                            <span class="mail-time">${fn:substring(r.email.createdAt, 5, 10)} ${fn:substring(r.email.createdAt, 11, 16)}</span>
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
