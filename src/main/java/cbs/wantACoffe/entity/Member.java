package cbs.wantACoffe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import cbs.wantACoffe.TableNames;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.ObtainVia;

/**
 * Clase para los miembros del grupo. Los miembros pueden estar registrados en la app
 * o no. 
 * TODO: crear unique constrains para que un miembro no pueda estar varias veces en un grupo
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = Member.TABLE_NAME, 
        indexes = {
            @Index(name = "idx_group_member_nickname", columnList = "nickname"),
            @Index(name = "idx_group_member_reg_user_id", columnList = "reg_user_id"),
            @Index(name = "idx_group_member_group_id", columnList = "group_id") },
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_reguser_groupid", columnNames = { "reg_user_id", "group_id" }),
                @UniqueConstraint(name = "unique_groupid_nickname", columnNames = { "group_id", "nickname" })
            })

public class Member {

    public final static String TABLE_NAME = "group_members";
    public final static String COLUMN_ID_NAME = "member_id";
    public final static String COLUMN_NICKNAME_NAME = "nickname";
    public final static String COLUMN_REG_USER_ID_NAME = "reg_user_id";
    public final static String COLUMN_IS_ADMIN_NAME = "is_admin";
    public final static String COLUMN_GROUP_ID_NAME = "group_id";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_id_generator")
    @SequenceGenerator(sequenceName = "MemberId", name = "member_id_generator", allocationSize = 1)
    @Column(name = COLUMN_ID_NAME) 
    private Long id;

    @Column(name = COLUMN_NICKNAME_NAME)
    private String nickname;

    @ManyToOne(targetEntity = RegisteredUser.class)
    @JsonBackReference
    @JoinColumn(name = COLUMN_REG_USER_ID_NAME)
    private RegisteredUser regUser;

    @Column(name = COLUMN_IS_ADMIN_NAME)
    private boolean isAdmin;

    @ManyToOne(targetEntity = Group.class, fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = COLUMN_GROUP_ID_NAME)
    private Group group;

    public boolean isRegisteredUser() {
        return this.regUser != null;
    }

}
