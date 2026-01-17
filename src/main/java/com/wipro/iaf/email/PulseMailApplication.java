package com.wipro.iaf.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main entry point for the PulseMail application.
 * 
 * <p>PulseMail is a secure, offline-capable enterprise email system designed
 * for LAN environments supporting 200-300 users. The application provides
 * comprehensive email functionality including composing, inbox management,
 * labels, attachments, and real-time notifications via WebSocket.</p>
 * 
 * <p>This class extends {@link SpringBootServletInitializer} to support
 * deployment as a traditional WAR file in external servlet containers,
 * while also supporting standalone execution via embedded Tomcat.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 */
@SpringBootApplication
public class PulseMailApplication extends SpringBootServletInitializer {

    /**
     * Configures the application when deployed as a WAR file to an external servlet container.
     * 
     * <p>This method is called by the servlet container during initialization
     * and registers the main application class as a Spring configuration source.</p>
     * 
     * @param builder the {@link SpringApplicationBuilder} used to configure the application
     * @return the configured {@link SpringApplicationBuilder} with this application as source
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PulseMailApplication.class);
    }

    /**
     * Main method to launch the PulseMail application in standalone mode.
     * 
     * <p>Starts the embedded Tomcat server and initializes the Spring application context.
     * The application will be accessible at the configured server port (default: 8080).</p>
     * 
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(PulseMailApplication.class, args);
    }
}