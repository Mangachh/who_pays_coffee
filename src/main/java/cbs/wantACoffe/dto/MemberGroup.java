package cbs.wantACoffe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Clase con los datos necesarios para 
 * añadir un nuevo miembro al grupo.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class MemberGroup {
    
    private Long groupId;
    private Long userId;
    private String nickname;
    private String username;
    
    private Boolean isAdmin;
}
