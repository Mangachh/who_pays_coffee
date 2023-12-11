package cbs.wantACoffe.service.auth;
import cbs.wantACoffe.dto.token.Token;
import cbs.wantACoffe.dto.token.Token.TokenType;
import cbs.wantACoffe.entity.IUser;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.InvalidTokenFormat;
import cbs.wantACoffe.exceptions.UserNotExistsException;

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

    /**
     * Devuelve un {@link RegisteredUser} a partir del token.
     * Primero llama al auth y mira que esté en sesión, luego busca al user en la
     * base de datos
     * 
     * @param groupController TODO
     * @param token -> token del usuario
     * @return -> usuario en sesión
     * @throws InvalidTokenFormat     -> si el formato del token es incorrecto
     * @throws UserNotExistsException -> si el usuario no existe
     */
    RegisteredUser getUserByToken(final String token) throws InvalidTokenFormat, UserNotExistsException;

        
    
}
