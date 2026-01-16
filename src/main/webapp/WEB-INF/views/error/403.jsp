<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Access Denied - PulseMail</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap-icons.css">
    <style>
        :root {
            --primary-color: #6c5ce7;
            --primary-dark: #5b4cdb;
            --error-color: #e74c3c;
        }
        
        * { box-sizing: border-box; margin: 0; padding: 0; }
        
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 40%, #0f3460 70%, #1a1a2e 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', system-ui, -apple-system, BlinkMacSystemFont, sans-serif;
            padding: 20px;
            position: relative;
        }
        
        body::before {
            content: '';
            position: absolute;
            top: 0; left: 0; right: 0; bottom: 0;
            background: radial-gradient(ellipse at 30% 20%, rgba(231, 76, 60, 0.1) 0%, transparent 50%),
                        radial-gradient(ellipse at 70% 80%, rgba(15, 52, 96, 0.2) 0%, transparent 50%);
            pointer-events: none;
        }
        
        .error-container {
            text-align: center;
            position: relative;
            z-index: 1;
            max-width: 480px;
        }
        
        .error-card {
            background: rgba(255, 255, 255, 0.98);
            border-radius: 16px;
            padding: 48px 44px;
            box-shadow: 0 25px 60px -12px rgba(0, 0, 0, 0.4),
                        0 0 40px rgba(231, 76, 60, 0.1);
        }
        
        .error-icon {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 24px;
            box-shadow: 0 10px 30px rgba(231, 76, 60, 0.3);
        }
        
        .error-icon i {
            font-size: 48px;
            color: white;
        }
        
        .error-code {
            font-size: 72px;
            font-weight: 700;
            color: #1a1a2e;
            line-height: 1;
            margin-bottom: 8px;
        }
        
        .error-title {
            font-size: 24px;
            font-weight: 600;
            color: #1a1a2e;
            margin-bottom: 12px;
        }
        
        .error-message {
            font-size: 15px;
            color: #6c757d;
            line-height: 1.6;
            margin-bottom: 32px;
        }
        
        .error-actions {
            display: flex;
            gap: 12px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn-primary-custom {
            background: linear-gradient(135deg, #6c5ce7 0%, #5b4cdb 100%);
            border: none;
            padding: 12px 28px;
            border-radius: 8px;
            color: white;
            font-weight: 600;
            font-size: 14px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.2s ease;
            box-shadow: 0 4px 15px rgba(108, 92, 231, 0.3);
        }
        
        .btn-primary-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(108, 92, 231, 0.4);
            color: white;
        }
        
        .btn-secondary-custom {
            background: transparent;
            border: 2px solid #e0e0e0;
            padding: 10px 24px;
            border-radius: 8px;
            color: #6c757d;
            font-weight: 600;
            font-size: 14px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.2s ease;
        }
        
        .btn-secondary-custom:hover {
            border-color: #6c5ce7;
            color: #6c5ce7;
        }
    </style>
</head>
<body>
<div class="error-container">
    <div class="error-card">
        <div class="error-icon">
            <i class="bi bi-shield-lock"></i>
        </div>
        <div class="error-code">403</div>
        <div class="error-title">Access Denied</div>
        <div class="error-message">
            Sorry, you don't have permission to access this page. 
            This might be because you're not logged in or don't have the required privileges.
        </div>
        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/mail/inbox" class="btn-primary-custom">
                <i class="bi bi-inbox"></i> Go to Inbox
            </a>
            <a href="${pageContext.request.contextPath}/auth/login" class="btn-secondary-custom">
                <i class="bi bi-box-arrow-in-right"></i> Sign In
            </a>
        </div>
    </div>
</div>
</body>
</html>