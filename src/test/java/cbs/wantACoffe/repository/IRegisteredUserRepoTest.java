package cbs.wantACoffe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
import cbs.wantACoffe.entity.RegisteredUser;

/**
 * Pruebas del repositorio {@link IRegisteredUserRepo}
 */

@ActiveProfiles("h2_test")
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@TestMethodOrder(OrderAnnotation.class)
public class IRegisteredUserRepoTest {

    @Autowired
    private IRegisteredUserRepo repo;

    private static RegisteredUser testUser = CommonData.getTestUserWithSuffix("IRegisteredUserRepoTests");
   

    static void checkTestUserWithResut(final RegisteredUser result) {
        //assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPassword(), result.getPassword());
    }

    /**
     * Añade un nuevo {@link RegisteredUser}de manera correcta.
     */
    @Test
    @Order(1)
    void testSaveCorrect() {        
        RegisteredUser result = this.repo.save(testUser);
        checkTestUserWithResut(result);
        assertEquals(testUser, result);
    }

    /**
     * Encuentra a un {@link RegisteredUser} ya presente en la base de datos 
     * de manera correcta
     */
    @Test
    @Order(2)
    void testFindByIdCorrect() {
        Long id = testUser.getUserId();
        Optional<RegisteredUser> result = this.repo.findById(id);
        assertTrue(result.isPresent());
        RegisteredUser uResult = result.get();
        checkTestUserWithResut(uResult);

    }

    /**
     * NO encuentra a un {@link RegisteredUser} porque el id es incorrecto
     */
    @Test
    @Order(3)
    void testFindByIdNoFound() {
        Long id = 5474L;
        Optional<RegisteredUser> result = this.repo.findById(id);
        assertTrue(result.isEmpty());
    }

    /**
     * Encuentra a un {@link RegisteredUser} por su {@link RegisteredUser#getEmail()}
     */
    @Test
    @Order(4)
    void testFindByEmailCorrect() {
        String email = testUser.getEmail();
        Optional<RegisteredUser> result = this.repo.findByEmail(email);
        assertTrue(result.isPresent());
        checkTestUserWithResut(result.get());
    }

    /**
     * NO encuentra a un {@link RegisteredUser} por su {@link RegisteredUser#getEmail()}
     */
    @Test
    @Order(5)
    void testFindByEmailNoFound() {
        String email = "inventado@mail.es";
        Optional<RegisteredUser> result = this.repo.findByEmail(email);
        assertTrue(result.isEmpty());
    }

    /**
     * Encuentra a un {@link RegisteredUser} por su {@link RegisteredUser#getUsername()}
     */
    @Test
    @Order(6)
    @Deprecated
    void testFindByUsernameCorrect() {
        String username = testUser.getUsername();
        Optional<RegisteredUser> result = this.repo.findByUsername(username);
        assertTrue(result.isPresent());
        checkTestUserWithResut(result.get());
    }

    /**
     * NO encuentra a un {@link RegisteredUser} por su {@link RegisteredUser#getEmail()}
     */
    @Test
    @Order(7)
    @Deprecated
    void testFindByUsernameNoFound() {
        String username = "No name for you";
        Optional<RegisteredUser> result = this.repo.findByUsername(username);
        assertTrue(result.isEmpty());
    }

    /**
     * Elimina a un {@link RegisteredUser}
     */
    @Test
    @Order(8)
    void testDeleteUserCorrect() {
        this.repo.delete(testUser);
        //List<RegisteredUser> all = this.repo.findAll();
        //assertEquals(0, all.size());
        // try get user
        Optional<RegisteredUser> user = this.repo.findByEmail(testUser.getEmail());
        assertTrue(user.isEmpty());
    }
}
