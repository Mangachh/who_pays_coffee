package cbs.wantACoffe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cbs.wantACoffe.dto.AdminToken;
import cbs.wantACoffe.dto.Token;
import cbs.wantACoffe.dto.Token.TokenType;
import cbs.wantACoffe.dto.user.BasicUserInfo;
import cbs.wantACoffe.dto.user.LoginAdminUser;
import cbs.wantACoffe.entity.AdminUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.repository.IAdminUserRepo;
import cbs.wantACoffe.service.admin.IAdminService;
import cbs.wantACoffe.service.auth.IAuthService;
import cbs.wantACoffe.service.auth.IEncryptService;
import cbs.wantACoffe.service.group.IGroupService;
import cbs.wantACoffe.service.user.IRegisteredUserService;
import cbs.wantACoffe.util.AuthUtils;
import jakarta.annotation.PostConstruct;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


/**
 * Controlador del usuario administrador. Aquí están todas las operaciones 
 * que puede realizar el admin.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@RestController
@RequestMapping("coffee/api/admin")
public class AdminController {

    @Autowired
    private IAdminUserRepo repo;

    @Autowired
    private IEncryptService encrypt;

    @Autowired
    private IAuthService auth;

    @Autowired
    private IAdminService adminService;
    
    /**
     * Añadimos un {@link AdminUser}. De momento lo metemos así.
     */
    @PostConstruct
    private void addUSer() {
        AdminUser admin = AdminUser.builder().username("Lluís").password(
                encrypt.encryptPassword("1234")).build();
        try{
            this.repo.save(admin);
            // Generate token
            Token t = this.auth.generateToken(admin, TokenType.ADMIN);
            auth.addUserTokenToSession(t, admin);
            System.out.println(t.toString());
        } catch (Exception e) {
            
        }
        
    }

    /**
     * Endpoint de logueo del {@link AdminUser}. Es un endpoint público.
     * 
     * @param admin -> datos de logueo, utiliza {@link LoginAdminUser}
     * @return -> {@link Admintoken}
     * @throws UserNotExistsException -> se lanza si no existe el usuario
     * @throws IncorrectPasswordException -> se lanza si el password es incorrecto
     */
    @PostMapping(value="p/login")
    public ResponseEntity<AdminToken> loginAdmin (@RequestBody LoginAdminUser admin) throws UserNotExistsException, IncorrectPasswordException {
        AdminUser adminUser = this.adminService.findByUsernameAndCheckPass(admin.getUsername(), 
        admin.getPassword());

        Token t = this.auth.generateToken(adminUser, TokenType.ADMIN);
        auth.addUserTokenToSession(t, adminUser);
        return ResponseEntity.ok().body(
            AdminToken.builder().username(admin.getUsername())
            .head(t.getType().getHead())
            .token(t.getBody())
            .build()
        );
    }
    
    /**
     * Endpoint de test.
     * 
     * @return -> string de confirmación
     */
    @GetMapping(value="r")
    public ResponseEntity<String> adminTest() {
        return ResponseEntity.ok().body("Eres un buen admin tio");
    }

    /**
     * Endpoint que desloguea al admin
     * @param header -> token de admin
     * @return -> string con mensaje
     * @throws Exception
     */
    @PostMapping(value = "r/logout")
    public ResponseEntity<String> logoutAdmin(@RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String header)
            throws Exception {
        Token t = AuthUtils.stringToToken(header);
        //long id = this.auth.getUserIdByToken(t);
        auth.removeTokenFromSession(t);
        return ResponseEntity.ok().body("Bye Bye admin");

    }

    /**
     * Devuelve a todos los usuarios registrados en la aplicación con datos básicos
     * @return
     */
    @GetMapping(value = "r/getAllUsers")
    public ResponseEntity<List<BasicUserInfo>> getAllUsers() {

        List<BasicUserInfo> users = this.adminService.findAllRegisteredUsers();
        return ResponseEntity.ok().body(users);
    }
    
    @GetMapping(value = "r/countUsers")
    public ResponseEntity<Long> getCountUsers() {
        return ResponseEntity.ok().body(this.adminService.countRegisteredUsers());
    }

    @GetMapping(value = "r/countGroups")
    public ResponseEntity<Long> getCountGroups() {
        return ResponseEntity.ok().body(this.adminService.countGroups());
    }
}
