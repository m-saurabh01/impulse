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
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/mail.css">
    
    <!-- WebSocket Libraries -->
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
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

<%@ include file="footer.tag" %>

</body>
</html>
