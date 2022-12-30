package com.example.managejob.config;

import com.example.managejob.service.CustomOAuth2UserService;
import com.example.managejob.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Cấu hình remember me, thời gian là 1296000 giây
        http.rememberMe().key("uniqueAndSecret").tokenValiditySeconds(1296000);

        http.authorizeRequests().antMatchers("/oauth2/**").permitAll().and().oauth2Login().loginPage("/system/login").userInfoEndpoint()
                .userService(auth2UserService).and().successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException{
                        DefaultOidcUser oauthUser = (DefaultOidcUser) authentication.getPrincipal();
                userService.processOAuthPostLogin(oauthUser.getEmail());
                response.sendRedirect("/");
                    }
                });
        http.authorizeRequests()
                .antMatchers("/admin/**").hasAnyRole("ADMIN", "SUBADMIN")
                .antMatchers("/user/list").hasAnyRole("ADMIN", "SUBADMIN")
                .antMatchers("/css/**", "/js/**", "/images/**", "/plugins/**", "/scss/**", "/user/download", "/system/**")
                .permitAll().anyRequest().authenticated()
                .and().csrf().disable()
                .formLogin()
                .loginPage("/system/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/system/login?e=Username or Password invalid!")
                .and().logout().logoutSuccessUrl("/system/login").permitAll()
                .and().exceptionHandling().accessDeniedPage("/system/error403");
        return http.build();
    }

    @Autowired
    private CustomOAuth2UserService auth2UserService;

}