<%@ tag pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<aside class="sidebar">
    <!-- Compose Button -->
    <a href="${pageContext.request.contextPath}/mail/compose" class="compose-btn">
        <i class="bi bi-pencil-square"></i>
        <span>New email</span>
    </a>
    
    <!-- Navigation -->
    <nav class="sidebar-nav">
        <a class="nav-item ${currentFolder == 'inbox' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/mail/inbox">
            <i class="bi bi-inbox-fill"></i>
            <span class="nav-label">Inbox</span>
            <c:if test="${unreadCount > 0}">
                <span class="nav-badge">${unreadCount}</span>
            </c:if>
        </a>
        
        <a class="nav-item ${currentFolder == 'drafts' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/mail/drafts">
            <i class="bi bi-file-earmark-text"></i>
            <span class="nav-label">Drafts</span>
            <c:if test="${draftCount > 0}">
                <span class="nav-badge">${draftCount}</span>
            </c:if>
        </a>
        
        <a class="nav-item ${currentFolder == 'sent' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/mail/sent">
            <i class="bi bi-send-fill"></i>
            <span class="nav-label">Sent Items</span>
        </a>
        
        <a class="nav-item ${currentFolder == 'trash' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/mail/trash">
            <i class="bi bi-trash3-fill"></i>
            <span class="nav-label">Deleted Items</span>
            <c:if test="${trashCount > 0}">
                <span class="nav-badge">${trashCount}</span>
            </c:if>
        </a>
    </nav>
</aside>
