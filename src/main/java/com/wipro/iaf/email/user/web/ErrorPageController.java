package com.wipro.iaf.email.user.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @GetMapping("/error")
    public String handle(HttpServletRequest req) {
        Integer status = (Integer) req.getAttribute(
            RequestDispatcher.ERROR_STATUS_CODE);

        if (status == 404) return "error/404";
        if (status == 403) return "error/403";
        return "error/500";
    }
}
