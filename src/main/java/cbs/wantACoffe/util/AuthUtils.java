package cbs.wantACoffe.util;

import cbs.wantACoffe.dto.token.Token;
import cbs.wantACoffe.dto.token.Token.TokenType;
import cbs.wantACoffe.exceptions.InvalidTokenFormat;

/**
 * Clase con útiles para la autentificación.
 * Tenemos el nombre del header donde queremos guardar el token
 * y como empieza el token.
 * 
 * CBS {token}
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class AuthUtils {
    
    /*
     * Nombre del header donde estará el token
     */
    public static final String HEADER_AUTH_TXT = "Authorization";

    /*
     * Inicio del token
     */
    @Deprecated
    public static final String AUTH_START = "CBS ";

    /**
     * A partir de un header, extraemos el token
     * @param header -> header donde está el token (CBS {token})
     * @return -> token
     * @deprecated
     */
    public static String extractTokenFromPrefix(final String fullToken) {
        return fullToken.substring(AUTH_START.length());
    }
    
    /**
     * Converts a text to a {@link Token} object.
     * If the text is not a token, throws an exception
     *
     * @param text
     * @return
     * @throws InvalidTokenFormat
     * @throws Exception -> the string is not a token. TODO: custom exception
     */
    public static Token stringToToken(final String text) throws InvalidTokenFormat{
        if (isPrefixAuthValid(text)) {
            TokenType type = TokenType.getTypeFromStr(text);
            String body = text.substring(type.getHead().length());
            return Token.builder()
                    .body(body.trim())
                    .type(type)
                    .build();
        }
        
        // exception
        throw new InvalidTokenFormat();
    }

    /**
     * Comprueba que el header sea válido.
     * <p>
     * Lo hace comprobando que empieza con AUTH_START
     * @param header
     * @return
     * @see #AUTH_START
     */
    public static boolean isPrefixAuthValid(final String fulltoken){
        TokenType[] types = TokenType.class.getEnumConstants();
        for (TokenType t : types) {
            if (fulltoken.startsWith(t.getHead())) {
                return true;
            }
        }
        return false;
    }
}
