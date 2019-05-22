package xyz.tumit.spbjwtdemo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import xyz.tumit.spbjwtdemo.security.filter.JwtAuthenticationFilter;
import xyz.tumit.spbjwtdemo.security.filter.JwtAuthorizationFilter;
import xyz.tumit.spbjwtdemo.security.provider.HardCodeAuthenticationProvider;

import static xyz.tumit.spbjwtdemo.security.SecurityConstants.*;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private final HardCodeAuthenticationProvider hardCodeAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin()
            .and()
            .cors().and().csrf().disable().authorizeRequests()
            .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
            .antMatchers(H2_CONSOLE_URL).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(new JwtAuthenticationFilter(this.authenticationManager()))
            .addFilter(new JwtAuthorizationFilter(this.authenticationManager()))
            // this disables session creation on Spring Security
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(hardCodeAuthenticationProvider);
    }

}
