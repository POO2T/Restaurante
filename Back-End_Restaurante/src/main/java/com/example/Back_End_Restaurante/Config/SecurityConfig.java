package com.example.Back_End_Restaurante.Config;


import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays; // Import para 'Arrays.asList'

// Imports do Spring Security
import com.example.Back_End_Restaurante.Security.Jwt.JwtRequestFilter;
import com.example.Back_End_Restaurante.Security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Permite usar @PreAuthorize
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // O filtro que lê o token

    // --- Beans de Configuração ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    // --- Bean de CORS ("corsconfig") ---
    // Este é o bean que faltava no código do seu parceiro
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // A URL do seu Angular
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Headers permitidos (importante para Authorization)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // Permitir envio de credenciais (cookies, tokens)
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração para TODAS as rotas
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // --- Filtro Principal (A "Lei") ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Aplica a configuração de CORS que definimos acima
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Desabilita CSRF (necessário para APIs stateless)
                .csrf(csrf -> csrf.disable())

                // 3. Define a API como STATELESS (não usa sessão)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Define as REGRAS DE AUTORIZAÇÃO
                .authorizeHttpRequests(authorize -> authorize
                        // --- Endpoints PÚBLICOS (permitAll) ---
                        // (Qualquer um pode acessar, NÃO precisa de token)
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/comandas/visitante").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/mesas").permitAll() // Deixa todos VEREM as mesas
                        .requestMatchers(HttpMethod.GET, "/api/mesas/**").permitAll() // Deixa todos VEREM uma mesa
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permite preflight (OPTIONS)
                        .requestMatchers("/h2-console/**").permitAll() // Permite acesso ao H2

                        // --- Endpoints PROTEGIDOS (authenticated) ---
                        // (Qualquer outra requisição DEVE estar autenticada / enviar token)
                        .anyRequest().authenticated()
                )

                // Permite o H2 Console funcionar
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // 5. Adiciona o Filtro JWT
        // (Diz ao Spring para usar nosso filtro para ler o token ANTES de qualquer coisa)
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}