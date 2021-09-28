package com.zhongy.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@Order(-1)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private RequestSuccessHandler requestSuccessHandler;

    /***
     * 忽略安全拦截的URL
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/oauth/login",
                "/oauth/logout",
                "/oauth/refresh",
                "/oauth/loginByQQ",
                "/oauth/loginAfter"
                );
    }

    /***
     * 创建授权管理认证对象
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }

    /***
     * 采用BCryptPasswordEncoder对密码进行编码
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /****
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //配置了springsecurity要加cors否则配置了注解也不会生效
        http.cors().and().csrf().disable()
                .httpBasic()        //启用Http基本身份验证
                .and()
                .formLogin()       //启用表单身份验证
                .and()
                .authorizeRequests()    //限制基于Request请求访问
                .antMatchers("/webjars/**",
                        "/resources/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/v2/api-docs")
                .permitAll()
                .anyRequest()
                .authenticated();       //其他请求都需要经过验证

    }
}
