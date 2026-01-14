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
            <span class="nav-badge" id="inboxUnreadBadge" style="${inboxUnreadCount > 0 ? '' : 'display:none;'}">${inboxUnreadCount}</span>
        </a>
        
        <a class="nav-item ${currentFolder == 'starred' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/mail/starred">
            <i class="bi bi-star-fill" style="color: ${currentFolder == 'starred' ? '#ffc107' : 'inherit'};"></i>
            <span class="nav-label">Starred</span>
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
        
        <div class="nav-divider"></div>
        
        <a class="nav-item ${currentFolder == 'contacts' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/contacts">
            <i class="bi bi-people-fill"></i>
            <span class="nav-label">Contacts</span>
        </a>
        
        <div class="nav-divider"></div>
        
        <a class="nav-item ${currentFolder == 'about' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/about">
            <i class="bi bi-info-circle-fill"></i>
            <span class="nav-label">About Impulse</span>
        </a>
    </nav>
    
    <!-- Labels Section -->
    <div class="sidebar-labels" id="sidebarLabels">
        <div class="sidebar-labels-header">
            <span>Labels</span>
            <a href="${pageContext.request.contextPath}/mail/labels/manage" title="Manage Labels">
                <i class="bi bi-plus-lg"></i>
            </a>
        </div>
        <div id="labelsNav">
            <!-- Labels loaded via JavaScript -->
        </div>
    </div>
</aside>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Load labels for sidebar
    fetch('${pageContext.request.contextPath}/mail/labels/list')
        .then(function(res) { return res.json(); })
        .then(function(labels) {
            var container = document.getElementById('labelsNav');
            if (!container) return;
            
            if (labels.length === 0) {
                container.innerHTML = '<a class="nav-item-label" href="${pageContext.request.contextPath}/mail/labels/manage"><i class="bi bi-plus"></i> Create label</a>';
                return;
            }
            
            var html = '';
            labels.forEach(function(label) {
                var isActive = '${currentFolder}' === 'label-' + label.id;
                html += '<a class="nav-item-label' + (isActive ? ' active' : '') + '" href="${pageContext.request.contextPath}/mail/labels/view/' + label.id + '">' +
                        '<span class="label-dot" style="background: ' + label.color + ';"></span>' +
                        label.name + '</a>';
            });
            container.innerHTML = html;
        })
        .catch(function(err) {
            console.error('Failed to load labels:', err);
        });
    
    // Update unread count badge
    function updateUnreadCount() {
        fetch('${pageContext.request.contextPath}/mail/unreadCount')
            .then(function(res) { return res.json(); })
            .then(function(count) {
                var badge = document.getElementById('inboxUnreadBadge');
                if (badge) {
                    badge.textContent = count;
                    badge.style.display = count > 0 ? '' : 'none';
                }
            })
            .catch(function(err) {
                console.error('Failed to update unread count:', err);
            });
    }
    
    // Refresh unread count every 30 seconds
    setInterval(updateUnreadCount, 30000);
    
    // Also expose function globally so it can be called after marking email as read
    window.updateInboxUnreadCount = updateUnreadCount;
});
</script>

