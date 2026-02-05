package com.birthday.invitation.controller;

import com.birthday.invitation.dto.request.AdminLoginRequest;
import com.birthday.invitation.dto.response.AdminResponse;
import com.birthday.invitation.dto.response.AuthResponse;
import com.birthday.invitation.security.AdminPrincipal;
import com.birthday.invitation.security.SessionTokenAuthenticationFilter;
import com.birthday.invitation.service.AdminAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminAuthService authService;

    @Value("${app.admin-session-validity-days:7}")
    private int sessionValidityDays;

    public AdminAuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestMagicLink(
            @Valid @RequestBody AdminLoginRequest request) {

        authService.requestMagicLink(request.getEmail());

        return ResponseEntity.ok(Map.of(
                "message", "If an admin account exists with this email, a login link has been sent."
        ));
    }

    @GetMapping("/validate/{magicToken}")
    public ResponseEntity<AuthResponse> validateMagicLink(
            @PathVariable String magicToken,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            HttpServletRequest request,
            HttpServletResponse response) {

        String ipAddress = forwardedFor != null ? forwardedFor : request.getRemoteAddr();

        AuthResponse authResponse = authService.validateMagicLink(magicToken, userAgent, ipAddress);

        // Set HttpOnly cookie
        Cookie sessionCookie = new Cookie(
                SessionTokenAuthenticationFilter.ADMIN_SESSION_COOKIE,
                authResponse.getSessionToken()
        );
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(sessionValidityDays * 24 * 60 * 60);
        sessionCookie.setAttribute("SameSite", "Strict");
        response.addCookie(sessionCookie);

        // Return response without token (it's in the cookie)
        return ResponseEntity.ok(authResponse.withoutToken());
    }

    @PostMapping("/verify")
    public ResponseEntity<AdminResponse> verifySession(
            @CookieValue(name = "ADMIN_SESSION_TOKEN", required = false) String sessionToken) {

        if (sessionToken == null) {
            return ResponseEntity.status(401).build();
        }

        return authService.verifySession(sessionToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "ADMIN_SESSION_TOKEN", required = false) String sessionToken,
            HttpServletResponse response) {

        if (sessionToken != null) {
            authService.invalidateSession(sessionToken);
        }

        // Clear cookie
        Cookie cookie = new Cookie(SessionTokenAuthenticationFilter.ADMIN_SESSION_COOKIE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AdminResponse> getCurrentAdmin(
            @AuthenticationPrincipal AdminPrincipal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(AdminResponse.from(principal.getAdmin()));
    }
}
