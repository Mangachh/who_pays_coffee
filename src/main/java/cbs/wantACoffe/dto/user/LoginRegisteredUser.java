package cbs.wantACoffe.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para el registro del usuraio. Esto es lo que recibe del cliente
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class LoginRegisteredUser {
    private String email;
    private String password;
}
