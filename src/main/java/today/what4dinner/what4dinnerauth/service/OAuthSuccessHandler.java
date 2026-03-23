package today.what4dinner.what4dinnerauth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import today.what4dinner.what4dinnerauth.repository.MysqlRepository;

import java.io.IOException;
import java.util.Map;

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
        OAuth2User principal = auth2AuthenticationToken.getPrincipal();
        OidcUser oidcUser = (OidcUser) principal;
        String email = oidcUser.getEmail();
        String username = oidcUser.getName();
        userInfoService.authenticateByGoogle(email, username);
        String jwtToken = jWTService.generateToken(oidcUser.getName(), oidcUser.getEmail());
        response.addHeader("Authorization", "Bearer " + jwtToken);
        response.addHeader("Access-Control-Expose-Headers", "Authorization");
        response.sendRedirect("https://dash.what4dinner.today/");
    }

}
