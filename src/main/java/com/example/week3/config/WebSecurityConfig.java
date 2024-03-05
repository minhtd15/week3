package com.example.week3.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

  @Autowired
  UserDetailsService userDetailsService;



  @Autowired
  public void config(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain config(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authz -> authz
        .requestMatchers("/").hasAnyAuthority("USER", "ADMIN", "CREATOR", "EDITOR")
        .requestMatchers("/NEW").hasAnyAuthority("ADMIN", "CREATOR")
        .requestMatchers("/EDIT/**").hasAnyAuthority("ADMIN", "EDITOR")
        .requestMatchers("/DELETE/**").hasAnyAuthority("ADMIN")
        .anyRequest().authenticated()
    ).formLogin(formLogin -> formLogin
        .loginPage("/login")
        .permitAll()
    ).logout(logout -> logout
        .logoutUrl("/logout")
        .permitAll()
    ).exceptionHandling(exceptionHandling -> exceptionHandling
        .accessDeniedPage("/access-denied")
    );

    return http.build();
  }
}