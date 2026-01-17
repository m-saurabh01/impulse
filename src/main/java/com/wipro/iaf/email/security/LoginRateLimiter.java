package com.wipro.iaf.email.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

/**
 * Rate limiter for login attempts to prevent brute force attacks.
 * 
 * <p>Tracks failed login attempts per IP address and locks out
 * IP addresses that exceed the maximum allowed attempts. Uses
 * an in-memory concurrent map for thread-safe operation.</p>
 * 
 * <h3>Configuration:</h3>
 * <ul>
 *   <li>Maximum attempts: 5</li>
 *   <li>Lockout duration: 15 minutes</li>
 * </ul>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see LoginRateLimitFilter
 * @see AuthenticationEventListener
 */
@Component
public class LoginRateLimiter {

    /** Maximum failed attempts before lockout */
    private static final int MAX_ATTEMPTS = 5;
    /** Lockout duration in milliseconds (15 minutes) */
    private static final long LOCKOUT_DURATION_MS = TimeUnit.MINUTES.toMillis(15);

    /** Map of IP addresses to their attempt information */
    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    /**
     * Records a failed login attempt for the given IP.
     * 
     * @param ipAddress the client IP address
     * @return true if the user is now locked out, false otherwise
     */
    public boolean recordFailedAttempt(String ipAddress) {
        AttemptInfo info = attempts.compute(ipAddress, (key, existing) -> {
            long now = System.currentTimeMillis();
            if (existing == null || now - existing.lastAttempt > LOCKOUT_DURATION_MS) {
                return new AttemptInfo(1, now, false);
            }
            int newCount = existing.count + 1;
            boolean locked = newCount >= MAX_ATTEMPTS;
            return new AttemptInfo(newCount, now, locked);
        });
        return info.locked;
    }

    /**
     * Checks if the given IP is currently locked out.
     * 
     * <p>Also clears expired lockouts automatically.</p>
     * 
     * @param ipAddress the client IP address
     * @return true if locked out, false otherwise
     */
    public boolean isBlocked(String ipAddress) {
        AttemptInfo info = attempts.get(ipAddress);
        if (info == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now - info.lastAttempt > LOCKOUT_DURATION_MS) {
            attempts.remove(ipAddress);
            return false;
        }
        return info.locked;
    }

    /**
     * Clears attempt history for an IP after successful login.
     * 
     * @param ipAddress the client IP address
     */
    public void clearAttempts(String ipAddress) {
        attempts.remove(ipAddress);
    }

    /**
     * Gets remaining lockout time in seconds.
     * 
     * @param ipAddress the client IP address
     * @return seconds remaining, or 0 if not locked
     */
    public long getRemainingLockoutSeconds(String ipAddress) {
        AttemptInfo info = attempts.get(ipAddress);
        if (info == null || !info.locked) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - info.lastAttempt;
        long remaining = LOCKOUT_DURATION_MS - elapsed;
        return remaining > 0 ? remaining / 1000 : 0;
    }

    /**
     * Gets the number of remaining attempts before lockout.
     * 
     * @param ipAddress the client IP address
     * @return remaining attempts before lockout
     */
    public int getRemainingAttempts(String ipAddress) {
        AttemptInfo info = attempts.get(ipAddress);
        if (info == null) {
            return MAX_ATTEMPTS;
        }
        long now = System.currentTimeMillis();
        if (now - info.lastAttempt > LOCKOUT_DURATION_MS) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - info.count);
    }

    /**
     * Internal class to track attempt information per IP.
     * 
     * @author Saurabh Mishra
     * @version 1.0
     * @since 2026-01-01
     */
    private static class AttemptInfo {
        /** Number of failed attempts */
        final int count;
        /** Timestamp of last attempt */
        final long lastAttempt;
        /** Whether the IP is currently locked */
        final boolean locked;

        AttemptInfo(int count, long lastAttempt, boolean locked) {
            this.count = count;
            this.lastAttempt = lastAttempt;
            this.locked = locked;
        }
    }
}
