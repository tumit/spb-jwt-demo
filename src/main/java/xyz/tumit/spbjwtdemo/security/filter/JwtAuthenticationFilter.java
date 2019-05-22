package xyz.tumit.spbjwtdemo.security.filter;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xyz.tumit.spbjwtdemo.security.model.ApplicationUser;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static xyz.tumit.spbjwtdemo.security.SecurityConstants.*;


@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            ApplicationUser creds = new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);
            log.debug("creds={}", creds);
            return authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                                    creds.getUsername(),
                                    creds.getPassword(),
                                    new ArrayList<>()));
        } catch (IOException e) {
            logger.warn("exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {
        String username = getSubject(auth);
        String token = JWT.create()
                          .withSubject(username)
                          .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                          .sign(HMAC512(SECRET.getBytes()));
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);

    }

    private String getSubject(Authentication auth) {

        if (auth.getPrincipal() instanceof String) {
            return (String) auth.getPrincipal();
        }

        if (auth.getPrincipal() instanceof User) {
            return ((User) auth.getPrincipal()).getUsername();
        }

        throw new RuntimeException("subject not found: auth={}" + auth);
    }

}
