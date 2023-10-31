package cbs.wantACoffe.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;

/**
 * Test de integración de {@link UserServiceImpl}
 */
@ActiveProfiles("h2_test")
@SpringBootTest()
@TestMethodOrder(OrderAnnotation.class)
public class RegisteredUserServiceImplIntegrationTest {

    @Autowired
    private RegisterdUserServiceImpl userService;

    private static RegisteredUser testUser = CommonData.getTestUserWithSuffix("_RegisteredUserServiceImplIntegrationTest");

    /**
     * Guarda un nuevo {@link RegisteredUser}
     * @throws NullValueInUserDataException
     * @throws UsernameEmailAlreadyExistsException
     */
    @Test
    @Order(1)
    void testSaveNewUser() throws NullValueInUserDataException, UsernameEmailAlreadyExistsException {
        RegisteredUser result = this.userService.saveNewUser(testUser);
        assertNotNull(result);
        this.compareTestUserWithResult(result);
    }

    /**
     * NO guarda un nuevo usuario porque su {@link RegisteredUser#username} esta vacio
     */
    @Test
    @Order(2)
    @Deprecated
    void testSaveNewUserNullUsername() {
        RegisteredUser u = RegisteredUser.builder()
                .email("pepote@mail.es")
                .password("123")
                .build();

        assertThrows(NullValueInUserDataException.class,
                () -> this.userService.saveNewUser(u));
    }

    /**
     * NO guarda un nuevo usuario porque su {@link RegisteredUser#email} esta vacio
     */
    @Test
    @Order(3)
    void testSaveNewUserNullEmail() {
        RegisteredUser u = RegisteredUser.builder()
                //.username("Pepote")
                .password("123")
                .build();

        assertThrows(NullValueInUserDataException.class,
                () -> this.userService.saveNewUser(u));
    }

    /**
     * NO guarda un nuevo usuario porque su {@link RegisteredUser#password} esta vacio
     */
    @Test
    @Order(4)
    void testSaveNewUserNullPassword() {
        RegisteredUser u = RegisteredUser.builder()
                //.username("Pepote")
                .email("marimari@mail.es")
                .build();

        assertThrows(NullValueInUserDataException.class,
                () -> this.userService.saveNewUser(u));
    }

    /**
     * NO guarda un nuevo usuario porque su {@link RegisteredUser#username} ya existe
     */
    @Test
    @Order(5)
    @Deprecated
    void testSaveNewUsernameAlreadyExists() {

        RegisteredUser u = RegisteredUser.builder()
                .username(testUser.getUsername())
                .email("otro_correo")
                .password("123")
                .build();

        assertThrows(UsernameEmailAlreadyExistsException.class,
         () -> this.userService.saveNewUser(u));

    }

    
    /**
     * NO guarda un nuevo usuario porque su {@link RegisteredUser#email} ya existe
     */
    @Test
    @Order(6)
    void testSaveNewEmailAlreadyExists() {
        RegisteredUser u = RegisteredUser.builder()
                .username("Otro nombre")
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        assertThrows(UsernameEmailAlreadyExistsException.class,
                () -> this.userService.saveNewUser(u));
    }

    /**
     * Encuentra a un {@link RegisteredUser#username} por su id
     */
    @Test
    @Order(7)
    void testFindById() throws UserNotExistsException {
        RegisteredUser result = this.userService.findById(testUser.getUserId());
        assertNotNull(result);
        this.compareTestUserWithResult(result);
    }

    /**
     * Encuentra a un {@link RegisteredUser#username} por su id porque no existe
     */
    @Test
    @Order(8)
    void testFindByIdNoExists() throws UserNotExistsException {
        assertThrows(UserNotExistsException.class,
                () -> this.userService.findById(548L));
    }

    /**
     * Encuentra a un {@link RegisteredUser} por su email
     */
    @Test
    @Order(9)
    void testFindByEmail() throws UserNotExistsException {
        RegisteredUser result = this.userService.findByEmail(testUser.getEmail());
        assertNotNull(result);
        this.compareTestUserWithResult(result);
    }

    /**
     * NO Encuentra a un {@link RegisteredUser} por su email
     * porque no existe
     */
    @Test
    @Order(10)
    void testFindByEmailNoExists() {
        assertThrows(UserNotExistsException.class,
                () -> this.userService.findByEmail("ESTE_CORRE_NO_EXISTE"));
    }

    /**
     * Encuentra a un {@link RegisteredUser} por su isername
     */
    @Test
    @Order(11)
    @Deprecated
    void testFindByUsernamel() throws UserNotExistsException {

        RegisteredUser result = this.userService.findByUsername(testUser.getUsername());
        assertNotNull(result);
        this.compareTestUserWithResult(result);
    }

    /**
     * NO Encuentra a un {@link RegisteredUser} por su username
     * porque no existe
     */
    @Test
    @Order(12)
    void testFindByUsernameNoExists() {
        assertThrows(UserNotExistsException.class,
                () -> this.userService.findByEmail("NOMBRE_DESCONOCIOD"));
    }

    /**
     * Encuentra a un {@link RegisteredUser} por su email
     * y comprueba que el password sea correcto
     */
    @Test
    @Order(13)
    void testFindByEmailAndCheckPass() throws UserNotExistsException, IncorrectPasswordException {
        RegisteredUser toSave = CommonData.getTestUserWithSuffix("_RegisteredUserServiceImplIntegrationTest");
        System.out.println("\n--------------------------------\n" + toSave.getPassword());
        RegisteredUser result = this.userService.findByEmailAndCheckPass(
                toSave.getEmail(),
                toSave.getPassword());

        assertNotNull(result);
        this.compareTestUserWithResult(result);
    }

    /**
     * NO Encuentra a un {@link RegisteredUser} por su email
     * y comprueba que el password sea correcto, y no lo es
     */
    @Test
    @Order(14)
    void testFindByEmaiAndCheckPassIncorrectPass() {
        assertThrows(IncorrectPasswordException.class,
                () -> this.userService.findByEmailAndCheckPass(testUser.getEmail(), "PASSWORD MALO!"));
    }

    /**
     * NO Encuentra a un {@link RegisteredUser} por su email
     * porque el username no existe
     */
    @Test
    @Order(15)
    void testFindByEmailAndCheckPassNoUser() {
        assertThrows(UserNotExistsException.class,
            () -> this.userService.findByEmailAndCheckPass("NO EXISTO!!!", testUser.getEmail()));
    }


    /**
     * Borra un {@link RegisteredUser} por su id
     */
    @Test
    @Order(16)
    void testDeleteUserById() throws UserNotExistsException {
        Long id = testUser.getUserId();
        // comprobamos que el usuario existe pillándolo
        RegisteredUser exists = this.userService.findById(id);
        assertNotNull(exists);
        // ahora borramos
        this.userService.deleteUserById(exists.getUserId());
        //al querer pillar de nuevo el user, debería dar excepción

        assertThrows(UserNotExistsException.class,
                () -> this.userService.findById(id));
    }

    /**
     * Borra a un {@link RegisteredUser} 
     */
    @Test
    @Order(17)
    void testDeleteUser() throws UserNotExistsException, NullValueInUserDataException, UsernameEmailAlreadyExistsException {
        // como hemos quitado usuario, lo metemos de nuevor
        this.userService.saveNewUser(testUser);

        // comprobamos que el usuario existe pillándolo
        RegisteredUser exists = this.userService.findById(testUser.getUserId());
        assertNotNull(exists);

        // ahora borramos
        this.userService.deleteUserById(exists.getUserId());
        //al querer pillar de nuevo el user, debería dar excepción
        assertThrows(UserNotExistsException.class,
                () -> this.userService.findById(exists.getUserId()));
    }


    /**
     * Compara que el resultado sea igual al testuser
     * @param result
     */
    private void compareTestUserWithResult(final RegisteredUser result){
        assertEquals(testUser.getUserId(), result.getUserId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        //assertEquals(testUser.getPassword(), result.getPassword());
    }


    

    

}
