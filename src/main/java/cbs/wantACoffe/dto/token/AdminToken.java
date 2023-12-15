package cbs.wantACoffe.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Token del administrador. Se envia al cliente
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class AdminToken {
    private String head;
    private String token;
    private String username;
}
