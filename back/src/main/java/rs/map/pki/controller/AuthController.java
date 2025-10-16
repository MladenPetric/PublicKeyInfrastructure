package rs.map.pki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import rs.map.pki.config.JwtProperties;
import rs.map.pki.dto.LoginRequestDto;
import rs.map.pki.dto.TokenResponseDto;
import rs.map.pki.model.User;
import rs.map.pki.util.JwtUtil;
import rs.map.pki.util.PkiUserDetails;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/api/auth"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // POST auth/login
    @PostMapping("/login")
    ResponseEntity<TokenResponseDto> login(
            @RequestBody @Valid LoginRequestDto request,
            JwtProperties jwtProperties
    ) {
        var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var userDetails = (UserDetails) auth.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        String cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(jwtProperties.getRefreshExpiration())
                .sameSite("Strict")
                .build()
                .toString();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(new TokenResponseDto(accessToken));
    }

    // POST auth/refresh
    @PostMapping("/refresh")
    ResponseEntity<TokenResponseDto> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtUtil.extractUsername(refreshToken);
        var userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        return ResponseEntity.ok(new TokenResponseDto(newAccessToken));
    }

    // GET auth/whoami
    @GetMapping("/whoami")
    User whoami(@AuthenticationPrincipal PkiUserDetails principal) {
        // TODO: maybe make a user dto
        return principal.getUser();
    }

}
