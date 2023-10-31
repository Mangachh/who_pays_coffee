package cbs.wantACoffe.config;

import java.sql.SQLException;

import org.springframework.boot.autoconfigure.integration.IntegrationProperties.RSocket.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor

/**
 * configuración de la app. De momento sólo cramos un bean
 * para encriptar el password.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class AppConfig {
    
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    
}

   
