package cbs.wantACoffe.dto.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Clase con los datos necesarios para crear un grupo
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class CreateGroup {
    
    private String memberName;
    private String groupName;
}
