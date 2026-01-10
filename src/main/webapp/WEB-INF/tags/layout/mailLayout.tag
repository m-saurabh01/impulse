<%@ tag pageEncoding="UTF-8" %>
<%@ attribute name="pageTitle" required="true" %>

<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle} | Pulse Mail</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/mail.css">
    <script defer src="${pageContext.request.contextPath}/assets/js/mail.js"></script>
    <script>
const contextPath = "${pageContext.request.contextPath}";
</script>
    
</head>
<body class="vh-100 d-flex flex-column">

<%@ include file="header.tag" %>

<div class="flex-grow-1 d-flex">

    <%@ include file="sidebar.tag" %>

    <main class="content flex-grow-1 p-3 overflow-auto">
        <jsp:doBody/>
    </main>

</div>

<%@ include file="footer.tag" %>

</body>

</html>
