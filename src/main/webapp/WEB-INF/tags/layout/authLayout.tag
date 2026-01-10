<%@ tag description="Auth page layout" pageEncoding="UTF-8" %>
<%@ attribute name="pageTitle" required="true" %>

<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle}</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/animations.css">
</head>
<body class="auth-body">

<div class="auth-container">
    <jsp:doBody/>
</div>

</body>
</html>
