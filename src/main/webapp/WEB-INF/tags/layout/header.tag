<header class="navbar navbar-light bg-light px-3 border-bottom">
    <span class="navbar-brand mb-0 h5">Pulse Mail</span>

    <div class="d-flex gap-2">
        <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline">
    <input type="hidden"
           name="${_csrf.parameterName}"
           value="${_csrf.token}"/>
    <button type="submit" class="btn btn-outline-secondary btn-sm">
        Logout
    </button>
	</form>
        
    </div>
</header>
