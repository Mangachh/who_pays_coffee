package cbs.wantACoffe.service.auth;

/**
 * Interfaz para el servicio de encriptación.
 * De momento sólo hace el password
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public interface IEncryptService {
    
    String encryptPassword(final String password);

    boolean isSamePassword(final String originalPass, final String cryptoPass);

}
