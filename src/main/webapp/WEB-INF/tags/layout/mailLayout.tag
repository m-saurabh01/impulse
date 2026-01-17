<%@ tag pageEncoding="UTF-8" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} | Impulse</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/icons/new_icon.png">

    <!-- CDN Resources -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/mail.css">
    
    <!-- WebSocket Libraries -->
    <script src="${pageContext.request.contextPath}/assets/js/sockjs.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/stomp.min.js"></script>
    
    <!-- Bootstrap JS -->
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
    
    <script defer src="${pageContext.request.contextPath}/assets/js/mail.js"></script>
    <script>
        const contextPath = "${pageContext.request.contextPath}";
        const csrfToken = "${_csrf.token}";
        const csrfHeader = "${_csrf.headerName}";
        const currentUserEmail = "${pageContext.request.userPrincipal.name}";
    </script>
</head>
<body>

<%@ include file="header.tag" %>

<div class="app-container">
    <%@ include file="sidebar.tag" %>
    
    <main class="content">
        <jsp:doBody/>
    </main>
</div>

</body>
</html>
