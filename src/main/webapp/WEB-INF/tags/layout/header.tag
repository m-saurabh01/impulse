<%@ tag pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<header class="app-header">
    <div class="brand">
        <img src="${pageContext.request.contextPath}/assets/icons/new_icon.png" alt="Impulse" class="brand-logo">
        <span>Impulse</span>
    </div>
    
    <div class="search-container">
        <div class="search-wrapper">
            <i class="bi bi-search search-icon"></i>
            <input type="text" class="search-box" placeholder="Search" id="searchInput"/>
        </div>
    </div>
    
    <div class="header-actions">
        <form method="post" action="${pageContext.request.contextPath}/logout" class="logout-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button type="submit" class="header-btn" title="Logout">
                <i class="bi bi-box-arrow-right"></i>
            </button>
        </form>
        <div class="user-avatar" title="${sessionScope.userEmail}">
            <c:choose>
                <c:when test="${not empty sessionScope.userEmail}">
                    ${fn:toUpperCase(fn:substring(sessionScope.userEmail, 0, 1))}
                </c:when>
                <c:otherwise>U</c:otherwise>
            </c:choose>
        </div>
    </div>
</header>
