package cbs.wantACoffe.dto.group;

/**
 * Interfaz para mostrar los datos del grupo.
 * Usamos la interfaz directamente en el {@link #IGroupRepo}
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public interface IGroupInfo {
    
    String getGroupName();

    Long getNumMembers();
}
