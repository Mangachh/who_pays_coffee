package cbs.wantACoffe.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * DTO necesario para logear un user. Esta clase es la que recibe de la request.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class LoginAdminUser {
    private String username;
    private String password;
}
