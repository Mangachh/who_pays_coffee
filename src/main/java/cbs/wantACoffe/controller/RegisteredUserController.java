package cbs.wantACoffe.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cbs.wantACoffe.dto.Token;
import cbs.wantACoffe.dto.Token.TokenType;
import cbs.wantACoffe.dto.user.RegisteredUserToken;
import cbs.wantACoffe.dto.user.UserPassword;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;
import cbs.wantACoffe.service.auth.IAuthService;
import cbs.wantACoffe.service.user.IRegisteredUserService;
import cbs.wantACoffe.util.AuthUtils;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Controlador del usuario.
 * <p>
 * Este controlador hace las siguientes operaciones:
 * <ul>
 * <li>Registra un nuevo usuario</li>
 * <li>Loguea a un nuevo usuario SI este existe en la BBDD</li>
 * <li>Hace Logout a un usuario</li>
 * <li>Elmina a un usuario</li>
 * </ul>
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@RestController
@RequestMapping("coffee/api/auth")
@RequiredArgsConstructor
public class RegisteredUserController {
    
    private final IRegisteredUserService userService;
    private final IAuthService authService;
    private final Logger log = LoggerFactory.getLogger(RegisteredUserController.class);

    /**
     * Añade a un usuario a la base de datos, genera un token de identificación
     *  y mete al token y al usuario en la sesión actual.
     * 
     * @param user -> {@link RegisteredUser} a añadir
     * @return -> {@link RegisteredUserToken}
     * @throws NullValueInUserDataException -> salta si los datos no están completos
     * @throws UsernameEmailAlreadyExistsException -> salta si el username/email ya existen en la bbdd
     */
    @PostMapping("p/register")    
    public ResponseEntity<RegisteredUserToken> registerUser(@RequestBody RegisteredUser user)
            throws NullValueInUserDataException, UsernameEmailAlreadyExistsException {
        RegisteredUser u = this.userService.saveNewUser(user);
        log.info("Created user {} with name {} and id {}", u.getEmail(), u.getUsername(), u.getUserId());
        Token token = this.authService.generateToken(u, TokenType.USER);
        log.info("Created token {} for user {}: {}", token, u.getUsername(), u.getEmail());
        this.authService.addUserTokenToSession(token, user);
        return ResponseEntity.ok().body(new RegisteredUserToken(token, u.getEmail(), u.getUsername()));

    }

    /**
     * Hace login de un usuario si este está en la base de datos, genera un token 
     * para el usuario y lo mete en la sesión actual
     * @param mail -> nombre del usuario para logear
     * @param pass -> password del usuario
     * @return -> {@link RegisteredUserToken}
     * @throws UserNotExistsException -> se lanza si no existe el user
     * @throws IncorrectPasswordException -> se lanza si el password no coincide con el de la bbdd
     */
    @PostMapping("p/login")
    public ResponseEntity<RegisteredUserToken> loginUser(@RequestBody RegisteredUser loginUser) throws UserNotExistsException, IncorrectPasswordException {

        log.info("Email {} wants to log {}", loginUser);
        RegisteredUser user = this.userService.findByEmailAndCheckPass(loginUser.getEmail(), loginUser.getPassword());
        log.info("Trying to log {}", user.getEmail());
        Token token = this.authService.generateToken(user, TokenType.USER);
        log.info("Token generated {} for user {}", token, user.getEmail());
        this.authService.addUserTokenToSession(token, user);
        return ResponseEntity.ok().body(new RegisteredUserToken(token, user.getEmail(), user.getUsername()));

    }

    /**
     * Quita al usuario y al token de la sesión actual.
     * @param token -> token a quitar
     * @return -> mensaje
     * @throws Exception
     */
    @PostMapping("logout")
    public ResponseEntity<String> logoutUser(@RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String header) throws Exception{
        Token token = AuthUtils.stringToToken(header);
        long userId = this.authService.getUserIdByToken(token);
        log.info("Login out user with id {}: ", userId);
        authService.removeTokenFromSession(token);
        log.info("Token {} is out of session");
        return ResponseEntity.ok().body("Session has ended");
    }

    /**
     * Quita al usuario y al token de la sesión actual
     * y borra al usuario de la base de datos.
     * <p>
     * Utilizamos el token que está almacenado en el header para que 
     * sólo se pueda borrar un usuario que está logeado. De esta manera
     * evitamos que cualquier usuario pueda borrar a otro usuario.
     * @param header -> header con el token de usuario<
     * @return -> mensaje
     * @throws Exception
     */
    @DeleteMapping("delete")
    public ResponseEntity<String> deleteUser(@RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String header)
            throws Exception {
        //String token = AuthUtils.extractTokenFromHeader(header);
        Token token = AuthUtils.stringToToken(header);
        Long userId = this.authService.getUserIdByToken(token);
        log.info("Deleting user with id {}", userId);
        // remove from database
        this.userService.deleteUserById(userId);
        // remove session
        this.authService.removeTokenFromSession(token);
        log.info("Removing token {} from session");
        log.info("User {} deleted from database.", userId);
        return ResponseEntity.ok().body("User deleted");
    }

    /**
     * Modificación del password. NO comprueba si es el mimso password 
     * que el anterior
     * @param header -> token de sesión
     * @param newPass -> nuevo password
     * @return
     * @throws NullValueInUserDataException -> Si el password está vacío, se lanza esto.
     */
    @PutMapping("modPassword")
    public ResponseEntity<String> modifyPassword(@RequestHeader(
                AuthUtils.HEADER_AUTH_TXT) final String header,
            @RequestBody final UserPassword newPass) throws Exception {
        
        if (newPass == null || newPass.getPassword().isEmpty()) {
            throw new NullValueInUserDataException();
        }
        
        Token token = AuthUtils.stringToToken(header);
        Long userId = this.authService.getUserIdByToken(token);
        RegisteredUser u = this.userService.findById(userId);
        u.setPassword(newPass.getPassword());
        this.userService.saveNewUser(u);
        return ResponseEntity.ok().body("New Password accepted");

    }
                
}
