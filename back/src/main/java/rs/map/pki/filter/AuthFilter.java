package rs.map.pki.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.map.pki.util.JwtUtil;

import java.io.IOException;
import java.util.Optional;


@RequiredArgsConstructor
@Component
public class AuthFilter extends OncePerRequestFilter {
    private static final String
            HEADER_AUTH = "Authorization",
            AUTH_TYPE = "Bearer";

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        Optional.ofNullable(request.getHeader(HEADER_AUTH))
                .filter(authHeader -> authHeader.startsWith(AUTH_TYPE + " "))
                .map(authHeader -> authHeader.substring(AUTH_TYPE.length() + " ".length()))
                .filter(jwtUtil::isTokenValid)
                .map(jwtUtil::extractUsername)
                .map(userDetailsService::loadUserByUsername)
                .ifPresent(userDetails -> {
                    var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                });

        filterChain.doFilter(request, response);
    }

}
