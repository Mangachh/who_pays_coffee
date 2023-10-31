package cbs.wantACoffe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cbs.wantACoffe.CoffeeApplication;
import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.AdminUser;

/**
 * Test unitario par {@link IAdminUserRepo}
 */
@ActiveProfiles("h2_test")
@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@TestMethodOrder(OrderAnnotation.class)
public class IAdminUserRepoUnitTest {

    @Autowired
    private IAdminUserRepo repo;

    private static AdminUser admin;
    
    @BeforeAll
    static void addAdmin() {
        admin = CommonData.getTestAdmin();
    }


    /**
     * Añade a un {@link AdminUser} a la bbdd.
     */
    @Test
    @Order(1)
    void testSaveUser() {
        AdminUser result = this.repo.save(admin);
        assertEquals(admin, result);
        admin = result;
    }

    /** 
     * Encuentra a un {@link AdminUser} por su username
     */
    @Test
    @Order(2)
    void testFindByUsername() {
        //AdminUser expected = admin;
        Optional<AdminUser> result = this.repo.findByUsername(admin.getUsername());
        assertTrue(result.isPresent());
        assertEquals(admin, result.get());
    }
    
    /** 
     * NO Encuentra a un {@link AdminUser} por su username
     */
    @Test
    @Order(3)
    void testFindByUsernameIncorrect() {
        Optional<AdminUser> result = this.repo.findByUsername("No existo");
        assertTrue(result.isEmpty());
    }
    
    /** 
     * Encuentra a un {@link AdminUser} por su id
     */
    @Test
    @Order(4)
    void testFindById() {
        Optional<AdminUser> result = this.repo.findById(admin.getUserId());
        assertTrue(result.isPresent());
        assertEquals(admin, result.get());
    }
    
    /** 
     * NO Encuentra a un {@link AdminUser} por su id
     */
    @Test
    @Order(5)
    void testFindByIdIncorrect() {
        Optional<AdminUser> result = this.repo.findById(admin.getUserId()+4);
        assertTrue(result.isEmpty());
    }
}
