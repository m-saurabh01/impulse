package com.wipro.iaf.email.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wipro.iaf.email.user.service.UserService;

/**
 * Controller handling user authentication operations including login and registration.
 * 
 * <p>This controller manages the public-facing authentication endpoints that allow
 * users to log in to existing accounts or create new accounts. Authentication itself
 * is handled by Spring Security; this controller provides the views and registration logic.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /login} - Display the login page</li>
 *   <li>{@code GET /signup} - Display the registration page</li>
 *   <li>{@code POST /signup} - Process new user registration</li>
 * </ul>
 * </p>
 * 
 * <h3>Registration Requirements:</h3>
 * <ul>
 *   <li>Email must end with @impulse.iaf.in</li>
 *   <li>Password must meet complexity requirements (8+ chars, upper, lower, digit, special)</li>
 * </ul>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see UserService
 */
@Controller
public class AuthController {
	
	@Autowired
	private UserService userService;

    /**
     * Displays the login page.
     * 
     * <p>The actual authentication is handled by Spring Security's form login.
     * This endpoint simply renders the login view.</p>
     * 
     * @return the view name for the login page ("auth/login")
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    /**
     * Displays the user registration page.
     * 
     * @return the view name for the signup page ("auth/signup")
     */
    @GetMapping("/signup")
    public String signup() {
        return "auth/signup";
    }

    /**
     * Processes user registration and creates a new account.
     * 
     * <p>Validates and creates a new user account with the provided credentials.
     * The password is securely hashed before storage. Upon successful registration,
     * redirects to the login page.</p>
     * 
     * <p>Validations performed:</p>
     * <ul>
     *   <li>Email must end with @impulse.iaf.in</li>
     *   <li>Email must not already be registered</li>
     *   <li>Password must meet complexity requirements</li>
     * </ul>
     * 
     * @param email    the email address for the new account (must be @impulse.iaf.in)
     * @param password the password for the new account (must meet complexity requirements)
     * @param model    the model for passing error messages to the view
     * @return redirect to the login page on success, or back to signup with error
     */
    @PostMapping("/signup")
    public String doSignup(@RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        try {
            userService.register(email, password);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/signup";
        }
    }
}
