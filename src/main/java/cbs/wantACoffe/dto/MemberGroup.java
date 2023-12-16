package cbs.wantACoffe.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Clase con los datos necesarios para 
 * añadir un nuevo miembro al grupo.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class MemberGroup {
    
    private Long groupId;
    private Long memberId;
    private String nickname;
    private String username;
    
    private Boolean isAdmin;
}
