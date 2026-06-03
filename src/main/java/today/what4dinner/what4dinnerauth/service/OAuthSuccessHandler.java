package today.what4dinner.what4dinnerauth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import today.what4dinner.what4dinnerauth.dto.UserInfo;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserInfoService userInfoService;

    private final JWTService jWTService;

    public OAuthSuccessHandler(UserInfoService userInfoService, JWTService jWTService) {
        this.userInfoService = userInfoService;
        this.jWTService = jWTService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OidcUser oidcUser = (OidcUser) auth2AuthenticationToken.getPrincipal();
        String email = oidcUser.getEmail();
        String username = oidcUser.getFullName();
        // Resolve (or create) our own user record; the returned UserInfo carries our UUID,
        // not Google's `sub`, so the JWT subject identifies the user in our own system.
        UserInfo user = userInfoService.authenticateByGoogle(email, username)
                .orElseThrow(() -> new IllegalStateException("Failed to resolve user for email " + email));
        String jwtToken = jWTService.generateShortTermToken(user.getId(), user.getEmail());
        response.sendRedirect("https://dash.what4dinner.today/callback?code="+ URLEncoder.encode(jwtToken, StandardCharsets.UTF_8));
    }

}
