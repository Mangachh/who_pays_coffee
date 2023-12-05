package cbs.wantACoffe.service.auth;
import cbs.wantACoffe.dto.token.Token;
import cbs.wantACoffe.dto.token.Token.TokenType;
import cbs.wantACoffe.entity.IUser;

/**
 * Interfaz para el servicio de autentificación.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public interface IAuthService {
    /**
     * Genera un token a partir del usuario
     * @param user -> usuario para generar token
     * @return token generado
     */
    Token generateToken(final IUser user, final TokenType type);

    /**
     * Añade a un usuario y su token a la sessión
     * @param token -> token a añadir
     * @param user -> user a añadir
     */
    void addUserTokenToSession(final Token token, final IUser user);

    /**
     * Quita un token de la session
     * @param token -> token a quitar
     */
    void removeTokenFromSession(final Token token); 

    /**
     * Comprueba si el usuario está en sessión
     * @param user -> usuari a checkear
     * @return está en sesión?
     */
    boolean isUserInSession(final IUser user);

    /**
     * Comprueba si el usuario está en sessión
     * @param user -> usuari a checkear
     * @return está en sesión?
     */
    boolean isUserInSession(final Long userId);

    /**
     * Comprueba si un token determinado está en sesión
     * @param token -> token a comprobar
     * @return está en sesión?
     */
    boolean isTokenInSession(final Token token);

    /**
     * Encuentra el id de un usuario a partir de un token
     * @param token -> token con el que buscar
     * @return userId
     */
    Long getUserIdByToken(final Token token);
}
