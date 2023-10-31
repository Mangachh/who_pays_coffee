package cbs.wantACoffe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * DTO que enviamos al cliente al crear un grupo.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class GroupModel {
    
    private Long id;
    private String name;
}
