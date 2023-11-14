package cbs.wantACoffe.entity;

import cbs.wantACoffe.TableNames;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = TableNames.NAME_ADMIN_USER,
indexes = @Index(name="idx_admin_name", columnList = "username"))
/**
 * Clase del Usuario Administrador.
 */


public class AdminUser implements IUser{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long userId;
    private String username;
    private String password;
    private boolean isActive;
}
