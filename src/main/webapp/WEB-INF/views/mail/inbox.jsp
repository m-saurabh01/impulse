<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Inbox">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state animate-fade-up">
            <i class="bi bi-inbox"></i>
            <h5>Your inbox is empty</h5>
            <p>New emails will appear here.</p>
            <a href="${pageContext.request.contextPath}/mail/compose" class="btn btn-primary">
                Compose your first email
            </a>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list-container">
            <div class="mail-list">
                <!-- Search Results Banner -->
                <c:if test="${not empty searchQuery}">
                    <div class="search-results-banner">
                        <span>Showing results for "<strong>${searchQuery}</strong>" (${page.totalElements} found)</span>
                        <a href="${pageContext.request.contextPath}/mail/inbox" class="btn btn-sm btn-outline-secondary">
                            Clear Search
                        </a>
                    </div>
                </c:if>
                
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
                        <button class="bulk-btn" onclick="bulkMarkRead()" title="Mark as read">
                            <i class="bi bi-envelope-open"></i>
                        </button>
                        <button class="bulk-btn bulk-btn-danger" onclick="bulkDelete()" title="Delete">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
                
                <c:forEach items="${page.content}" var="r" varStatus="loop">
                    <c:set var="avatarColor" value="${(loop.index % 6) + 1}" />
                    <c:set var="initials" value="${fn:toUpperCase(fn:substring(r.email.sender.email, 0, 2))}" />
                    
                    <div class="mail-row ${not r.read ? 'unread' : ''}" 
                         data-email-id="${r.email.id}">
                        
                        <label class="mail-checkbox" onclick="event.stopPropagation()">
                            <input type="checkbox" class="email-checkbox" data-email-id="${r.email.id}" onchange="updateBulkSelection()">
                            <span class="checkmark"></span>
                        </label>
                        
                        <div class="mail-row-content" onclick="selectEmail(this.parentElement, ${r.email.id}, 'inbox')">
                            <div class="mail-avatar color-${avatarColor}">${initials}</div>
                            
                            <div class="mail-content">
                                <div class="mail-sender">${r.email.sender.email}</div>
                                <div class="mail-subject">${not empty r.email.subject ? r.email.subject : '(No subject)'}</div>
                            </div>
                            
                            <div class="mail-meta">
                                <c:set var="monthNum" value="${fn:substring(r.email.createdAt, 5, 7)}" />
                                <span class="mail-time">
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
                                    ${fn:substring(r.email.createdAt, 8, 10)}
                                </span>
                                <c:if test="${not r.read}">
                                    <span class="unread-dot"></span>
                                </c:if>
                                <button class="mail-actions-btn" onclick="event.stopPropagation(); toggleMailMenu(this, ${r.email.id}, 'inbox', ${r.starred})" title="More actions">
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
