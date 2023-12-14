package cbs.wantACoffe.service.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
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

/**
 * Test unitari de {@link AdminUserService}
 */
@ActiveProfiles("h2_test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class AdminUserServiceImplUnitTest {    
    
    @Autowired
    private AdminUserServiceImpl service;

    @Autowired
    private IEncryptService encrypt;

    private static AdminUser admin;

    @MockBean
    private IAdminUserRepo repo;

    @MockBean
    private IGroupRepo groupRepo;

    @MockBean
    private IRegisteredUserRepo userRepo;

    @BeforeAll
    static void addAdmin() {
        admin = CommonData.getTestAdmin();
    }
    


    @Test
    @Order(1)
    void testFindByUsername() throws UserNotExistsException {
        Mockito.when(repo.findByUsername(admin.getUsername()))
                .thenReturn(Optional.of(admin));

        AdminUser result = this.service.getByUsername(admin.getUsername());
        assertEquals(admin, result);
    }

    @Test
    @Order(2)
    void testFindByUsernameNotCorrect() {
        String badName = "asdasda";
        Mockito.when(repo.findByUsername(badName))
                .thenReturn(Optional.empty());

        assertThrows(UserNotExistsException.class,
                () -> this.service.getByUsername(admin.getUsername()));
    }
    
    @Test
    @Order(3)
    void testFindById() throws UserNotExistsException {
        Mockito.when(repo.findById(admin.getUserId()))
                .thenReturn(Optional.of(admin));

        AdminUser result = this.service.getById(admin.getUserId());
        assertEquals(admin, result);
    }
    
    @Test
    @Order(4)
    void testFindByIdNotCorrect() throws UserNotExistsException {
        Mockito.when(repo.findById(admin.getUserId()))
                .thenReturn(Optional.empty());
        
        assertThrows(UserNotExistsException.class,
        () -> this.service.getById(admin.getUserId()));
    }

    @Test
    @Order(5)
    void testFindByUsernameAndCheckPass() throws UserNotExistsException, IncorrectPasswordException {

        String encrypted = this.encrypt.encryptPassword(admin.getPassword());
        AdminUser expected = CommonData.getTestAdmin();
        expected.setPassword(encrypted);
        Mockito.when(this.repo.findByUsername(expected.getUsername()))
                .thenReturn(Optional.of(expected));

        AdminUser result = this.service.getByUsernameAndCheckPass(expected.getUsername(), admin.getPassword());
        assertEquals(expected, result);

    }

    @Test
    @Order(6)
    void testFindByUsernameAndCheckPassNotCorrect() throws UserNotExistsException, IncorrectPasswordException {

        //String encrypted = this.encrypt.encryptPassword(admin.getPassword());
        AdminUser expected = CommonData.getTestAdmin();
        expected.setPassword("No encriptao");
        Mockito.when(this.repo.findByUsername(expected.getUsername()))
                .thenReturn(Optional.of(expected));

        assertThrows(IncorrectPasswordException.class, 
        () -> this.service.getByUsernameAndCheckPass(expected.getUsername(), admin.getPassword()));

    }

    @Test
    @Order(7)
    void testFindAllRegisteredUsers() {
        // metemos unos users
        List<RegisteredUser> usersToAdd = CommonData
                .getRegUsersForGroupWithSuffix("_AdminUserServiceImplIntegraionTest");

        List<IBasicUserInfo> infos = new ArrayList<>();

        for(RegisteredUser u : usersToAdd){
            infos.add(new IBasicUserInfo() {
                @Override
                public String getEmail() {
                    return u.getEmail();
                }

                @Override
                public String getUsername(){
                    return u.getUsername();
                }
            });
        }
        Mockito.when(this.userRepo.findAllBasicData())
                .thenReturn(infos);
        
        List<IBasicUserInfo> users = this.service.getAllRegisteredUsers();

    
        assertEquals(usersToAdd.size(), users.size());
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

        assertEquals(users.size(), counter);
    }
    
    @Test
    @Order(8)
    void findAllGroupsAndCountMembers() {
        Group group = CommonData.getTestGroup("_AdminUserServiceImplUnitTest");
        Member member = Member.builder()
                .nickname("Prueba AdminUserIOmpl")
                .group(group)
                .build();
        assertTrue(group.tryAddMember(member));
        List<IGroupInfo> infos = new ArrayList<>();
        infos.add(new IGroupInfo() {

            @Override
            public String getGroupName() {
                return group.getGroupName();
            }

            @Override
            public Long getNumMembers() {
                return (long)group.getMembers().size();
            }            
        });

        Mockito.when(this.groupRepo.findAllGroupsAndCountMembers())
                .thenReturn(infos);

        List<IGroupInfo> result = this.service.getAllGroupsAndCountMembers();
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumMembers());
        assertEquals(group.getGroupName(), result.get(0).getGroupName());
    }
    
    @Test
    @Order(9)
    void testCountGroups() {
        // sólo hemos metido uno, en el de arriba
        Mockito.when(this.groupRepo.count())
                .thenReturn(1L);
        
        Long countGroups = this.service.countGroups();
        assertEquals(1, countGroups);
    }
    
    
    @Test
    @Order(10)
    void testCountRegisteredUsers() {
        long expected = 52;
        Mockito.when(this.userRepo.count())
                .thenReturn(expected);
        Long countRegUsers = this.service.countRegisteredUsers();
        assertEquals(expected, countRegUsers);
    }
}
