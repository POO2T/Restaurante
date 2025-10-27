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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Habilita CORS usando a CorsConfigurationSource/CorsFilter definidos na aplicação
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs REST
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // REMOVA ou COMENTE esta linha por enquanto para usar o login padrão
            .authorizeHttpRequests(authorize -> authorize
                    // Permite preflight CORS (OPTIONS) para todas as rotas
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // Permite acesso PÚBLICO ao endpoint de cadastro de funcionários (POST) - Ajuste se necessário
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/funcionarios").permitAll()
                    // Permite acesso PÚBLICO ao endpoint de cadastro de clientes (POST) - Ajuste se necessário
                    .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/h2-console").permitAll()
                    // Você pode adicionar outras rotas públicas aqui (ex: GET /cardapio)
                    // .requestMatchers(HttpMethod.GET, "/api/produtos").permitAll()

                    // Qualquer outra requisição PRECISA estar autenticada
                    .anyRequest().authenticated()
            )
            // Habilita o login via formulário (que também cria o endpoint POST /login)
            .formLogin(form -> form
                            .loginProcessingUrl("/login") // URL onde o POST de login será enviado
                            .usernameParameter("email")    // Diz ao Spring que o campo de usuário se chama 'email'
                            .passwordParameter("senha")    // Diz ao Spring que o campo de senha se chama 'senha' (ou deixe 'password' se preferir)
                            .permitAll() // Permite acesso à URL de login
                    // Você pode adicionar .defaultSuccessUrl("/alguma-url-apos-login") ou handlers de sucesso/falha
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