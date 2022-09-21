package hexlet.code.filter;

import hexlet.code.component.JWTHelper;
import io.jsonwebtoken.ClaimJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static hexlet.code.config.SecurityConfig.DEFAULT_AUTHORITIES;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer";

    private final RequestMatcher publicUrls;
    private final JWTHelper jwtHelper;

    public JWTAuthorizationFilter(final RequestMatcher publicUrls,
                                  final JWTHelper jwtHelper) {
        this.publicUrls = publicUrls;
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return publicUrls.matches(request);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        UsernamePasswordAuthenticationToken authToken = null;
        try {
            authToken = Optional.ofNullable(request.getHeader(AUTHORIZATION))
                    .map(header -> header.replaceFirst("^" + BEARER, ""))
                    .map(String::trim)
                    .map(jwtHelper::verify)
                    .map(claims -> claims.get(SPRING_SECURITY_FORM_USERNAME_KEY))
                    .map(Object::toString)
                    .map(this::buildAuthToken)
                    .orElse(null);
        } catch (ClaimJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            return;
        }

        if (authToken == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("No token specified");
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken buildAuthToken(final String username) {
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                DEFAULT_AUTHORITIES
        );
    }
}
