package cbs.wantACoffe.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cbs.wantACoffe.dto.AdminToken;
import cbs.wantACoffe.dto.Token;
import cbs.wantACoffe.dto.Token.TokenType;
import cbs.wantACoffe.dto.group.IGroupInfo;
import cbs.wantACoffe.dto.user.IBasicUserInfo;
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

    private final Logger log = LoggerFactory.getLogger(AdminController.class);
    
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
            log.info("Create admin {}", admin.getUsername());
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
        log.info("Logged Admin: {}", adminUser.getUsername());
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
        log.info("Test endpoint for admin");
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
        log.info("Logout admin");
        return ResponseEntity.ok().body("Bye Bye admin");

    }

    /**
     * Devuelve a todos los usuarios registrados en la aplicación con datos básicos
     * @return -> Lista de {@link IBasicUserInfo}
     */
    @GetMapping(value = "r/get/all/users")
    public ResponseEntity<List<IBasicUserInfo>> getAllUsers() {
        log.info("Admin getting all users");
        List<IBasicUserInfo> users = this.adminService.findAllRegisteredUsers();
        return ResponseEntity.ok().body(users);
    }
    
    /**
     * Devuelve el conteo de {@link RegisteredUser}
     * @return -> número de usuarios
     */
    @GetMapping(value = "r/count/users")
    public ResponseEntity<Long> getCountUsers() {
        log.info("Admin getting count of users");
        return ResponseEntity.ok().body(this.adminService.countRegisteredUsers());
    }

    /**
     * Devuele el conteo de {@link Group}
     * @return -> número de grupos
     */
    @GetMapping(value = "r/count/groups")
    public ResponseEntity<Long> getCountGroups() {
        log.info("Admin getting count of groups");
        return ResponseEntity.ok().body(this.adminService.countGroups());
    }

    /**
     * Devuelve todos los grupos dados de alta en la app
     * @return -> Lista de {@link IGroupInfo}
     */
    @GetMapping(value = "r/get/all/groups")
    public ResponseEntity<List<IGroupInfo>> getAllGroups() {
        return ResponseEntity.ok().body(this.adminService.findAllGroupsAndCountMembers());
    }
}
