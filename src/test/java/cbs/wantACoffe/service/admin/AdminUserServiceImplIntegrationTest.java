package cbs.wantACoffe.service.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.AdminUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.repository.IAdminUserRepo;
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

    @BeforeAll
    static void addAdmin() {
        admin = CommonData.getTestAdmin();
        admin.setUserId(null);
    }
    
    

    @Test
    @Order(1)
    void testFindByUsername() throws UserNotExistsException {
        // add the user
        //AdminUser toSave = CommonData.getTestAdmin();
        //toSave.setPassword(encrypt.encryptPassword(toSave.getPassword()));
        //this.adminRepo.save(toSave);
        
        AdminUser result = this.service.findByUsername(admin.getUsername());
        assertEquals(admin.getUsername(), result.getUsername());

        // seteamos la id
        admin.setUserId(result.getUserId());
        
    }

    @Test
    @Order(2)
    void testFindByUsernameNotCorrect() {
        String badName = "asdasda";        
        assertThrows(UserNotExistsException.class,
                () -> this.service.findByUsername(badName));
    }
    
    @Test
    @Order(3)
    void testFindById() throws UserNotExistsException {        
        AdminUser result = this.service.findById(admin.getUserId());
        assertEquals(admin.getUsername(), result.getUsername());
    }
    
    @Test
    @Order(4)
    void testFindByIdNotCorrect() throws UserNotExistsException {
        
        assertThrows(UserNotExistsException.class,
        () -> this.service.findById(admin.getUserId() + 200));
    }

    @Test
    @Order(5)
    void testFindByUsernameAndCheckPass() throws UserNotExistsException, IncorrectPasswordException {

        AdminUser result = this.service.findByUsernameAndCheckPass(admin.getUsername(), admin.getPassword());
        assertEquals(admin.getUsername(), result.getUsername());

    }

    @Test
    @Order(6)
    void testFindByUsernameAndCheckPassNotCorrect() throws UserNotExistsException, IncorrectPasswordException {

        //String encrypted = this.encrypt.encryptPassword(admin.getPassword());
        AdminUser expected = CommonData.getTestAdmin();
        expected.setPassword("No encriptao");

        assertThrows(IncorrectPasswordException.class, 
        () -> this.service.findByUsernameAndCheckPass(admin.getUsername(), "My new password ajaja"));

    }

    @Test    
    void testFindAllRegisteredUsers() {
        //TODO
    }
}
