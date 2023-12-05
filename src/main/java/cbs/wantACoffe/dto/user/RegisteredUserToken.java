package cbs.wantACoffe.dto.user;

import cbs.wantACoffe.dto.token.Token;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model que tiene como campos un token y el nombre de usuario.
 * Esta clase es la que se enviará a los clientes que logeen o
 * registren a un usuario. 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteredUserToken {

    /**
     * Header del token
     */
    private String head;

    /**
     * Token del usuario
     */
    private String token;

    /**
     * Email del usuario. Se 
     * usa como identificación
     */
    private String email;

    /**
     * Nombre del usuario. No tiene 
     * más uso que mostrarlo en la app
     * o ponerlo como defecto en los grupos
     */
    private String name;

    public RegisteredUserToken(final Token token, final String email, final String name) {
        this.head = token.getType().getHead();
        this.token = token.getBody();
        this.email = email;
        this.name = name;
    }
}
