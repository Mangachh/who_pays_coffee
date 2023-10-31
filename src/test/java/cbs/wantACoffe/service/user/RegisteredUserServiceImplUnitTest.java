package cbs.wantACoffe.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;
import cbs.wantACoffe.repository.IRegisteredMemberRepo;

/**
 * Test unitario de {@link RegisteredUserService}
 */
@ActiveProfiles("h2_test")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class RegisteredUserServiceImplUnitTest {
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RegisterdUserServiceImpl service;

    @MockBean
    private IRegisteredMemberRepo repo;

    private static RegisteredUser testUser;

    @BeforeAll
    static void createTestUser() {
            testUser = CommonData.getTestUserWithSuffix("_RegisteredUserServiceImplUnitTest");
    }

    /**
     * Añade a un nuevo {@link RegisteredUSer}
     * @throws NullValueInUserDataException
     * @throws UsernameEmailAlreadyExistsException
     */
    @Test
    @Order(1)
    void testSaveNewUser() throws NullValueInUserDataException, UsernameEmailAlreadyExistsException {
            Mockito.when(repo.save(testUser))
                            .thenReturn(testUser);

            RegisteredUser result = this.service.saveNewUser(testUser);
            assertNotNull(result);
            this.checkUserWithTest(result);
    }

    /**
     * NO añade a un nuevo {@link RegisteredUSer} porque el campo
     * username está vacio
     * @throws UsernameEmailAlreadyExistsException
     */
    @Test
    @Order(2)
    void testSaveNewUserNullUserName() throws UsernameEmailAlreadyExistsException {

            RegisteredUser u = RegisteredUser.builder()
                            .email("pepote@mail.se")
                            .password("1234")
                            .build();

            assertThrows(NullValueInUserDataException.class,
                            () -> this.service.saveNewUser(u));

    }

    /**
     * NO añade a un nuevo {@link RegisteredUSer} porque el campo
     * email está vacio
     * @throws UsernameEmailAlreadyExistsException
     */
    @Test
    @Order(3)
    void testSaveNewUserNullEmail() throws UsernameEmailAlreadyExistsException {
            RegisteredUser u = RegisteredUser.builder()
                            .username("Pepo")
                            .password("1234")
                            .build();

            assertThrows(NullValueInUserDataException.class,
                            () -> this.service.saveNewUser(u));
    }

    /**
     * NO añade a un nuevo {@link RegisteredUSer} porque el campo
     * password está vacio
     * @throws UsernameEmailAlreadyExistsException
     */
    @Test
    @Order(4)
    void testSaveNewUserNullPassword() throws UsernameEmailAlreadyExistsException {
            RegisteredUser u = RegisteredUser.builder()
                            .username("Mariela")
                            .email("pepote@mail.se")
                            .build();

            assertThrows(NullValueInUserDataException.class,
                            () -> this.service.saveNewUser(u));
    }

    /**
     * NO añade a un nuevo {@link RegisteredUSer} porque el campo
     * email ya existe
     * @throws UNullValueInUserDataException
     */
    @Test
    @Order(5)
    void testSaveNewUserEmailExists() throws NullValueInUserDataException {

            Mockito.when(repo.save(testUser))
                            .thenThrow(DataIntegrityViolationException.class);

            assertThrows(UsernameEmailAlreadyExistsException.class,
                            () -> this.service.saveNewUser(testUser));
    }

    /**
     * NO añade a un nuevo {@link RegisteredUSer} porque el campo
     * username ya existe
     * @throws UNullValueInUserDataException
     */
    @Test
    @Order(6)
    void testSaveNewUsernameExists() throws NullValueInUserDataException {

            Mockito.when(repo.save(testUser))
                            .thenThrow(DataIntegrityViolationException.class);

            assertThrows(UsernameEmailAlreadyExistsException.class,
                            () -> this.service.saveNewUser(testUser));
    }

    /**
     * Encuentra a un {@link RegisteredUSer} por su id
     * @throws UserNotExistsException
     */
    @Test
    @Order(7)
    void testFindById() throws UserNotExistsException {
            Mockito.when(repo.findById(testUser.getUserId()))
                            .thenReturn(Optional.of(testUser));

            RegisteredUser result = this.service.findById(testUser.getUserId());
            assertNotNull(result);
            this.checkUserWithTest(result);
    }

    /**
     * NO encuentra a un {@link RegisteredUSer} porque su id no existe 
     * en la bdd
     */
    @Test
    @Order(8)
    void testFindByIdNoExists() {
            Long id = 54L;
            Mockito.when(repo.findById(id))
                            .thenReturn(Optional.empty());

            assertThrows(UserNotExistsException.class,
                            () -> this.service.findById(id));
    }

    /**
     * Encuentra a un {@link RegisteredUSer} por su email
     * @throws UserNotExistsException
     */
    @Test
    @Order(9)
    void testFindByEmail() throws UserNotExistsException {
            Mockito.when(this.repo.findByEmail(testUser.getEmail()))
                            .thenReturn(Optional.of(testUser));

            RegisteredUser u = this.service.findByEmail(testUser.getEmail());
            assertNotNull(u);
            this.checkUserWithTest(u);
    }

    /**
     * NO encuentra a un {@link RegisteredUSer} por su email
     * @throws UserNotExistsException
     */
    @Test
    @Order(10)
    void testFindByEmailNoExists() throws UserNotExistsException {
            Mockito.when(this.repo.findByEmail(testUser.getEmail()))
                            .thenReturn(Optional.empty());

            assertThrows(UserNotExistsException.class,
                            () -> this.service.findByEmail(testUser.getEmail()));
    }

    /**
     * Encuentra a un {@link RegisteredUSer} por su username
     * @throws UserNotExistsException
     */
    @Test
    @Order(11)
    void testFindByUsername() throws UserNotExistsException {
            Mockito.when(this.repo.findByUsername(testUser.getUsername()))
                            .thenReturn(Optional.of(testUser));

            RegisteredUser u = this.service.findByUsername(testUser.getUsername());
            assertNotNull(u);
            this.checkUserWithTest(u);
    }

    /**
     * NO Encuentra a un {@link RegisteredUSer} por su username porque no existe
     * @throws UserNotExistsException
     */
    @Test
    @Order(12)
    void testFindByUsernameNoExists() {
            Mockito.when(this.repo.findByUsername(testUser.getUsername()))
                            .thenReturn(Optional.empty());

            assertThrows(UserNotExistsException.class,
                            () -> this.service.findByUsername(testUser.getUsername()));

    }

    /**
     * Encuentra a un {@link RegisteredUSer} por su email y comprueba que el password sea correcto
     * @throws UserNotExistsException
     * @throws IncorrectPasswordException
     */
    @Test
    @Order(12)
    void testFindByEmailAndCheckPass() throws UserNotExistsException, IncorrectPasswordException {
            // user with encrypted password
            String password = CommonData.getTestUserWithSuffix("_RegisteredUserServiceImplUnitTest").getPassword();

            RegisteredUser cryptoUser = RegisteredUser.builder()
                            .username(testUser.getUsername())
                            .email(testUser.getEmail())
                            .userId(testUser.getUserId())
                            .password(encoder.encode(password))
                            .build();
            Mockito.when(this.repo.findByEmail(testUser.getEmail()))
                            .thenReturn(Optional.of(cryptoUser));

            RegisteredUser u = this.service.findByEmailAndCheckPass(testUser.getEmail(), password);
            assertNotNull(u);
            this.checkUserWithTest(u);
    }

    /**
     * NO Encuentra a un {@link RegisteredUSer} por su email porque no existe
     * @throws UserNotExistsException
     * @throws IncorrectPasswordException
     */
    @Test
    @Order(13)
    void testFindByEmailAndCheckPassUserNoExists() throws IncorrectPasswordException {
            Mockito.when(this.repo.findByEmail(testUser.getEmail()))
                            .thenReturn(Optional.empty());

            assertThrows(UserNotExistsException.class,
                            () -> this.service.findByEmailAndCheckPass(testUser.getEmail(),
                                            testUser.getPassword()));

    }

    /**
     * NO Encuentra a un {@link RegisteredUSer} por su email porque el password es incorrecto
     * @throws UserNotExistsException
     * @throws IncorrectPasswordException
     */
    @Test
    @Order(14)
    void testFindByEmailAndCheckPassPasswordIncorrect() throws UserNotExistsException {
        String username = testUser.getEmail();
        String password = "INCORRECT_PASSWORD";

        Mockito.when(this.repo.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotExistsException.class,
                () -> this.service.findByEmailAndCheckPass(username, password));

    }

    private void checkUserWithTest(final RegisteredUser result) {
        assertEquals(testUser.getUserId(), result.getUserId());
        // assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        // assertEquals(testUser.getPassword(), result.getPassword());
    }

}