package com.example.managejob.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasAnyRole("ADMIN", "SUBADMIN")
                .antMatchers("/user/list").hasAnyRole("ADMIN", "SUBADMIN")
                .antMatchers("/css/**", "/js/**", "/images/**", "/plugins/**", "/scss/**", "/user/download", "/", "/system/**")
                .permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()
                .formLogin()
                .loginPage("/system/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/system/login?e=Username or Password invalid!")
                .and().logout().logoutSuccessUrl("/system/login").permitAll().and()
                .exceptionHandling().accessDeniedPage("/system/error403");
        return http.build();
    }

}