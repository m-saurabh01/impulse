package com.wipro.iaf.email.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security configuration for PulseMail application.
 * 
 * <p>Configures authentication, authorization, session management,
 * and security headers. Extends WebSecurityConfigurerAdapter for
 * compatibility with Spring Boot 2.7.x.</p>
 * 
 * <h3>Security Features:</h3>
 * <ul>
 *   <li>Form-based login with custom login page</li>
 *   <li>CSRF protection enabled</li>
 *   <li>XSS protection headers</li>
 *   <li>Content Security Policy</li>
 *   <li>Session fixation protection</li>
 *   <li>Maximum 3 concurrent sessions per user</li>
 *   <li>HTTPS support (when enabled in application.properties)</li>
 * </ul>
 * 
 * <h3>HTTPS Configuration:</h3>
 * <p>To enable HTTPS:
 * <ol>
 *   <li>Generate SSL certificate (see application.properties for guide)</li>
 *   <li>Enable SSL settings in application.properties</li>
 *   <li>Uncomment the HTTPS sections marked below</li>
 * </ol>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see SecurityUser
 * @see SecurityUserDetailsService
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configures HTTP security settings.
     * 
     * <p>Sets up:
     * <ul>
     *   <li>Public access to login, signup, error pages, and static assets</li>
     *   <li>All other requests require authentication</li>
     *   <li>Custom login page at /login</li>
     *   <li>Logout with session invalidation</li>
     *   <li>Security headers including CSP, X-XSS-Protection, X-Frame-Options</li>
     *   <li>Session management with migration on authentication</li>
     * </ul>
     * </p>
     * 
     * @param http the HttpSecurity to configure
     * @throws Exception if configuration fails
     */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http
                // ============================================
                // HTTPS CONFIGURATION (Currently Disabled)
                // ============================================
                // Uncomment to force all requests to use HTTPS
                // Requires SSL to be enabled in application.properties
                //
                // .requiresChannel(channel -> channel
                //         .anyRequest().requiresSecure()
                // )
                // ============================================
                
                .authorizeHttpRequests()
                .antMatchers(
                        "/login",
                        "/signup",
                        "/error/**",
                        "/assets/**"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/mail/inbox", true))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .csrf(withDefaults())
                // Security Headers
                .headers(headers -> headers
                        .xssProtection(xss -> xss.block(true))
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: blob:; font-src 'self' data:; frame-src 'self';"))
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentTypeOptions(withDefaults())
                        // ============================================
                        // HSTS (HTTP Strict Transport Security)
                        // ============================================
                        // Uncomment when HTTPS is enabled to force browsers
                        // to always use HTTPS for this domain
                        //
                        // .httpStrictTransportSecurity(hsts -> hsts
                        //         .includeSubDomains(true)
                        //         .maxAgeInSeconds(31536000) // 1 year
                        // )
                        // ============================================
                )
                // Session Security
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .maximumSessions(3)
                        .maxSessionsPreventsLogin(false));
                
                // ============================================
                // SECURE COOKIES (Currently Disabled)
                // ============================================
                // Uncomment when HTTPS is enabled to secure session cookies
                // This prevents cookies from being sent over HTTP
                //
                // To enable programmatically, add this bean to a @Configuration class:
                //
                // @Bean
                // public ServletContextInitializer servletContextInitializer() {
                //     return servletContext -> {
                //         servletContext.getSessionCookieConfig().setSecure(true);
                //         servletContext.getSessionCookieConfig().setHttpOnly(true);
                //         servletContext.getSessionCookieConfig().setName("IMPULSE_SESSION");
                //     };
                // }
                //
                // Or add to application.properties:
                // server.servlet.session.cookie.secure=true
                // server.servlet.session.cookie.http-only=true
                // server.servlet.session.cookie.same-site=strict
                // ============================================
	}

}
