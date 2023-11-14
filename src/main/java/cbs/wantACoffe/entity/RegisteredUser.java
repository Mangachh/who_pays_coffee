package cbs.wantACoffe.entity;

import cbs.wantACoffe.TableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de usuario
*/
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name=TableNames.NAME_REGISTERED_USERS, 
       indexes = {@Index(name= "idx_username", columnList = "username"),
                  @Index(name= "idx_user_email", columnList = "email")})

public class RegisteredUser implements IUser{


    @Id
    @GeneratedValue(generator = "user_id_generator")
    @SequenceGenerator(name = "user__id_generator", initialValue = 1)
    /**
     * Id del usuario
     */
    private Long userId;

    @Column(unique = true, 
            nullable = false)
    /**
     * Nombre del usuario. Tiene que ser único.
     */
    private String username;    

    @Column(unique = true,
            nullable = false)
        /**
         * Correo del usuario. Tiene que ser único.
         */
    private String email;

    @Column(nullable = false)
    /**
     * Password del usuario
     */
    private String password;
    
}
