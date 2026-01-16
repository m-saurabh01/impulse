<%@ tag description="Auth page layout" pageEncoding="UTF-8" %>
<%@ attribute name="pageTitle" required="true" %>

<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle} | Impulse</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/icons/new_icon.png">

    <!-- Local Resources (offline LAN compatible) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/animations.css">
</head>
<body class="auth-body">

<div class="auth-container">
    <jsp:doBody/>
</div>

</body>
</html>
