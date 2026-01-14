<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:authLayout pageTitle="Sign in">

    <div class="auth-card animate-fade-up">
        
        <!-- Logo -->
        <div class="auth-logo">
            <img src="${pageContext.request.contextPath}/assets/icons/new_icon.png" alt="Impulse" class="auth-logo-img">
            <span>Impulse</span>
        </div>
        
        <h2 class="auth-title">Welcome back</h2>
        <p class="auth-subtitle">Sign in to access your mailbox</p>
        
        <c:if test="${param.error != null}">
            <div class="auth-error">
                <i class="bi bi-exclamation-circle"></i>
                Invalid email or password
            </div>
        </c:if>
        
        <c:if test="${param.logout != null}">
            <div class="auth-success">
                <i class="bi bi-check-circle"></i>
                You have been logged out successfully
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login" class="auth-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="mb-3">
                <label class="form-label">Email address</label>
                <input class="form-control" 
                       type="email" 
                       name="username" 
                       placeholder="name@company.com"
                       required autofocus>
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <input class="form-control" 
                       type="password" 
                       name="password" 
                       placeholder="Enter your password"
                       required>
            </div>

            <button type="submit" class="btn btn-primary w-100">
                Sign in
            </button>

            <div class="auth-footer">
                <span>Don't have an account?</span>
                <a href="${pageContext.request.contextPath}/signup">Create one</a>
            </div>
        </form>
    </div>

</layout:authLayout>
