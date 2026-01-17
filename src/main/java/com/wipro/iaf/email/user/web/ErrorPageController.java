package com.wipro.iaf.email.user.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Custom error page controller for handling HTTP errors.
 * 
 * <p>Implements Spring Boot's {@link ErrorController} interface to provide
 * custom error pages for common HTTP error codes. This ensures users see
 * friendly, branded error pages instead of default container error pages.</p>
 * 
 * <p>Handled error codes:
 * <ul>
 *   <li><b>403</b> - Access denied (rendered as error/403.jsp)</li>
 *   <li><b>404</b> - Page not found (rendered as error/404.jsp)</li>
 *   <li><b>500</b> - Internal server error (rendered as error/500.jsp, default for other errors)</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 */
@Controller
public class ErrorPageController implements ErrorController {

    /**
     * Handles error requests and routes to appropriate error pages.
     * 
     * <p>Examines the HTTP status code from the request and returns the
     * corresponding error view. Unknown errors default to the 500 error page.</p>
     * 
     * @param req the HTTP request containing error information
     * @return the view name for the appropriate error page
     */
    @GetMapping("/error")
    public String handle(HttpServletRequest req) {
        Integer status = (Integer) req.getAttribute(
            RequestDispatcher.ERROR_STATUS_CODE);

        if (status == 404) return "error/404";
        if (status == 403) return "error/403";
        return "error/500";
    }
}
