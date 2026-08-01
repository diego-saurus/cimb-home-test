package dev.diegosaurus.cimb.callmonitoring.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties properties;
    private final List<String> effectiveOrigins;

    public CorsConfig(CorsProperties properties, Environment environment) {
        this.properties = properties;
        String envOrigins = environment.getProperty("CORS_ALLOWED_ORIGINS");
        this.effectiveOrigins = (envOrigins != null && !envOrigins.isBlank())
                ? Arrays.stream(envOrigins.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList()
                : properties.getAllowedOrigins();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(effectiveOrigins.toArray(String[]::new))
                .allowedMethods(properties.getAllowedMethods().toArray(String[]::new))
                .allowedHeaders(properties.getAllowedHeaders().toArray(String[]::new))
                .allowCredentials(properties.isAllowCredentials())
                .maxAge(properties.getMaxAge());
    }
}
