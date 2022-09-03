package hexlet.code;

import hexlet.code.component.JWTHelper;
import hexlet.code.filter.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

import static org.springframework.http.HttpMethod.POST;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final List<GrantedAuthority> DEFAULT_AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));
    private final UserDetailsService userDetailsService;
    private final JWTHelper jwtHelper;
    private final RequestMatcher loginRequest;
    private final RequestMatcher publicUrls;
    private final PasswordEncoder passwordEncoder;


    public SecurityConfig(@Value("${server.servlet.context-path}") final String baseUrl,
                          final UserDetailsService userDetailsService,
                          final PasswordEncoder passwordEncoder, final JWTHelper jwtHelper) {
        this.loginRequest = new AntPathRequestMatcher("/login", POST.toString());
        this.publicUrls = new OrRequestMatcher(
                loginRequest,
                new AntPathRequestMatcher("/users"),
                new AntPathRequestMatcher("/h2console/**")
        );
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        final JWTAuthorizationFilter authorizationFilter = new JWTAuthorizationFilter(
            publicUrls,
            jwtHelper
        );

        http.csrf().disable()
                .authorizeRequests()
                    .requestMatchers(publicUrls).permitAll()
                    .anyRequest().authenticated()
                .and()
                //.addFilter(authenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement().disable()
                .logout().disable();

        http.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
