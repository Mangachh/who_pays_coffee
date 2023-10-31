package cbs.wantACoffe.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cbs.wantACoffe.dto.CreateGroup;
import cbs.wantACoffe.dto.GroupModel;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.service.auth.IAuthService;
import cbs.wantACoffe.service.group.IMemberService;
import cbs.wantACoffe.service.group.IGroupService;
import cbs.wantACoffe.service.user.IRegisteredUserService;
import cbs.wantACoffe.util.AuthUtils;
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
            Long userId = this.authService.getUserIdByToken(
                            AuthUtils.stringToToken(token));

            RegisteredUser user = this.userService.findById(userId);

            // Creamos primero el user-group
            // como estamos creando grupo, es admin sí o sí
            Member m = this.memberService.saveGroupMember(user, groupData.getMemberName(), true);

            // creamos el grupo
            Group g = Group.builder()
                            .groupName(groupData.getGroupName())
                            .members(List.of(m))
                            .build();

            m.setGroup(g);

            this.groupService.saveGroup(g);

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
     * @throws Exception -> TODO: cambiar por excepciones concretas
     */
    @GetMapping
    public ResponseEntity<List<GroupModel>> getAllGroupsByMember(
            @RequestHeader(AuthUtils.HEADER_AUTH_TXT) final String token) throws Exception {
        // pillamos id
        Long id = this.authService.getUserIdByToken(AuthUtils.stringToToken(token));
        RegisteredUser u = this.userService.findById(id);
        List<Group> groups = this.groupService.findAllByRegUser(u);
        List<GroupModel> model = groups.stream().map(g -> new GroupModel(g.getGroupId(), g.getGroupName())).toList();

        return ResponseEntity.ok().body(model);
    }
    
    @PostMapping(value = "add_member")
    public ResponseEntity<String> addMemberToGroup() {
        return null;
    }
}

