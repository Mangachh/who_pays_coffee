package cbs.wantACoffe.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cbs.wantACoffe.dto.MemberGroup;
import cbs.wantACoffe.dto.group.CreateGroup;
import cbs.wantACoffe.dto.group.GroupModel;
import cbs.wantACoffe.dto.user.MemberUpdateNickname;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.GroupHasNoNameException;
import cbs.wantACoffe.exceptions.GroupNotExistsException;
import cbs.wantACoffe.exceptions.InvalidTokenFormat;
import cbs.wantACoffe.exceptions.MemberAdminTypeUnknown;
import cbs.wantACoffe.exceptions.MemberAlreadyIsInGroup;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.MemberIsNotAdmin;
import cbs.wantACoffe.exceptions.MemberNotInGroup;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.service.auth.IAuthService;
import cbs.wantACoffe.service.group.IMemberService;
import cbs.wantACoffe.service.group.IGroupService;
import cbs.wantACoffe.service.user.IRegisteredUserService;
import cbs.wantACoffe.util.AuthUtils;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
/**
 * Controlador para la creación de grupos y la gestión de los miembros.
 * En un principio iba a tener dos controladores diferentes, pero como todo
 * se hace desde el grupo creo que es más fácil a la hora de mantener
 * hacerlo todo en el mismo controlador
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class GroupController {

    private final IMemberService memberService;
    private final IGroupService groupService;
    private final IAuthService authService;
    public final IRegisteredUserService userService;

    private static final String TYPE_ADMIN = "admin";
    private static final String TYPE_MEMBER = "member";
    private static final String TYPE_ALL = "all";
    private final Logger log = LoggerFactory.getLogger(GroupController.class);

    /**
     * Añade un grupo a la base de datos.
     * 
     * @param token     -> token de sesión del usuario.
     * @param groupData -> datos de grupo
     * @return -> {@link GroupModel}
     * @throws UserNotExistsException
     * @throws InvalidTokenFormat
     * @throws MemberHasNoNicknameException
     * @throws GroupHasNoNameException
     */
    @PostMapping(value = "add/group")
    public ResponseEntity<GroupModel> createGroup(@RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token,
            @RequestBody CreateGroup groupData) throws InvalidTokenFormat, UserNotExistsException, MemberHasNoNicknameException, GroupHasNoNameException {

        // get user
        RegisteredUser user = this.authService.getUserByToken(token);
        log.info("User {} wants to create a new group", user.getUsername());
            
        if (groupData.getGroupName() == null || groupData.getGroupName().isBlank()) {
            throw new GroupHasNoNameException();
        }
        
        Member m = Member.builder()
                .regUser(user)
                .nickname(groupData.getMemberName())
                .isAdmin(true)
                .build();
        this.memberService.saveGroupMember(m);
        
        // creamos el grupo
        Group g = Group.builder()
                .groupName(groupData.getGroupName())
                .owner(m)
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
     * 
     * @param token -> token de sesión del usuario
     * @return -> lista de grupos usando {@link GroupModel}
     */
    @GetMapping("get/groups")
    public ResponseEntity<List<GroupModel>> getAllGroupsByMember(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String token,
            @RequestParam final String type) throws Exception {
        // pillamos id

        if (type == null || type.isBlank()) {
            throw new MemberAdminTypeUnknown();
        }

        RegisteredUser user = this.authService.getUserByToken(token);
        log.info("User {} tries to get all their groups", user.getUsername());
        List<Group> groups = switch (type) {
            case TYPE_ADMIN -> this.groupService.getAllByRegUserIsAdmin(user, true);
            case TYPE_MEMBER -> this.groupService.getAllByRegUserIsAdmin(user, false);
            case TYPE_ALL -> this.groupService.getAllByRegUser(user);
            default -> throw new MemberAdminTypeUnknown();
        };

        List<GroupModel> model = groups.stream().map(g -> new GroupModel(g.getGroupId(), g.getGroupName())).toList();
        log.info("User {} got {} groups", user.getUsername(), model.size());
        return ResponseEntity.ok().body(model);
    }

    /**
     * Añade un miembro al grupo
     * 
     * @param token
     * @param memberGroup
     * @return
     * @throws UserNotExistsException  -> lanzada si el usuario que quiere meter
     *                                 miembro no existe
     * @throws InvalidTokenFormat      -> token no valido
     * @throws MemberNotInGroup        -> lanzada si el usuario que quiere meter
     *                                 miembro no está en el grupo
     * @throws MemberIsNotAdmin        -> lanzada si el usuario que quiere meter
     *                                 miembro no es admin
     * @throws GroupNotExistsException -> lanzada si el grupo donde se quiere añadir
     *                                 a un miembro no existe
     * @throws MemberAlreadyIsInGroup  -> lanzada si el miembro a añadir ya está e
     *                                 el grupo. SÓLO funciona si el
     *                                 miembro es {@link RegisteredUser}
     * @throws GroupHasNoNameException
     * @throws MemberHasNoNicknameException
     */
    @PostMapping(value = "add/member")
    public ResponseEntity<String> addMemberToGroup(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) String token,
            @RequestBody(required = true) MemberGroup memberGroup) throws InvalidTokenFormat, UserNotExistsException,
            MemberNotInGroup, MemberIsNotAdmin, GroupNotExistsException, MemberAlreadyIsInGroup, GroupHasNoNameException, MemberHasNoNicknameException {

        // pillamos el usuario que mete esto
        RegisteredUser userCaller = this.authService.getUserByToken(token);
        
        if (memberGroup.getNickname() == null || memberGroup.getNickname().isBlank()) {
            throw new MemberHasNoNicknameException();
        }
        // miramos que exista y sea admin
        Member adminMember = this.memberService.getMemberByGroupIdAndRegUserId(memberGroup.getGroupId(),
                userCaller.getUserId());

        if (adminMember.isAdmin() == false) {
            throw new MemberIsNotAdmin();
        }

        // mmm esto no me gusta... pero lo dejamos así.
        RegisteredUser newUser;
        try {
            newUser = this.userService.getByUsername(memberGroup.getUsername());
            log.info("The new user is a registered_member by name {}", newUser.getUsername());
        } catch (UserNotExistsException e) {
            newUser = null;
            log.info("The new user is not a registered user");
        }

        Group group = this.groupService.getGroupById(memberGroup.getGroupId());
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
     * Borra un grupo siempre que el usuario logeado sea miembro de este y además
     * sea el owner
     * 
     * @param token   -> token el usuario en sesión
     * @param groupId -> id del grupo a eliminar
     * @return -> mensaje
     * @throws InvalidTokenFormat -> si el token no tiene el formato correcto
     * @throws MemberNotInGroup   -> si el miembro no está en el grupo
     * @throws MemberIsNotAdmin   -> si el miembro SÍ está en el grupo, pero no es
     *                            admin
     * @throws GroupNotExistsException
     */
    @DeleteMapping("delete/group/{id}")
    public ResponseEntity<String> deleteGroup(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String token,
            @PathVariable(name = "id", required = true) final Long groupId)
            throws InvalidTokenFormat, MemberNotInGroup, MemberIsNotAdmin, GroupNotExistsException {

        Long userId = this.authService.getUserIdByToken(AuthUtils.stringToToken(token));
        Member member = this.memberService.getMemberByGroupIdAndRegUserId(groupId, userId);
        Group group = this.groupService.getGroupById(groupId);
        
        if (group.getOwner() != member) {
            throw new MemberIsNotAdmin(); //TODO: change exception
        }

        // ahora sí, borramos grupo...
        this.groupService.deleteGroup(groupId);
        log.info("Group with id {} deleted. All members deleted too.", groupId);
        return ResponseEntity.ok().body("Groupd deleted");
    }

    /**
     * Devuelve todos los miembros de un grupo
     * @param token -> token de request
     * @param groupId -> grupo del que queremos pillar los miembors
     * @return -> Lista de {@link MemberGroup}
     * @throws InvalidTokenFormat
     * @throws UserNotExistsException
     * @throws GroupNotExistsException
     * @throws MemberNotInGroup
     */
    @GetMapping("get/members/group/{id}")
    public ResponseEntity<List<MemberGroup>> getAllMembersFromGroup(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String token,
            @PathVariable(name = "id", required = true) final Long groupId)
            throws InvalidTokenFormat, UserNotExistsException, GroupNotExistsException, MemberNotInGroup {
        // hacemos check de user...
        RegisteredUser user = this.authService.getUserByToken(token);

        // Pillamos grupo
        Group g = this.groupService.getGroupById(groupId);

        // siempre está bien hacer un check
        if (g.getMembers().stream().anyMatch(m -> m.getRegUser() == user) == false) {
            throw new MemberNotInGroup();
        }

        return ResponseEntity.ok().body(
            this.memberService.getAllMembersByGroupId(groupId)
        );
    }

    /**
     * Añade o modifica un {@link Member#setRegUser(RegisteredUser)}
     * @param token -> token del solicitante
     * @param memberGroup -> {@link MemberGroup} con la info
     * @return
     * @throws InvalidTokenFormat
     * @throws UserNotExistsException
     * @throws GroupNotExistsException
     * @throws MemberIsNotAdmin
     * @throws MemberNotInGroup
     * @throws MemberHasNoNicknameException
     */
    @PutMapping("add/reguser/member/from/group")
    public ResponseEntity<String> addRegUserToMember(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String token,
            @RequestBody(required = true) final MemberGroup memberGroup)
            throws InvalidTokenFormat, UserNotExistsException, GroupNotExistsException, MemberIsNotAdmin,
            MemberNotInGroup, MemberHasNoNicknameException {

        // get reguser that makes the request
        RegisteredUser registeredUser = this.authService.getUserByToken(token);

        // get group to update the member
        Group group = this.groupService.getGroupById(memberGroup.getGroupId());

        // if the requester is the same as the wanted change

        // is the requester an admin?
        if (group.getMembers().stream().anyMatch(member -> member.isAdmin() &&
                member.getRegUser() == registeredUser) == false) {
            throw new MemberIsNotAdmin();
        }

        // get the member to update
        Member memberToUpdate = group.getMembers()
                .stream()
                .filter(m -> m.getNickname().equals(memberGroup.getNickname()))
                .findFirst()
                .orElseThrow(MemberNotInGroup::new);

        // get its registereduser
        RegisteredUser userToMember = this.userService.getByUsername(memberGroup.getUsername());

        memberToUpdate.setRegUser(userToMember);

        this.memberService.saveGroupMember(memberToUpdate);
        return ResponseEntity.ok().body("Done");
    }
    
    /**
     * Modifica el nickame de un miembro siempre que el requester sea dicho miembro o el requester
     * sea un administrador
     * @param token
     * @param memberGroup
     * @return
     * @throws InvalidTokenFormat
     * @throws UserNotExistsException
     * @throws GroupNotExistsException
     * @throws MemberNotInGroup
     * @throws MemberIsNotAdmin
     * @throws MemberHasNoNicknameException
     */
    @PutMapping("update/nickname/group")
    public ResponseEntity<String> updateNickname(
        @RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String token,
        @RequestBody(required = true) final MemberUpdateNickname memberGroup) throws InvalidTokenFormat, UserNotExistsException, GroupNotExistsException, MemberNotInGroup, MemberIsNotAdmin, MemberHasNoNicknameException
    {
        // pillamos requester
        RegisteredUser registeredUser = this.authService.getUserByToken(token);

        // pillamos el grupo
        Group group = this.groupService.getGroupById(memberGroup.getGroupId());

        // pillamos al miembro que queremos cambiar el nickname
        Member toUpdate = this.memberService.getMemberByGroupIdAndNickname(
                memberGroup.getGroupId(),
                memberGroup.getOldNickname());

        if (toUpdate.getRegUser() != registeredUser &&
                group.getMembers().stream().anyMatch(member -> member.isAdmin() &&
                        member.getRegUser() == registeredUser) == false) {
            throw new MemberIsNotAdmin();
        }

        // hacemos el update
        toUpdate.setNickname(memberGroup.getNewNickname());
        this.memberService.saveGroupMember(toUpdate);
        return ResponseEntity.ok("Nickname changed to: " + toUpdate.getNickname());
    }
    
    /**
     * Borra un miembro de la base de datos. SOLO se puede borrar el requester
     * o un admin.
     * @param token
     * @param memberDeleteId
     * @param groupId
     * @return
     * @throws InvalidTokenFormat
     * @throws UserNotExistsException
     * @throws MemberNotInGroup
     * @throws MemberIsNotAdmin
     */
    @DeleteMapping("delete/member/from/group/{memberId}/{groupId}")
    public ResponseEntity<String> deleteGroupMember(
        @RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String token,
        @PathVariable(name = "memberId", required = true) final Long memberDeleteId,
        @PathVariable(name = "groupId", required = true) final Long groupId) throws InvalidTokenFormat, UserNotExistsException, MemberNotInGroup, MemberIsNotAdmin
    {
        log.info("Trying to delete the member {} from group {}", memberDeleteId, groupId);

        // ok, pillamos reguser
        RegisteredUser registeredUser = this.authService.getUserByToken(token);

        // pillamos el grupo
        Member memberToDelete = this.memberService.getMemberById(memberDeleteId);

        Member executor = memberToDelete;

        // si el reguser no es el mimso que el que queremos deletear
        if (memberToDelete.getRegUser() != registeredUser) {
            log.info("Member to delete {} doesn't belong to the reguser {}. Finding executor", memberDeleteId, registeredUser.getUserId());
            executor = this.memberService.getMemberByGroupIdAndRegUserId(groupId, registeredUser.getUserId());
            log.info("Executor found");
            if (executor.isAdmin() == false) {
                throw new MemberIsNotAdmin();
            }
        }

        log.info("Delete member {} ", memberDeleteId);
        this.memberService.deleteGroupMemberById(memberDeleteId);

        return ResponseEntity.ok("Deleted");

        
    }
}
