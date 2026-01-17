<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="preview-header">
    <div class="preview-actions-row">
        <div class="preview-actions">
            <%-- Star toggle - show for inbox/sent/starred --%>
            <c:if test="${source == 'inbox' || source == 'sent' || source == 'starred'}">
                <button class="action-btn action-btn-star ${recipient.starred ? 'starred' : ''}" 
                        onclick="toggleStarPreview(${email.id}, this)" 
                        title="${recipient.starred ? 'Remove from starred' : 'Add to starred'}" 
                        id="previewStarBtn">
                    <i class="bi ${recipient.starred ? 'bi-star-fill' : 'bi-star'}"></i>
                    <span>${recipient.starred ? 'Starred' : 'Star'}</span>
                </button>
            </c:if>
            <%-- Reply buttons - only show for inbox/sent (not trash or draft) and not for deleted users --%>
            <c:if test="${source == 'inbox' || source == 'sent'}">
                <c:choose>
                    <c:when test="${email.sender.deleted}">
                        <button class="action-btn action-btn-disabled" disabled title="Cannot reply - this user no longer exists">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="9 17 4 12 9 7"></polyline>
                                <path d="M20 18v-2a4 4 0 0 0-4-4H4"></path>
                            </svg>
                            <span>Reply unavailable</span>
                        </button>
                    </c:when>
                    <c:otherwise>
                        <button class="action-btn action-btn-primary" onclick="replyToEmail(${email.id}, false)" title="Reply">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="9 17 4 12 9 7"></polyline>
                                <path d="M20 18v-2a4 4 0 0 0-4-4H4"></path>
                            </svg>
                            <span>Reply</span>
                        </button>
                        <button class="action-btn action-btn-secondary" onclick="replyToEmail(${email.id}, true)" title="Reply All">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="9 17 4 12 9 7"></polyline>
                                <polyline points="15 17 10 12 15 7"></polyline>
                                <path d="M20 18v-2a4 4 0 0 0-4-4H10"></path>
                            </svg>
                            <span>Reply All</span>
                        </button>
                    </c:otherwise>
                </c:choose>
                <button class="action-btn action-btn-secondary" onclick="forwardEmail(${email.id})" title="Forward">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="15 17 20 12 15 7"></polyline>
                        <path d="M4 18v-2a4 4 0 0 1 4-4h12"></path>
                    </svg>
                    <span>Forward</span>
                </button>
            </c:if>
            <c:if test="${source != 'trash'}">
                <button class="action-btn action-btn-danger" onclick="moveToTrash(${email.id})" title="Move to trash">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        <line x1="10" y1="11" x2="10" y2="17"></line>
                        <line x1="14" y1="11" x2="14" y2="17"></line>
                    </svg>
                    <span>Delete</span>
                </button>
            </c:if>
            <c:if test="${source == 'trash'}">
                <button class="action-btn action-btn-success" onclick="restoreFromTrash(${email.id})" title="Restore from trash">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
                        <path d="M3 3v5h5"></path>
                    </svg>
                    <span>Restore</span>
                </button>
                <button class="action-btn action-btn-danger" onclick="permanentDelete(${email.id})" title="Delete permanently">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        <line x1="10" y1="11" x2="10" y2="17"></line>
                        <line x1="14" y1="11" x2="14" y2="17"></line>
                    </svg>
                    <span>Delete Forever</span>
                </button>
            </c:if>
        </div>
    </div>
    
    <h2 class="preview-subject">${not empty email.subject ? email.subject : '(No subject)'}</h2>
    
    <div class="preview-sender-row">
        <div class="preview-avatar ${email.sender.deleted ? 'deleted-user' : ''}">
            <c:choose>
                <c:when test="${email.sender.deleted}">
                    <i class="bi bi-person-x"></i>
                </c:when>
                <c:otherwise>
                    ${fn:toUpperCase(fn:substring(email.sender.email, 0, 1))}
                </c:otherwise>
            </c:choose>
        </div>
        <div class="preview-sender-info">
            <div class="preview-sender-name">
                ${email.sender.email}
                <c:if test="${email.sender.deleted}">
                    <span class="deleted-badge"><i class="bi bi-exclamation-circle"></i> Account deleted</span>
                </c:if>
            </div>
            <div class="preview-recipients">
                <c:if test="${not empty email.toRecipients}">
                    <div class="preview-recipient-line"><span class="recipient-label">To:</span> ${email.toRecipients}</div>
                </c:if>
                <c:if test="${not empty email.ccRecipients}">
                    <div class="preview-recipient-line"><span class="recipient-label">Cc:</span> ${email.ccRecipients}</div>
                </c:if>
                <%-- BCC only visible to the sender --%>
                <c:if test="${not empty email.bccRecipients && email.sender.email == sessionScope.userEmail}">
                    <div class="preview-recipient-line"><span class="recipient-label">Bcc:</span> ${email.bccRecipients}</div>
                </c:if>
            </div>
        </div>
        <div class="preview-date">
            <c:set var="monthNum" value="${fn:substring(email.createdAt, 5, 7)}" />
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
            ${fn:substring(email.createdAt, 8, 10)}, ${fn:substring(email.createdAt, 0, 4)} at ${fn:substring(email.createdAt, 11, 16)}
        </div>
    </div>
</div>

<div class="preview-body">
    <div class="preview-body-content">
        ${email.bodyHtml}
    </div>
</div>

<c:if test="${not empty attachments}">
    <div class="preview-attachments">
        <div class="attachments-header">
            <svg class="attachments-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="m21.44 11.05-9.19 9.19a6 6 0 0 1-8.49-8.49l8.57-8.57A4 4 0 1 1 18 8.84l-8.59 8.57a2 2 0 0 1-2.83-2.83l8.49-8.48"></path>
            </svg>
            <span>Attachments (${fn:length(attachments)})</span>
        </div>
        <div class="attachments-list">
            <c:forEach items="${attachments}" var="a">
                <a href="${pageContext.request.contextPath}/attachments/${a.id}" class="attachment-item" title="${a.originalFilename}">
                    <div class="attachment-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                            <polyline points="14 2 14 8 20 8"></polyline>
                        </svg>
                    </div>
                    <div class="attachment-info">
                        <span class="attachment-name">${a.originalFilename}</span>
                        <span class="attachment-size">${a.formattedSize}</span>
                    </div>
                </a>
            </c:forEach>
        </div>
    </div>
</c:if>

<%-- Conversation Thread / Email History --%>
<c:if test="${not empty threadEmails}">
    <div class="conversation-thread">
        <div class="thread-header" onclick="toggleThread()">
            <svg class="thread-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            <span>${fn:length(threadEmails)} earlier message<c:if test="${fn:length(threadEmails) > 1}">s</c:if> in this conversation</span>
            <svg class="thread-chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
        </div>
        <div class="thread-messages" id="threadMessages" style="display: none;">
            <c:forEach items="${threadEmails}" var="threadEmail" varStatus="loop">
                <div class="thread-message">
                    <div class="thread-message-header">
                        <div class="thread-avatar ${threadEmail.sender.deleted ? 'deleted-user' : ''}">
                            <c:choose>
                                <c:when test="${threadEmail.sender.deleted}">
                                    <i class="bi bi-person-x"></i>
                                </c:when>
                                <c:otherwise>
                                    ${fn:toUpperCase(fn:substring(threadEmail.sender.email, 0, 1))}
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="thread-sender-info">
                            <span class="thread-sender">${threadEmail.sender.email}</span>
                            <span class="thread-date">${fn:substring(threadEmail.createdAt, 0, 16)}</span>
                        </div>
                    </div>
                    <div class="thread-message-body">
                        ${threadEmail.bodyHtml}
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</c:if>
