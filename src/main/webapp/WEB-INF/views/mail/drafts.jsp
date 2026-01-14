<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Drafts">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state animate-fade-up">
            <i class="bi bi-file-earmark-text"></i>
            <h5>No drafts</h5>
            <p>Draft emails will appear here.</p>
            <a href="${pageContext.request.contextPath}/mail/compose" class="btn btn-primary">
                Compose an email
            </a>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list-container">
            <div class="mail-list">
                <!-- Bulk Selection Toolbar -->
                <div class="bulk-toolbar" id="bulkToolbar" style="display: none;">
                    <div class="bulk-toolbar-left">
                        <label class="select-all-container" onclick="event.stopPropagation()">
                            <input type="checkbox" id="selectAllCheckbox" onchange="toggleSelectAll()">
                            <span class="checkmark"></span>
                        </label>
                        <span class="bulk-count"><span id="selectedCount">0</span> selected</span>
                    </div>
                    <div class="bulk-toolbar-actions">
                        <button class="bulk-btn bulk-btn-danger" onclick="bulkDeleteDrafts()" title="Delete selected drafts">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
                
                <c:forEach items="${page.content}" var="email" varStatus="loop">
                    <c:set var="avatarColor" value="${(loop.index % 6) + 1}" />
                    
                    <div class="mail-row" 
                         data-email-id="${email.id}">
                        
                        <label class="mail-checkbox" onclick="event.stopPropagation()">
                            <input type="checkbox" class="email-checkbox" data-email-id="${email.id}" onchange="updateBulkSelection()">
                            <span class="checkmark"></span>
                        </label>
                        
                        <div class="mail-row-content" onclick="window.location.href='${pageContext.request.contextPath}/mail/compose?id=${email.id}'">
                            <div class="mail-avatar color-4">
                                <i class="bi bi-pencil" style="font-size:14px;"></i>
                            </div>
                            
                            <div class="mail-content">
                                <div class="mail-sender" style="color:#e81123;">[Draft]</div>
                                <div class="mail-subject">${not empty email.subject ? email.subject : '(No subject)'}</div>
                            </div>
                            
                            <div class="mail-meta">
                                <span class="mail-time">${fn:substring(email.createdAt, 5, 10)}</span>
                                <button class="mail-actions-btn" onclick="event.stopPropagation(); toggleMailMenu(this, ${email.id}, 'drafts', false)" title="More actions">
                                    <i class="bi bi-three-dots-vertical"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:otherwise>
</c:choose>

</layout:mailLayout>
