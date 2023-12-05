package cbs.wantACoffe.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Tokens de autentificación de la app. Los token pueden tener
 * diferentes tipos. Se componen de un head y un body
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public class Token {
    
    /**
     * Tipo del token.
     * Tiene 2 métodos: uno que devuelve una string que servirá de head 
     * y otro que a partir de un token completo, encuentra un head
     */
    public static enum TokenType {
        // ok, tienen la misma longitud por facilidad
        ADMIN("MTLL"), USER("CBS"), NONE("NONE");

        private String head;

        private TokenType(String type) {
            this.head = type;
        }

        public String getHead() {
            return this.head;
        }

        public static TokenType getTypeFromStr(final String str) {
            TokenType[] types = TokenType.class.getEnumConstants();
            for (TokenType t : types) {
                if (str.startsWith(t.getHead())) {
                    return t;
                }
            }
            return NONE;
        }

    }
    
    
    @Builder.Default
    private TokenType type = TokenType.USER;
    private String body;

    
}
