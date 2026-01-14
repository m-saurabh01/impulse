<%@ tag pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<header class="app-header">
    <div class="brand">
        <img src="${pageContext.request.contextPath}/assets/icons/new_icon.png" alt="Impulse" class="brand-logo">
        <span>Impulse</span>
    </div>
    
    <div class="search-container">
        <form action="${pageContext.request.contextPath}/mail/inbox" method="get" class="search-form">
            <div class="search-wrapper">
                <i class="bi bi-search search-icon"></i>
                <input type="text" class="search-box" placeholder="Search emails (subject, sender, content)" 
                       id="searchInput" name="q" value="${searchQuery}"/>
                <c:if test="${not empty searchQuery}">
                    <a href="${pageContext.request.contextPath}/mail/inbox" class="search-clear" title="Clear search">
                        <i class="bi bi-x-circle"></i>
                    </a>
                </c:if>
            </div>
        </form>
    </div>
    
    <div class="header-actions">
        <!-- User Dropdown -->
        <div class="user-dropdown">
            <button class="user-dropdown-toggle" onclick="toggleUserDropdown(event)">
                <div class="user-avatar-header">
                    <c:choose>
                        <c:when test="${not empty sessionScope.userEmail}">
                            ${fn:toUpperCase(fn:substring(sessionScope.userEmail, 0, 1))}
                        </c:when>
                        <c:otherwise>U</c:otherwise>
                    </c:choose>
                </div>
                <div class="user-info-header">
                    <span class="user-name-header">
                        <c:choose>
                            <c:when test="${not empty sessionScope.userDisplayName}">
                                ${sessionScope.userDisplayName}
                            </c:when>
                            <c:when test="${not empty sessionScope.userEmail}">
                                ${fn:substringBefore(sessionScope.userEmail, '@')}
                            </c:when>
                            <c:otherwise>User</c:otherwise>
                        </c:choose>
                    </span>
                    <span class="user-email-header">${sessionScope.userEmail}</span>
                </div>
                <i class="bi bi-chevron-down dropdown-arrow"></i>
            </button>
            
            <div class="user-dropdown-menu" id="userDropdownMenu">
                <div class="dropdown-header">
                    <div class="dropdown-avatar">
                        <c:choose>
                            <c:when test="${not empty sessionScope.userEmail}">
                                ${fn:toUpperCase(fn:substring(sessionScope.userEmail, 0, 1))}
                            </c:when>
                            <c:otherwise>U</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="dropdown-user-info">
                        <strong>
                            <c:choose>
                                <c:when test="${not empty sessionScope.userDisplayName}">
                                    ${sessionScope.userDisplayName}
                                </c:when>
                                <c:otherwise>${sessionScope.userEmail}</c:otherwise>
                            </c:choose>
                        </strong>
                        <span>${sessionScope.userEmail}</span>
                    </div>
                </div>
                
                <div class="dropdown-divider"></div>
                
                <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">
                    <i class="bi bi-person-gear"></i>
                    Profile & Settings
                </a>
                
                <div class="dropdown-divider"></div>
                
                <form method="post" action="${pageContext.request.contextPath}/logout" class="dropdown-logout-form">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="dropdown-item dropdown-logout">
                        <i class="bi bi-box-arrow-right"></i>
                        Sign Out
                    </button>
                </form>
            </div>
        </div>
    </div>
</header>

<script>
function toggleUserDropdown(event) {
    event.stopPropagation();
    var menu = document.getElementById('userDropdownMenu');
    menu.classList.toggle('show');
}

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    var dropdown = document.querySelector('.user-dropdown');
    var menu = document.getElementById('userDropdownMenu');
    if (dropdown && !dropdown.contains(event.target)) {
        menu.classList.remove('show');
    }
});
</script>
