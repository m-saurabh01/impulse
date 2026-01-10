<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Server Error</title>
    <link rel="stylesheet" href="${ctx}/assets/css/bootstrap.min.css"/>
    <style>
        body {
            background-color: #f8f9fa;
            display: flex;
            height: 100vh;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', sans-serif;
        }
        .error-container {
            text-align: center;
        }
        .error-code {
            font-size: 100px;
            font-weight: bold;
            color: #6c757d;
            margin-bottom:20px;
        }
        .error-message {
            font-size: 24px;
            margin-top: -20px;
        }
        .back-link {
            margin-top: 30px;
        }
    </style>
</head>
<body>
<div class="error-container">
    <div class="error-code">500</div>
    <div class="error-message">An unexpected error occurred.</div>
    <div class="back-link">
        <a href="<c:url value='/mail/inbox'/>" class="btn btn-primary">Go to Inbox</a>
    </div>
</div>
</body>
</html>