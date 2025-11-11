package om.example.om_pay.middleware;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import om.example.om_pay.security.JwtTokenProvider;
import om.example.om_pay.utils.CookieUtil;

/**
 * Filtre JWT qui intercepte chaque requête HTTP pour valider le token
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final CookieUtil cookieUtil;

    public JwtAuthenticationFilter(
        JwtTokenProvider tokenProvider,
        UserDetailsService userDetailsService,
        CookieUtil cookieUtil
    ) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Essayer de récupérer le token depuis le cookie (priorité)
            String jwt = cookieUtil.getJwtFromCookie(request);
            
            // 2. Si pas de cookie, essayer le header Authorization (rétro-compatibilité)
            if (!StringUtils.hasText(jwt)) {
                jwt = getJwtFromRequest(request);
            }

            // 3. Valider le token et configurer le contexte de sécurité
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String telephone = tokenProvider.getPhoneFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(telephone);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Impossible de configurer l'authentification utilisateur", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT de l'en-tête Authorization (rétro-compatibilité)
     * Format attendu: "Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
