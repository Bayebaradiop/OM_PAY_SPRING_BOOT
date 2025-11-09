package om.example.om_pay.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Utilitaire pour gérer les cookies JWT HTTP-only
 */
@Component
public class CookieUtil {

    @Value("${app.jwt.cookie-name:JWT-TOKEN}")
    private String cookieName;

    @Value("${app.jwt.cookie-expiry:86400}") 
    private int cookieExpiry;

    public void createJwtCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true); 
        cookie.setSecure(false);  
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpiry);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public String getJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void deleteJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); 
        response.addCookie(cookie);
    }
}
