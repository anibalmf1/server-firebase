package br.com.anibal.captacaofilipeserver.filters;

import br.com.anibal.captacaofilipeserver.services.MyUserDetailsService;
import br.com.anibal.captacaofilipeserver.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilterRequest extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    private final String BEARER_TEXT = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorization = request.getHeader("authorization");

        if (isValidAuthorization(authorization) && hasNoAuthentication()) {
            final String jwt = extractJwt(authorization);

            validateJwtAndCreateAuthenticationToken(request, jwt);
        }

        filterChain.doFilter(request, response);
    }

    private void validateJwtAndCreateAuthenticationToken(HttpServletRequest request, String jwt) {
        final String username = jwtUtil.extractUsername(jwt);

        if (Strings.isNotEmpty(username)) {
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                createAuthenticationToken(request, userDetails);
            }
        }
    }

    private void createAuthenticationToken(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    private boolean hasNoAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private boolean isValidAuthorization(String authorization) {
        if (Strings.isEmpty(authorization))
            return false;

        return authorization.startsWith(BEARER_TEXT);
    }

    private String extractJwt(String authorization) {
        return authorization.substring(BEARER_TEXT.length() + 1);
    }
}
