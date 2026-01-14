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
                <!-- Bulk Actions Toolbar -->
                <div class="bulk-toolbar" id="bulkToolbar" style="display: none;">
                    <div class="bulk-toolbar-left">
                        <label class="select-all-container">
                            <input type="checkbox" id="selectAllCheckbox" onchange="toggleSelectAll()">
                            <span class="checkmark"></span>
                        </label>
                        <span class="bulk-count"><span id="selectedCount">0</span> selected</span>
                    </div>
                    <div class="bulk-toolbar-actions">
                        <button class="bulk-btn bulk-btn-danger" onclick="bulkDelete()" title="Delete">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
                
                <c:forEach items="${page.content}" var="r" varStatus="loop">
                    <c:set var="avatarColor" value="${(loop.index % 6) + 1}" />
                    <c:set var="recipientEmail" value="${r.email.firstToRecipient}" />
                    <c:set var="initials" value="${fn:toUpperCase(fn:substring(recipientEmail, 0, 2))}" />
                    <c:set var="ccCount" value="${fn:length(fn:split(r.email.ccRecipients, ','))}" />
                    <c:set var="hasCc" value="${not empty r.email.ccRecipients}" />
                    <c:set var="hasBcc" value="${not empty r.email.bccRecipients}" />
                    
                    <div class="mail-row sent-row" 
                         data-email-id="${r.email.id}">
                        
                        <label class="mail-checkbox" onclick="event.stopPropagation()">
                            <input type="checkbox" class="email-checkbox" data-email-id="${r.email.id}" onchange="updateBulkSelection()">
                            <span class="checkmark"></span>
                        </label>
                        
                        <div class="mail-row-content" onclick="selectEmail(this.parentElement, ${r.email.id}, 'sent')">
                            <div class="mail-avatar color-${avatarColor}">${not empty initials ? initials : 'TO'}</div>
                            
                            <div class="mail-content">
                                <div class="mail-sender">
                                    To: ${not empty recipientEmail ? recipientEmail : 'Recipients'}
                                    <c:if test="${hasCc}">
                                        <span class="recipient-badge cc-badge" title="CC: ${r.email.ccRecipients}">+CC</span>
                                    </c:if>
                                    <c:if test="${hasBcc}">
                                        <span class="recipient-badge bcc-badge" title="BCC: ${r.email.bccRecipients}">+BCC</span>
                                    </c:if>
                                </div>
                                <div class="mail-subject">${not empty r.email.subject ? r.email.subject : '(No subject)'}</div>
                            </div>
                            
                            <div class="mail-meta">
                                <span class="mail-time">${fn:substring(r.email.createdAt, 5, 10)} ${fn:substring(r.email.createdAt, 11, 16)}</span>
                                <button class="mail-actions-btn" onclick="event.stopPropagation(); toggleMailMenu(this, ${r.email.id}, 'sent', ${r.starred})" title="More actions">
                                    <i class="bi bi-three-dots-vertical"></i>
                                </button>
                            </div>
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
