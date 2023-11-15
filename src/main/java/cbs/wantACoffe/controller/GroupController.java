package cbs.wantACoffe.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cbs.wantACoffe.dto.MemberGroup;
import cbs.wantACoffe.dto.group.CreateGroup;
import cbs.wantACoffe.dto.group.GroupModel;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.InvalidTokenFormat;
import cbs.wantACoffe.exceptions.MemberAdminTypeUnknown;
import cbs.wantACoffe.exceptions.MemberAlreadyIsInGroup;
import cbs.wantACoffe.exceptions.MemberIsNotAdmin;
import cbs.wantACoffe.exceptions.MemberNotInGroup;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.service.auth.IAuthService;
import cbs.wantACoffe.service.group.IMemberService;
import cbs.wantACoffe.service.group.IGroupService;
import cbs.wantACoffe.service.user.IRegisteredUserService;
import cbs.wantACoffe.util.AuthUtils;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("coffee/api/groups")
@RequiredArgsConstructor
/**
 * Controlador para la creación de grupos y la gestión de los miembros.
 * En un principio iba a tener dos controladores diferentes, pero como todo 
 * se hace desde el grupo creo que es más fácil a la hora de mantener
 * hacerlo todo en el mismo controlador
 * @author Lluís Cobos Aumatell
 * @version 0.2
 */
public class GroupController {

    private final IMemberService memberService;
    private final IGroupService groupService;
    private final IAuthService authService;
    private final IRegisteredUserService userService;

    private static final String TYPE_ADMIN = "admin";
    private static final String TYPE_MEMBER = "member";
    private static final String TYPE_ALL = "all";
        private final Logger log = LoggerFactory.getLogger(GroupController.class);

    /**
     * Añade un grupo a la base de datos.
     * @param token -> token de sesión del usuario.
     * @param groupData -> datos de grupo
     * @return -> {@link GroupModel}
     * @throws Exception -> TODO: cambiar la excepción general por concretas
     */
    @PostMapping(value = "add_group")
    public ResponseEntity<GroupModel> createGroup(@RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token, 
            @RequestBody CreateGroup groupData) throws Exception {
            
           
            // get user
            RegisteredUser user = this.getUserByToken(token);
            log.info("User {} wants to create a new group", user.getUsername());

            // Creamos primero el user-group
            // como estamos creando grupo, es admin sí o sí
            Member m = this.memberService.saveGroupMember(user,
                    groupData.getMemberName(),
            true);

            // creamos el grupo
            Group g = Group.builder()
                            .groupName(groupData.getGroupName())
                            .members(List.of(m))
                    .build();
            
            // ponemos el grupo al miembro
            m.setGroup(g);

            this.groupService.saveGroup(g);
            log.info("User {} creates a new group by name {}", user.getUsername(), g.getGroupName());
            return ResponseEntity.ok().body(
                            GroupModel.builder()
                                      .id(g.getGroupId())
                                      .name(g.getGroupName())
                                      .build());
    }
    
    /**
     * Devuelve todos los grupos a los que pertenece un usuario
     * @param token -> token de sesión del usuario
     * @return -> lista de grupos usando {@link GroupModel}
     */
    @GetMapping
    public ResponseEntity<List<GroupModel>> getAllGroupsByMember(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String token,
            @RequestParam final String type) throws Exception {
        // pillamos id

        if (type == null || type.isBlank()) {
            throw new MemberAdminTypeUnknown();
        }

        RegisteredUser user = this.getUserByToken(token);
        log.info("User {} tries to get all their groups", user.getUsername());
        List<Group> groups = switch (type) {
            case TYPE_ADMIN -> this.groupService.findAllByRegUserIsAdmin(user, true);
            case TYPE_MEMBER -> this.groupService.findAllByRegUserIsAdmin(user, false);
            case TYPE_ALL -> this.groupService.findAllByRegUser(user);
            default -> throw new MemberAdminTypeUnknown();
        };

        List<GroupModel> model = groups.stream().map(g -> new GroupModel(g.getGroupId(), g.getGroupName())).toList();
        log.info("User {} got {} groups", user.getUsername(), model.size());
        return ResponseEntity.ok().body(model);
    }
    
    /**
     * Añade un miembro al grupo
     * @param token
     * @param memberGroup
     * @return
     * @throws Exception
     */
    @PostMapping(value = "add_member")
    public ResponseEntity<String> addMemberToGroup(@RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token,
            @RequestBody(required = true) MemberGroup memberGroup) throws Exception {

        // pillamos el usuario que mete esto
        RegisteredUser userCaller = this.getUserByToken(token);

        // miramos que exista y sea admin
        Member adminMember = this.memberService.findMemberByGroupIdAndRegUserId(memberGroup.getGroupId(),
                userCaller.getUserId());

        if (adminMember == null) {
            throw new MemberNotInGroup();
        }

        if (adminMember.isAdmin() == false) {
            throw new MemberIsNotAdmin();
        }

        // mmm esto no me gusta... pero lo dejamos así.
        RegisteredUser newUser;
        try {
            newUser = this.userService.findByUsername(memberGroup.getUsername());
            log.info("The new user is a registered_member by name {}", newUser.getUsername());
        } catch (UserNotExistsException e) {
            newUser = null;
            log.info("The new user is not a registered user");
        }

        Group group = this.groupService.findGroupById(memberGroup.getGroupId());
        Member newMember = Member.builder()
                .nickname(memberGroup.getNickname())
                .group(group)
                .regUser(newUser)
                .build();

        if (group.tryAddMember(newMember)) {
            this.groupService.saveGroup(group);
            log.info("Member {} added correctly into group {}", newMember.getNickname(), group.getGroupName());
        } else {
            throw new MemberAlreadyIsInGroup();
        }

        return ResponseEntity.ok("New member added");
    }


    /**
     * Borra un grupo siempre que el usuario logeado sea miembro de este y además admin.
     * @param token -> token el usuario en sesión
     * @param groupId -> id del grupo a eliminar
     * @return -> mensaje
     * @throws InvalidTokenFormat -> si el token no tiene el formato correcto
     * @throws MemberNotInGroup -> si el miembro no está en el grupo
     * @throws MemberIsNotAdmin -> si el miembro SÍ está en el grupo, pero no es admin
     */
    @DeleteMapping("delete/group/{id}")
    public ResponseEntity<String> deleteGroup(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String token,
            @PathVariable(name = "id", required = true) final Long groupId) throws InvalidTokenFormat, MemberNotInGroup, MemberIsNotAdmin {
        
               
        Long userId = this.authService.getUserIdByToken(AuthUtils.stringToToken(token));
        Member member = this.memberService.findMemberByGroupIdAndRegUserId(groupId, userId);

        if (member == null) {
            log.error("No member exists in this group");
            throw new MemberNotInGroup();
        }

        // si no es admin
        if (member.isAdmin() == false) {
            log.error("The member is not admin");
            throw new MemberIsNotAdmin();
        }

        // ahora sí, borramos grupo...
        this.groupService.deleteGroup(groupId);
        return ResponseEntity.ok().body("Groupd deleted");
    }

    // delete group

    // mod reg_user
    
    /**
     * Devuelve un {@link RegisteredUser} a partir del token. 
     * Primero llama al auth y mira que esté en sesión, luego busca al user en la base de datos
     * @param token -> token del usuario
     * @return -> usuario en sesión
     * @throws InvalidTokenFormat -> si el formato del token es incorrecto
     * @throws UserNotExistsException -> si el usuario no existe
     */
    private RegisteredUser getUserByToken(final String token) throws InvalidTokenFormat, UserNotExistsException {
        Long id = this.authService.getUserIdByToken(AuthUtils.stringToToken(token));
        return this.userService.findById(id);
    }
}

