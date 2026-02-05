package com.birthday.invitation.security;

import com.birthday.invitation.entity.Admin;
import com.birthday.invitation.entity.AdminSession;
import com.birthday.invitation.entity.Guest;
import com.birthday.invitation.entity.GuestSession;
import com.birthday.invitation.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class SessionTokenAuthenticationFilter extends OncePerRequestFilter {

    public static final String GUEST_SESSION_COOKIE = "SESSION_TOKEN";
    public static final String ADMIN_SESSION_COOKIE = "ADMIN_SESSION_TOKEN";

    private final SessionService sessionService;

    public SessionTokenAuthenticationFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Try admin authentication first (for admin endpoints)
        String adminToken = extractToken(request, ADMIN_SESSION_COOKIE);
        if (adminToken != null) {
            Optional<AdminSession> sessionOpt = sessionService.validateAndRefreshAdminSession(adminToken);

            if (sessionOpt.isPresent()) {
                AdminSession session = sessionOpt.get();
                Admin admin = session.getAdmin();

                AdminPrincipal principal = new AdminPrincipal(admin, session);
                MagicLinkAuthenticationToken authentication =
                        new MagicLinkAuthenticationToken(principal, principal.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Try guest authentication
        String guestToken = extractToken(request, GUEST_SESSION_COOKIE);
        if (guestToken != null) {
            Optional<GuestSession> sessionOpt = sessionService.validateAndRefreshGuestSession(guestToken);

            if (sessionOpt.isPresent()) {
                GuestSession session = sessionOpt.get();
                Guest guest = session.getGuest();

                GuestPrincipal principal = new GuestPrincipal(guest, session);
                MagicLinkAuthenticationToken authentication =
                        new MagicLinkAuthenticationToken(principal, principal.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request, String cookieName) {
        // Check cookie first
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<String> token = Arrays.stream(cookies)
                    .filter(c -> cookieName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst();

            if (token.isPresent()) {
                return token.get();
            }
        }

        // Fallback to Authorization header (for mobile apps or API clients)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}
