package cbs.wantACoffe.entity;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
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
@Table(name=RegisteredUser.TABLE_NAME, 
       indexes = {@Index(name= "idx_username", columnList = "username"),
                  @Index(name= "idx_user_email", columnList = "email")})

public class RegisteredUser implements IUser{
    public final static String TABLE_NAME = "registered_users";
    public final static String COLUMN_ID_NAME = "user_id";
    public final static String COLUMN_USERNAME_NAME = "username";
    public final static String COLUMN_EMAIL_NAME = "email";
    public final static String COLUMN_PASSWORD_NAME = "password";

    @Id
    @GeneratedValue(generator = "user_id_generator")
    @SequenceGenerator(name = "user__id_generator", initialValue = 1)
    @Column(name = COLUMN_ID_NAME)
    /**
     * Id del usuario
     */    
    private Long userId;

    @Column(unique = true, 
            nullable = false,
            name = COLUMN_USERNAME_NAME)
    /**
     * Nombre del usuario. Tiene que ser único.
     */
    private String username;    

    @Column(unique = true,
            nullable = false,
            name = COLUMN_EMAIL_NAME)
        /**
         * Correo del usuario. Tiene que ser único.
         */
    private String email;

    @Column(nullable = false,
            name = COLUMN_PASSWORD_NAME)
    /**
     * Password del usuario
     */
    private String password;

    // pasamos esto a bidirectional a ver
    @OneToMany(mappedBy = "regUser")
    @Builder.Default
    private List<Member> memberGroups = new ArrayList<>();

    /*
     * De momento nulleamos esto...
     */
    @PreRemove
    private void PreRemove() {
            for (Member m : memberGroups) {
                m.setRegUser(null);
        }
    }
    
}
