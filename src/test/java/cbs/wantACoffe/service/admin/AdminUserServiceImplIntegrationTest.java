package cbs.wantACoffe.service.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.dto.group.IGroupInfo;
import cbs.wantACoffe.dto.user.IBasicUserInfo;
import cbs.wantACoffe.entity.AdminUser;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.repository.IAdminUserRepo;
import cbs.wantACoffe.repository.IGroupRepo;
import cbs.wantACoffe.repository.IRegisteredUserRepo;
import cbs.wantACoffe.service.auth.IEncryptService;

@ActiveProfiles("h2_test")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class AdminUserServiceImplIntegrationTest {    
    
    @Autowired
    private AdminUserServiceImpl service;

    @Autowired
    private IEncryptService encrypt;

    private static AdminUser admin;

    @Autowired
    private IAdminUserRepo adminRepo;

    @Autowired
    private IGroupRepo groupRepo;

    @Autowired
    private IRegisteredUserRepo regUserRepo;

    private static long defSizeGroups;
    private static long defSizeRegUsers;

    @BeforeAll
    static void addAdmin() {
        admin = CommonData.getTestAdmin();
        admin.setUserId(null);
    }
    
    

    @Test
    @Order(1)
    void testFindByUsername() throws UserNotExistsException {

        // ponemos los defaults aquí 
        defSizeGroups = this.groupRepo.count();
        defSizeRegUsers = this.regUserRepo.count();

        // add the user
        AdminUser toSave = CommonData.getTestAdmin();
        toSave.setPassword(encrypt.encryptPassword(toSave.getPassword()));
        this.adminRepo.save(toSave);
        AdminUser result = this.service.getByUsername(admin.getUsername());
        assertEquals(admin.getUsername(), result.getUsername());

        // seteamos la id
        admin.setUserId(result.getUserId());
        
    }

    @Test
    @Order(2)
    void testFindByUsernameNotCorrect() {
        String badName = "asdasda";        
        assertThrows(UserNotExistsException.class,
                () -> this.service.getByUsername(badName));
    }
    
    @Test
    @Order(3)
    void testFindById() throws UserNotExistsException {        
        AdminUser result = this.service.getById(admin.getUserId());
        assertEquals(admin.getUsername(), result.getUsername());
    }
    
    @Test
    @Order(4)
    void testFindByIdNotCorrect() throws UserNotExistsException {
        
        assertThrows(UserNotExistsException.class,
        () -> this.service.getById(admin.getUserId() + 200));
    }

    @Test
    @Order(5)
    void testFindByUsernameAndCheckPass() throws UserNotExistsException, IncorrectPasswordException {

        AdminUser result = this.service.getByUsernameAndCheckPass(admin.getUsername(), admin.getPassword());
        assertEquals(admin.getUsername(), result.getUsername());

    }

    @Test
    @Order(6)
    void testFindByUsernameAndCheckPassNotCorrect() throws UserNotExistsException, IncorrectPasswordException {

        //String encrypted = this.encrypt.encryptPassword(admin.getPassword());
        AdminUser expected = CommonData.getTestAdmin();
        expected.setPassword("No encriptao");

        assertThrows(IncorrectPasswordException.class,
                () -> this.service.getByUsernameAndCheckPass(admin.getUsername(), "My new password ajaja"));

    }

    @Test
    @Order(7)
    void testFindAllRegisteredUsers() {
        // metemos unos users
        List<RegisteredUser> usersToAdd = CommonData
                .getRegUsersForGroupWithSuffix("_AdminUserServiceImplIntegraionTest");

        usersToAdd.stream().forEach(this.regUserRepo::save);

        List<IBasicUserInfo> users = this.service.getAllRegisteredUsers();
        assertEquals(usersToAdd.size() + defSizeRegUsers, users.size());
        int counter = 0;

        // comprobamos que existam, hacemos dos bucles, más sencillo
        // a cada acierto se suma 1 al counter, de esta manera al cabar
        // el counter debería ser igual a la lista de resultados
        for (IBasicUserInfo info : users) {
            for (RegisteredUser u : usersToAdd) {
                if (u.getUsername().equals(info.getUsername()) &&
                        u.getEmail().equals(info.getEmail())) {
                    counter++;
                    break;
                }
            }
        }

        assertEquals(usersToAdd.size(), counter);
    }
    
    @Test
    @Order(8)
    void findAllGroupsAndCountMembers() {
        Group group = CommonData.getTestGroup();
        Member member = Member.builder()
                .nickname("Prueba AdminUserIOmpl")
                .group(group)
                .build();
        assertTrue(group.tryAddMember(member));
        this.groupRepo.save(group);

        List<IGroupInfo> groupInfos = this.service.getAllGroupsAndCountMembers();
        assertEquals(1 + defSizeGroups, groupInfos.size());
        // tengo que buscar el grupo que quiero
        IGroupInfo result = null;
        for (IGroupInfo info : groupInfos) {
            if (info.getGroupName().equals(group.getGroupName())) {
                result = info;
            }
        }
        assertEquals(1, result.getNumMembers());
        assertEquals(group.getGroupName(), result.getGroupName());
    }
    
    @Test
    @Order(9)
    void testCountGroups() {
        // sólo hemos metido uno, en el de arriba
        Long countGroups = this.service.countGroups();
        assertEquals(1 + defSizeGroups, countGroups);
    }
    
    @Test
    @Order(10)
    void testCountRegisteredUsers() {
        int expected = CommonData.getRegUsersForGroupWithSuffix("").size();
        Long countRegUsers = this.service.countRegisteredUsers();
        assertEquals(expected + defSizeRegUsers, countRegUsers);
    }
    
}
