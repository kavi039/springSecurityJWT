package com.springmvc.config;

import com.springmvc.filter.JWTAuthenticationFilter;
import com.springmvc.filter.JWTAuthorizationFilter;
import com.springmvc.handler.AuthenticationSuccess;
import com.springmvc.handler.LogoutSuccess;
import com.springmvc.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("successHandler")
    AuthenticationSuccess authenticationSuccess;

    @Autowired
    @Qualifier("logoutSuccessHandler")
    LogoutSuccess logoutSuccess;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder managerBuilder) throws Exception {
        managerBuilder
                .userDetailsService(customUserDetailsService);
//                .inMemoryAuthentication()
//                .withUser("user").password("pass").roles("ADMIN")
//                .and()
//                .withUser("user2").password("pass2").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/register/**","/registerUser/**","/registrationConfirmation").permitAll()
                .anyRequest().authenticated()
                .and()
//                .formLogin()
//                .successHandler(authenticationSuccess)
//                .loginPage("/login").permitAll()
//                .loginProcessingUrl("/loginUrl")
//                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()));
//                .logout()
//                .logoutSuccessHandler(logoutSuccess)
//                .permitAll()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/doLogout","GET"));
//                .and()
//                .rememberMe()
//                .tokenRepository(persistentTokenRepository());
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
}
