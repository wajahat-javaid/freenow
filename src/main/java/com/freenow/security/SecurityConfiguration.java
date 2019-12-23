package com.freenow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{

    @Bean
    public UserDetailsService userDetailsService()
    {
        return new CustomUserDetailsService();
    }


    @Bean
    public DaoAuthenticationProvider authProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {

        auth.authenticationProvider(authProvider());

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception
    {

        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/v1/drivers").hasRole("ADMIN")
            .antMatchers("/v1/drivers/**").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.PUT, "/v1/cars/**").hasRole("ADMIN")
            .antMatchers("/v1/cars/**").hasAnyRole("ADMIN", "USER")
            .antMatchers("/h2-console/**").permitAll()
            .and()
            .csrf().disable()
            .formLogin();
        http.headers().frameOptions().disable();
    }
}
