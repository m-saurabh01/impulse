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
        
        <c:if test="${param.accountDeleted != null}">
            <div class="auth-success">
                <i class="bi bi-check-circle"></i>
                Your account has been permanently deleted
            </div>
        </c:if>
        
        <c:if test="${param.registered != null}">
            <div class="auth-success">
                <i class="bi bi-check-circle"></i>
                Account created successfully! Please sign in
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
                <div class="password-wrapper">
                    <input class="form-control" 
                           type="password" 
                           name="password" 
                           id="passwordInput"
                           placeholder="Enter your password"
                           required>
                    <button type="button" class="password-toggle" onclick="togglePassword('passwordInput', this)" aria-label="Show password">
                        <i class="bi bi-eye"></i>
                    </button>
                </div>
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

    <style>
        .password-wrapper {
            position: relative;
        }
        .password-wrapper .form-control {
            padding-right: 45px;
        }
        .password-toggle {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            border: none;
            background: transparent;
            color: #6c757d;
            cursor: pointer;
            padding: 5px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .password-toggle:hover {
            color: #0078d4;
        }
        .password-toggle i {
            font-size: 18px;
        }
    </style>

    <script>
        function togglePassword(inputId, button) {
            const input = document.getElementById(inputId);
            const icon = button.querySelector('i');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
                button.setAttribute('aria-label', 'Hide password');
            } else {
                input.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
                button.setAttribute('aria-label', 'Show password');
            }
        }
    </script>

</layout:authLayout>
