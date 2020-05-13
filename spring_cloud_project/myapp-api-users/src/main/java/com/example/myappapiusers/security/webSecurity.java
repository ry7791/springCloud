package com.example.myappapiusers.security;


import com.example.myappapiusers.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class webSecurity extends WebSecurityConfigurerAdapter {

    private Environment env;
    private UsersService usersService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public webSecurity(Environment env, UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.env = env;
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();
        // http.authorizeRequests().antMatchers("/users/**").permitAll(); //모든 ip에서 접근 가능
        http.authorizeRequests().antMatchers("/**").hasIpAddress(env.getProperty("gateway.ip"))
        .and()
        .addFilter(getAuthenticationFilter());  // ("gateway.ip")

        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception{
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(usersService, env, authenticationManager());
//        authenticationFilter.setAuthenticationManager(
//                authenticationManager()
//        );

        return authenticationFilter;
    }
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception{
            auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
        }


    }


