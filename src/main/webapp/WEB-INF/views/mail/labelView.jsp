<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="${label.name}">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state animate-fade-up">
            <i class="bi bi-tag" style="color: ${label.color};"></i>
            <h5>No emails with this label</h5>
            <p>Emails labeled "${label.name}" will appear here.</p>
            <a href="${pageContext.request.contextPath}/mail/inbox" class="btn btn-primary">
                Go to Inbox
            </a>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list-container">
            <div class="mail-list">
                <!-- Label Header -->
                <div class="label-header" style="border-left: 4px solid ${label.color};">
                    <div class="label-header-info">
                        <span class="label-color-dot" style="background: ${label.color};"></span>
                        <span class="label-header-name">${label.name}</span>
                        <span class="label-header-count">${page.totalElements} email${page.totalElements != 1 ? 's' : ''}</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/mail/labels/manage" class="label-manage-link">
                        <i class="bi bi-gear"></i> Manage Labels
                    </a>
                </div>
                
                <c:forEach items="${page.content}" var="el" varStatus="loop">
                    <c:set var="email" value="${el.email}" />
                    <c:set var="avatarColor" value="${(loop.index % 6) + 1}" />
                    <c:set var="initials" value="${fn:toUpperCase(fn:substring(email.sender.email, 0, 2))}" />
                    
                    <div class="mail-row" data-email-id="${email.id}">
                        
                        <label class="mail-checkbox" onclick="event.stopPropagation()">
                            <input type="checkbox" class="email-checkbox" data-email-id="${email.id}" onchange="updateBulkSelection()">
                            <span class="checkmark"></span>
                        </label>
                        
                        <div class="mail-row-content" onclick="selectEmail(this.parentElement, ${email.id}, 'label')">
                            <div class="mail-avatar color-${avatarColor}">${initials}</div>
                            
                            <div class="mail-content">
                                <div class="mail-sender">${email.sender.email}</div>
                                <div class="mail-subject">${not empty email.subject ? email.subject : '(No subject)'}</div>
                            </div>
                            
                            <div class="mail-meta">
                                <c:set var="monthNum" value="${fn:substring(email.createdAt, 5, 7)}" />
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
                                    ${fn:substring(email.createdAt, 8, 10)}
                                </span>
                                <button class="mail-actions-btn" onclick="event.stopPropagation(); toggleMailMenu(this, ${email.id}, 'label', false)" title="More actions">
                                    <i class="bi bi-three-dots-vertical"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <div class="preview-pane" id="previewPane">
                <div class="preview-empty">
                    <i class="bi bi-tag" style="color: ${label.color};"></i>
                    <h5>Select an item to read</h5>
                    <p>Nothing is selected</p>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

</layout:mailLayout>
<script>
// Store the current label ID for remove-from-label functionality
window.currentLabelId = ${label.id};
</script>