package com.example.Back_End_Restaurante.Config;



import com.example.Back_End_Restaurante.Security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs REST
        .cors(Customizer.withDefaults()) // Habilita suporte a CORS usando CorsConfigurationSource/CorsFilter
         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // REMOVA ou COMENTE esta linha por enquanto para usar o login padrão
        .authorizeHttpRequests(authorize -> authorize
                        // Permite acesso PÚBLICO ao endpoint de cadastro de funcionários (POST) - Ajuste se necessário
                        .requestMatchers(HttpMethod.POST, "/api/funcionarios").permitAll()
                        // Permite acesso PÚBLICO ao endpoint de cadastro de clientes (POST) - Ajuste se necessário
                        .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()
                        // Você pode adicionar outras rotas públicas aqui (ex: GET /cardapio)
                        // .requestMatchers(HttpMethod.GET, "/api/produtos").permitAll()

                        // Qualquer outra requisição PRECISA estar autenticada
                        .anyRequest().authenticated()
                )
                // Habilita o login via formulário (que também cria o endpoint POST /login)
                .formLogin(form -> form
                        .loginProcessingUrl("/login") // will be prefixed by context-path -> effective /api/login
                        .usernameParameter("email")    // username field
                        .passwordParameter("senha")    // password field
                        // Return JSON on success/failure instead of redirecting to HTML pages
                        .successHandler((request, response, authentication) -> {
                            Objects.requireNonNull(request);
                            Objects.requireNonNull(authentication);
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json;charset=UTF-8");
                            // Minimal success payload; you can expand to include token/user info
                            response.getWriter().write("{\"status\":\"ok\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            Objects.requireNonNull(request);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + (exception != null ? exception.getMessage() : "Auth failed") + "\"}");
                        })
                        .permitAll()
                )
                // Habilita o logout (cria o endpoint /logout)
                .logout(logout -> logout.permitAll())
                .httpBasic(Customizer.withDefaults()); // Habilita autenticação HTTP Basic também (útil para testes)

        return http.build();
    }
    private final UserDetailsServiceImpl userDetailsService;

    // Injeta o UserDetailsService
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura o AuthenticationManager para usar seu UserDetailsService e PasswordEncoder
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}
