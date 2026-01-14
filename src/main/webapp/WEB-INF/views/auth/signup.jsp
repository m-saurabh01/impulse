<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:authLayout pageTitle="Create account">

    <div class="auth-card animate-fade-up">
        
        <!-- Logo -->
        <div class="auth-logo">
            <img src="${pageContext.request.contextPath}/assets/icons/new_icon.png" alt="Impulse" class="auth-logo-img">
            <span>Impulse</span>
        </div>
        
        <h2 class="auth-title">Create your account</h2>
        <p class="auth-subtitle">Start using Impulse today</p>
        
        <c:if test="${not empty error}">
            <div class="auth-error">
                <i class="bi bi-exclamation-circle"></i>
                ${error}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/signup" class="auth-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="mb-3">
                <label class="form-label">Email address</label>
                <input class="form-control" 
                       type="email" 
                       name="email" 
                       placeholder="name@company.com"
                       required autofocus>
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <input class="form-control" 
                       type="password" 
                       name="password" 
                       placeholder="Create a strong password"
                       required
                       minlength="8">
                <small style="color: #605e5c; font-size: 12px; margin-top: 4px; display: block;">
                    Password must be at least 8 characters
                </small>
            </div>

            <button type="submit" class="btn btn-success w-100">
                Create account
            </button>

            <div class="auth-footer">
                <span>Already have an account?</span>
                <a href="${pageContext.request.contextPath}/login">Sign in</a>
            </div>
        </form>
    </div>

</layout:authLayout>


