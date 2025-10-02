package rs.ogisa.notesapp.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import rs.ogisa.notesapp.dto.CreateUserDto;
import rs.ogisa.notesapp.jwt.JwtUtil;
import rs.ogisa.notesapp.services.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class AuthenticationSuccessHandlerController implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthenticationSuccessHandlerController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");
        String username = (String) attributes.get("login");

        var user = userService.getUserByUsernameOrEmail(email);
        if (user == null) {
            CreateUserDto dto = new CreateUserDto();
            dto.setUsername(username);
            dto.setEmail(email);
            dto.setPassword(UUID.randomUUID().toString());
            userService.createUser(dto);
        }

        String token = jwtUtil.generateToken(userService.getUserByUsernameOrEmail(email));

        //TODO don't know if i need this
        String redirectUrl = "myapp://login?token=" + token; // custom scheme for React Native
        response.sendRedirect(redirectUrl);

    }
}
