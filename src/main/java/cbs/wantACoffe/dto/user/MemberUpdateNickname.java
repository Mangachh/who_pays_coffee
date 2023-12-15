package cbs.wantACoffe.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Clase con los datos necesarios para 
 * modificar el nickname de los miembros
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class MemberUpdateNickname {
    private Long groupId;
    private String oldNickname;
    private String newNickname;
}
