package cbs.wantACoffe.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Clase neesaria para cambiar el password
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class UserPassword {
    private String password;
}
