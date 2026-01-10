
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

<layout:authLayout pageTitle="Create account">

   <div class="auth-card animate-fade-up">
            <h2 class="auth-title">Create account</h2>
            <p class="auth-subtitle">Your new mailbox starts here</p>

            <form method="post" action="${pageContext.request.contextPath}/signup">
                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}"/>

                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input class="form-control"
                           type="email"
                           name="email"
                           required>
                </div>

                <div class="mb-4">
                    <label class="form-label">Password</label>
                    <input class="form-control"
                           type="password"
                           name="password"
                           required>
                </div>

                <button class="btn btn-success w-100 mb-3">
                    Create account
                </button>

                <div class="auth-footer">
                    <span>Already have an account?</span>
                    <a href="${pageContext.request.contextPath}/login">
                        Sign in
                    </a>
                </div>
            </form>
        </div>


</layout:authLayout>


