package com.kahago.kahagoservice.configuration;

import com.kahago.kahagoservice.controller.SignInController;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.security.JwtAuthenticationFilter;
import com.kahago.kahagoservice.security.JwtAuthorizationFilter;
import com.kahago.kahagoservice.security.UserServiceSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

import javax.sql.DataSource;

/**
 * @author Hendro yuwono
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class HttpSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String SQL_LOGIN = "SELECT user_id, password, status_layanan AS enabled FROM m_user WHERE user_id = ?";
    private static final String SQL_ROLES = "SELECT u.user_id,  m.ROLE_NAME AS user_level FROM m_user u INNER JOIN m_user_category m ON u.user_category=m.seqid WHERE user_id = ?";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserServiceSecurity userServiceSecurity;

    @Autowired
    private SignInController signInController;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private MUserCategoryRepo mUserCategoryRepo;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();

        http.antMatcher("/api/**").authorizeRequests()
                .antMatchers("/api/auth/login").permitAll()
                .antMatchers("/error").permitAll()
                .antMatchers("/api/**")
                //.hasAnyAuthority("ROLE_COUNTER","ROLE_CUSTOMER","ROLE_MARKETING","ROLE_MITRA","ROLE_SUPERVISOR_MARKETING","ROLE_TIKI_REQUEST","ROLE_TIKI_BOOKING","ROLE_EXTRA_MITRA","ROLE_EXTRA_CUSTOMER","ROLE_ADMIN_INPUT","ROLE_SALES_KAHA","BANNED","ROLE_WAREHOUSE","ROLE_OPERATIONAL", "ROLE_ADMIN","ROLE_KEUANGAN","ROLE_DEVELOPER")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(jwtAuthenticationFilter())
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userServiceSecurity));

    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/api/signup",
                "/api/checktarif",
                "/api/area/districts",
                "/api/area/origin",
                "/api/goods/*",
			    "/api/track/*",
				"/api/vendors",
				"/api/tutorial",
				"/api/user/forgot",
                "/api/tutorial/*",
                "/api/dashboard/**",
                "/api/goods",
                "/app/version",
                "/api/app/version",
                "/api/images/**",
                "/api/user/newpass",
                "/api/bank/logo/**",
                "/api/warehouse/print/**",
                "/api/resi/*",
                "/api/report/manifest/pos",
                "/api/report/manifest/pickup",
                "/api/faspay/**",
                "/api/report/approval/*",
                "/blast/**",
                "/api/reportadmin/**",
                "/api/report/permohonan",
                "/api/app/version/ios",
                "/api/cron/**",
                "/api/leadtime/report");
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .usersByUsernameQuery(SQL_LOGIN)
                .authoritiesByUsernameQuery(SQL_ROLES);

        auth.userDetailsService(userServiceSecurity).passwordEncoder(passwordEncoder);
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authenticationManager(), userServiceSecurity, signInController);
        filter.setFilterProcessesUrl("/api/auth/login");
        return filter;
    }

}
