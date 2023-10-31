package cbs.wantACoffe.entity;

/**
 * Interfaz básica para los diferentes tipos de usuarios que puede
 * tener la aplicación
 */
public interface IUser {
    
    /**
     * Devuelve el id del usuario
     * @return
     */
    Long getUserId();

    /**
     * Devuelve el nombre de usuario
     * @return
     */
    String getUsername();

    /**
     * Devuelve el password
     * @return
     */
    String getPassword();

    void setUserId(Long id);

    void setPassword(String password);

    void setUsername(String name);
}
