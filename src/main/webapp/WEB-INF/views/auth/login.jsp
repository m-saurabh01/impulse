

<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

<layout:authLayout pageTitle="Sign in">

    <div class="auth-card animate-fade-up">
        <h2 class="auth-title">Welcome back</h2>
        <p class="auth-subtitle">Sign in to your mailbox</p>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <input type="hidden"
                   name="${_csrf.parameterName}"
                   value="${_csrf.token}"/>

            <div class="mb-3">
                <label class="form-label">Email</label>
                <input class="form-control"
                       type="email"
                       name="username"
                       required autofocus>
            </div>

            <div class="mb-4">
                <label class="form-label">Password</label>
                <input class="form-control"
                       type="password"
                       name="password"
                       required>
            </div>

            <button class="btn btn-primary w-100 mb-3">
                Sign in
            </button>

            <div class="auth-footer">
                <span>New here?</span>
                <a href="${pageContext.request.contextPath}/signup">
                    Create an account
                </a>
            </div>
        </form>
    </div>

</layout:authLayout>
