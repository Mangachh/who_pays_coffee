package cbs.wantACoffe.service.auth;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import cbs.wantACoffe.controller.GroupController;
import cbs.wantACoffe.dto.token.Token;
import cbs.wantACoffe.dto.token.Token.TokenType;
import cbs.wantACoffe.entity.IUser;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.InvalidTokenFormat;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.util.AuthUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
/**
 * Servicio para todas las operaciones de Autentificación.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class AuthServiceImpl implements IAuthService {

    /*
     * Dictionary with all the users in session.
     * Each key is a token type (USER, ADMIN, ETC)
     * For each key, there's another dictionary that stores the
     * user's id with the token body as it's key.
     */
    private Map<TokenType, Map<String, Long>> usersInSession = new HashMap<>();

    @PostConstruct
    private void initDicts() {
        final TokenType[] tokTypes = TokenType.class.getEnumConstants();
        for (TokenType t : tokTypes) {
            this.usersInSession.put(t, new HashMap<>());
        }
    }

    @Override
    public Token generateToken(final IUser user, TokenType type) {
        // TODO: generate some garbage or something similar, like username+ip connected
        final String toByte = user.getUsername() + type.getHead();
        final UUID uuid = UUID.nameUUIDFromBytes(toByte.getBytes());
        final String tk = Base64.getEncoder().withoutPadding().encodeToString(uuid.toString().getBytes());
        return Token.builder().body(tk).type(type).build();
    }

    @Override
    public void addUserTokenToSession(final Token token, final IUser user) {
        this.usersInSession
                .get(token.getType())
                .put(token.getBody(), user.getUserId());
    }

    @Override
    public void removeTokenFromSession(final Token token) {
        this.usersInSession
                .get(token.getType())
                .remove(token.getBody());
    }

    @Override
    public boolean isUserInSession(final IUser user) {
        return this.isUserInSession(user.getUserId());
    }
    
    @Override
    public boolean isUserInSession(final Long userId) {
        return this.usersInSession.values().stream()
                .anyMatch(
                        key -> key.values().stream()
                        .anyMatch(Predicate.isEqual(userId)));
    }

    @Override
    public boolean isTokenInSession(final Token token) {
        return this.usersInSession
                .get(token.getType())
                .containsKey(token.getBody());
    }

    @Override
    public Long getUserIdByToken(final Token token) {
        return this.usersInSession
                .get(token.getType())
                .get(token.getBody());
    }

    @Override
    public RegisteredUser getUserByToken(GroupController groupController, String token)
            throws InvalidTokenFormat, UserNotExistsException {
        Long id = getUserIdByToken(AuthUtils.stringToToken(token));
        return groupController.userService.findById(id);
    }
}
