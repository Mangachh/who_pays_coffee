package cbs.wantACoffe.entity;

import cbs.wantACoffe.TableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = AdminUser.TABLE_NAME,
        indexes = @Index(name="idx_admin_name", columnList = AdminUser.COLUMN_USERNAME_NAME),
        uniqueConstraints = @UniqueConstraint(name = "unique_admin_username", columnNames = AdminUser.COLUMN_USERNAME_NAME))
/**
 * Clase del Usuario Administrador.
 */


public class AdminUser implements IUser{
    public final static String TABLE_NAME = "admin_users";
    public final static String COLUMN_ID_NAME = "user_id";
    public final static String COLUMN_USERNAME_NAME = "username";
    public final static String COLUMN_PASSWORD_NAME = "password";
    public final static String COLUMN_IS_ACTIVE_NAME = "is_active";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ID_NAME)
    private Long userId;

    @Column(name = COLUMN_USERNAME_NAME)
    private String username;

    @Column(name = COLUMN_PASSWORD_NAME)
    private String password;

    @Column(name = COLUMN_IS_ACTIVE_NAME)
    private boolean isActive;
}
