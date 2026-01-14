<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="preview-header">
    <div class="preview-header-top">
        <h2 class="preview-subject">${not empty email.subject ? email.subject : '(No subject)'}</h2>
        <div class="preview-actions">
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
    
    <div class="preview-sender-row">
        <div class="preview-avatar">
            ${fn:toUpperCase(fn:substring(email.sender.email, 0, 1))}
        </div>
        <div class="preview-sender-info">
            <div class="preview-sender-name">${email.sender.email}</div>
            <div class="preview-recipients">
                <c:if test="${not empty email.toRecipients}">
                    <div class="preview-recipient-line"><span class="recipient-label">To:</span> ${email.toRecipients}</div>
                </c:if>
                <c:if test="${not empty email.ccRecipients}">
                    <div class="preview-recipient-line"><span class="recipient-label">Cc:</span> ${email.ccRecipients}</div>
                </c:if>
                <c:if test="${not empty email.bccRecipients}">
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
