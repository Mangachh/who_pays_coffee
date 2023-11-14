package cbs.wantACoffe.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cbs.wantACoffe.dto.Token;
import cbs.wantACoffe.dto.Token.TokenType;
import cbs.wantACoffe.service.auth.IAuthService;
import cbs.wantACoffe.util.AuthUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro que se ejecuta antes de cada llamada a un endpoint.
 * En este filtro tenemos un array con los endpoints que podemos
 * visitar aunque no estemos registrados en la base de datos y/o
 * no tengamos un token de sesión.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    /**
     * Endpoints that don't need permissions
     */
    private final String[] WHITE_LIST = { "/coffee/api/auth/p/", "/console", "/coffee/api/admin/p" };
    private final String ENTER_MESSAGE_WHITE = "Accessing to a Whitelist endpoint";

    private final String[] ADMIN_LIST = { "/coffee/api/admin/r" };
    private final String ENTER_MESSAGE_ADMIN = "Accessing to an Admin endpoint";
    private final String ENTER_MESSAGE_USER = "Accessing to an Userendpoint";

    /**
     * The current AuthService
     * 
     * @see IAuthService
     */
    private final IAuthService authService;

    private final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    /**
     * Primero comprueba si la request pertenece a la {@link WHITE_LIST}.
     * Si la request no pertenece a ella, siginifica que se necesitan
     * permisos para acceder al endpoint.
     * Por lo tanto comprueba
     * <ol>
     * <li>Que el header de autorización exista.</li>
     * <li>Que el header cotiene el token con la estructura deseada</li>
     * <li>Que el token está en uso en {@link IAuthService}</li> *
     * </ol>
     * Si todas las condiciones se cumplen, la request sigue su curso y llamará
     * al endpoint que sea necesario. Si cualquiera de ellas no se cumple, entonces
     * devuelve un error 403, forbiden, al cliente
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Filtering call.");

        if (this.isUriInArray(request.getRequestURI(), this.WHITE_LIST)) {
            log.info(this.ENTER_MESSAGE_WHITE);
            filterChain.doFilter(request, response);
            return;
        }

        final String header = request.getHeader(AuthUtils.HEADER_AUTH_TXT);
        final Token token;

        if (header != null) {
            try {
                token = AuthUtils.stringToToken(header);
            } catch (Exception e) {
                log.error("Header is not a valid token: " + header);
                response.sendError(403, "You don't have permissions");
                return;
            }

            boolean isAdminList = this.isUriInArray(request.getRequestURI(), this.ADMIN_LIST);

            // si es admin y está en su lista
            if (token.getType() == TokenType.ADMIN && isAdminList &&
                    this.authService.isTokenInSession(token)) {
                log.info(this.ENTER_MESSAGE_ADMIN);
                filterChain.doFilter(request, response);
                return;
                // si es user y no está en la lista de user
            } else if (token.getType() == TokenType.USER && isAdminList == false &&
                    this.authService.isTokenInSession(token)) {
                log.info(this.ENTER_MESSAGE_USER);
                filterChain.doFilter(request, response);
                return;
            }

        }

        log.error("The user don't have permission to access {}", request.getRequestURI());
        response.sendError(403, "You don't have permissions");
    }
    
    /**
     * Comprueba que la Uri a la que quiere acceder el usuario está en la {@link #WHITE_LIST}
     * 
     * @param reqUri -> uri de acceso
     * @param list -> white_list
     * @return
     */
    private boolean isUriInArray(final String reqUri, final String[] list) {
        for (String s : list) {
            if (reqUri.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

}
