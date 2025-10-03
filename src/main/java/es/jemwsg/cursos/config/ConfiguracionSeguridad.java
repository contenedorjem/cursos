package es.jemwsg.cursos.config;

import es.jemwsg.cursos.config.seguridad.FiltroJwtAutenticacion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class ConfiguracionSeguridad {

    @Bean
    public SecurityFilterChain filtro(HttpSecurity http, FiltroJwtAutenticacion jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // API stateless
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Público: home, login estático y recursos estáticos
                .requestMatchers(
                    "/",
                    "/login.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                ).permitAll()
                // Público: Swagger/OpenAPI
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // Público: H2 console (solo dev)
                .requestMatchers("/h2-console/**").permitAll()
                // Público: endpoints de autenticación JWT
                .requestMatchers("/api/auth/**").permitAll()
                // Resto: requiere autenticación
                .anyRequest().authenticated()
            )
            // Necesario para que la consola H2 se muestre en un <frame>
            .headers(h -> h.frameOptions(f -> f.disable()));

        // Filtro JWT antes del de usuario/contraseña
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Delegante: acepta {noop}..., {bcrypt}..., {pbkdf2}..., etc. Ideal para despliegue de prueba.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
