package cbs.wantACoffe.service.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.AdminUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.repository.IAdminUserRepo;
import cbs.wantACoffe.service.auth.IEncryptService;

/**
 * Test unitari de {@link AdminUserService}
 */
@ActiveProfiles("h2_test")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class AdminUserServiceImplUnitTest {    
    
    @Autowired
    private AdminUserServiceImpl service;

    @Autowired
    private IEncryptService encrypt;

    private static AdminUser admin;

    @MockBean
    private IAdminUserRepo repo;

    @BeforeAll
    static void addAdmin() {
        admin = CommonData.getTestAdmin();
    }
    


    @Test
    @Order(1)
    void testFindByUsername() throws UserNotExistsException {
        Mockito.when(repo.findByUsername(admin.getUsername()))
                .thenReturn(Optional.of(admin));

        AdminUser result = this.service.findByUsername(admin.getUsername());
        assertEquals(admin, result);
    }

    @Test
    @Order(2)
    void testFindByUsernameNotCorrect() {
        String badName = "asdasda";
        Mockito.when(repo.findByUsername(badName))
                .thenReturn(Optional.empty());

        assertThrows(UserNotExistsException.class,
                () -> this.service.findByUsername(admin.getUsername()));
    }
    
    @Test
    @Order(3)
    void testFindById() throws UserNotExistsException {
        Mockito.when(repo.findById(admin.getUserId()))
                .thenReturn(Optional.of(admin));

        AdminUser result = this.service.findById(admin.getUserId());
        assertEquals(admin, result);
    }
    
    @Test
    @Order(4)
    void testFindByIdNotCorrect() throws UserNotExistsException {
        Mockito.when(repo.findById(admin.getUserId()))
                .thenReturn(Optional.empty());
        
        assertThrows(UserNotExistsException.class,
        () -> this.service.findById(admin.getUserId()));
    }

    @Test
    @Order(5)
    void testFindByUsernameAndCheckPass() throws UserNotExistsException, IncorrectPasswordException {

        String encrypted = this.encrypt.encryptPassword(admin.getPassword());
        AdminUser expected = CommonData.getTestAdmin();
        expected.setPassword(encrypted);
        Mockito.when(this.repo.findByUsername(expected.getUsername()))
                .thenReturn(Optional.of(expected));

        AdminUser result = this.service.findByUsernameAndCheckPass(expected.getUsername(), admin.getPassword());
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
        () -> this.service.findByUsernameAndCheckPass(expected.getUsername(), admin.getPassword()));

    }

    @Test    
    void testFindAllRegisteredUsers() {
        //TODO
    }
}
