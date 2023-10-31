package cbs.wantACoffe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
/**
 * Configuración para desactivar el cors. De esta manera
 * se puede acceder a los endpoints desde cualquier lugar.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class WebConfig implements WebMvcConfigurer {
    
     @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }
}
