package cbs.wantACoffe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(name = "group_members", indexes = { @Index(name = "idx_group_member_nickname", columnList = "nickname"),
        @Index(name = "idx_group_member_reg_user_id", columnList = "reg_user_user_id"),
        @Index(name = "idx_group_member_group_id", columnList = "group_id") })

public class Member {

    @Id
    @GeneratedValue(generator = "member_id_generator")
    @SequenceGenerator(name = "member_id_generator", initialValue = 1)
    private Long id;

    private String nickname;

    @ManyToOne(targetEntity = RegisteredUser.class)
    @JsonBackReference
    private RegisteredUser regUser;

    private boolean isAdmin;

    @ManyToOne(targetEntity = Group.class, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "group_id")
    private Group group;

    public boolean isRegisteredUser() {
        return this.regUser != null;
    }

}
