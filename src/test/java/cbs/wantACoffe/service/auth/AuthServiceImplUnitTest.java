package cbs.wantACoffe.service.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;
import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.dto.token.Token;
import cbs.wantACoffe.dto.token.Token.TokenType;
import cbs.wantACoffe.entity.IUser;
import cbs.wantACoffe.entity.RegisteredUser;

/**
 * Test unitario de {@link AuthServiceImpl}
 */
@ActiveProfiles("h2_test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class AuthServiceImplUnitTest {

    @Autowired
    AuthServiceImpl authService;

    private static Token token;
    private static RegisteredUser testUser = CommonData.getTestUserWithSuffix("_AuthServiceImplUnitTest");

   
    @BeforeAll
    static void generateToken(){
        final String toByte = testUser.getUsername() + TokenType.USER.getHead();
        final UUID uuid = UUID.nameUUIDFromBytes(toByte.getBytes());
        final String end = Base64.getEncoder().withoutPadding().encodeToString(uuid.toString().getBytes());


        token = new Token(TokenType.USER, end);
    }
    /**
     * Genera un token a un nuevo usuario
     */
    @Test
    @Order(1)
    void testGenerateToken() {
        // creamos token a mano
        
        Token result = authService.generateToken(testUser, TokenType.USER);
        assertEquals(token, result);
        assertEquals(CommonData.getPrefix(), result.getType().getHead());

        // añadimos el token a la sesión, para los siguientes tests
        this.authService.addUserTokenToSession(result, testUser);
    }

    /**
     * Comprueba que el token SÍ está en sesión
     */
    @Test
    @Order(2)
    void testIsTokenInSession() {
        assertTrue(this.authService.isTokenInSession(token));
    }

    /**
     * Comprueba que el token NO está en sesión
     */
    @Test
    @Order(3)
    void testIsTokenInSessionIncorrect() {
        assertFalse(this.authService.isTokenInSession(new Token(TokenType.USER, "TOKEN!!!")));
    }

    /**
     * Encuentra la id de {@link IUSer} por el token
     */
    @Test
    @Order(4)
    void testGetUserIdByToken() {
        final Long expected = testUser.getUserId();
        Long result = this.authService.getUserIdByToken(token);
        assertEquals(expected, result);
    }

    /**
     * Comprueba que un {@link IUser} está en sesión
     */
    @Test
    @Order(5)
    void testIsUserInSession() {
        assertTrue(this.authService.isUserInSession(testUser));
    }

    /**
     * Elimina un token de la sesión
     */
    @Test
    @Order(6)
    void testRemoveTokenFromSession() {
        // check that the token is in session
        assertTrue(this.authService.isTokenInSession(token));

        //remove
        this.authService.removeTokenFromSession(token);

        // and check again
        assertFalse(this.authService.isTokenInSession(token));
    }
}
