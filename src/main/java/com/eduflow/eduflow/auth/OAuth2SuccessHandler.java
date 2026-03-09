package com.eduflow.eduflow.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.eduflow.eduflow.user.User;
import com.eduflow.eduflow.user.UserRepository;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        // Redirect to frontend with token as query param
        // In production this will be your React/Angular app URL
        String redirectUrl = "https://d3li9dplflwnfz.cloudfront.net/oauth2/callback?token=" + token;
        try {
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (IOException e) {
            throw new ServletException("Failed to redirect after OAuth2 login", e);
        }
    }

}
