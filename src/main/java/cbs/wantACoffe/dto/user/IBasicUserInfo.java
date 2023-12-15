package cbs.wantACoffe.dto.user;

/**
 * Interfaz para mostrar los datos básicos del usuario.
 * Utilizado en {@link IRegisteredUser}
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public interface IBasicUserInfo {
    String getUsername();

    String getEmail();
    
}
