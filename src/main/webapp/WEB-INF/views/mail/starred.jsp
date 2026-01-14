<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Starred">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state animate-fade-up">
            <i class="bi bi-star" style="color: #ffc107;"></i>
            <h5>No starred emails</h5>
            <p>Star important emails to find them easily here.</p>
            <a href="${pageContext.request.contextPath}/mail/inbox" class="btn btn-primary">
                Go to Inbox
            </a>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list-container">
            <div class="mail-list">
                <c:forEach items="${page.content}" var="r" varStatus="loop">
                    <c:set var="avatarColor" value="${(loop.index % 6) + 1}" />
                    <c:set var="initials" value="${fn:toUpperCase(fn:substring(r.email.sender.email, 0, 2))}" />
                    
                    <div class="mail-row ${not r.read ? 'unread' : ''}" 
                         data-email-id="${r.email.id}">
                        
                        <div class="mail-row-content" onclick="selectEmail(this.parentElement, ${r.email.id}, 'starred')">
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
                                <button class="mail-actions-btn" onclick="event.stopPropagation(); toggleMailMenu(this, ${r.email.id}, 'starred', true)" title="More actions">
                                    <i class="bi bi-three-dots-vertical"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <div class="preview-pane" id="previewPane">
                <div class="preview-empty">
                    <i class="bi bi-star" style="color: #ffc107;"></i>
                    <h5>Select an item to read</h5>
                    <p>Nothing is selected</p>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

</layout:mailLayout>
