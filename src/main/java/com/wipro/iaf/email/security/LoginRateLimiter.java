package com.wipro.iaf.email.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

/**
 * Rate limiter for login attempts to prevent brute force attacks.
 * Limits login attempts per IP address.
 */
@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = TimeUnit.MINUTES.toMillis(15);

    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    /**
     * Record a failed login attempt for the given IP.
     * @param ipAddress The client IP address
     * @return true if the user is now locked out
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
     * Check if the given IP is currently locked out.
     * @param ipAddress The client IP address
     * @return true if locked out
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
     * Clear attempts for an IP after successful login.
     * @param ipAddress The client IP address
     */
    public void clearAttempts(String ipAddress) {
        attempts.remove(ipAddress);
    }

    /**
     * Get remaining lockout time in seconds.
     * @param ipAddress The client IP address
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
     * Get the number of remaining attempts before lockout.
     * @param ipAddress The client IP address
     * @return remaining attempts
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

    private static class AttemptInfo {
        final int count;
        final long lastAttempt;
        final boolean locked;

        AttemptInfo(int count, long lastAttempt, boolean locked) {
            this.count = count;
            this.lastAttempt = lastAttempt;
            this.locked = locked;
        }
    }
}
