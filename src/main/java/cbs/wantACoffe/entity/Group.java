package cbs.wantACoffe.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase grupo. Las operaciones de la app se centran en esta clase
 * ya que cada {@link RegisteredUser} puede crear un grupo.
 * El grupo tiene una lista de usuarios que pertenecen a él.
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = Group.TABLE_NAME, indexes = { @Index(name = "idx_group_group_name", columnList = Group.COLUMN_GROUP_NAME),
        @Index(name = "idx_group_group_owner", columnList = Group.COLUMN_OWNER_ID)
})
public class Group {
    public final static String TABLE_NAME = "groups";
    public final static String COLUMN_ID_NAME = "group_id";
    public final static String COLUMN_GROUP_NAME = "group_name";
    public final static String COLUMN_OWNER_ID = "owner_id";

    @Id
    @GeneratedValue(generator = "group_id_generator")
    @SequenceGenerator(name = "group__id_generator", initialValue = 1)
    @Column(name = COLUMN_ID_NAME)
    private Long groupId;

    @Column(name = COLUMN_GROUP_NAME, nullable = false)
    private String groupName;

    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = COLUMN_OWNER_ID, referencedColumnName = Member.COLUMN_ID_NAME)
    private Member owner;

    // create
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REMOVE })
    @JsonBackReference // esto lo puedo quitar en teoría
    @Builder.Default
    private List<Member> members = new ArrayList<>();

    @OneToMany(
        mappedBy = "group", 
        fetch = FetchType.EAGER, 
        cascade = { CascadeType.MERGE, CascadeType.REMOVE }
    )
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @Override
    public String toString() {
        return "Group-> name: " + groupName + " members: [" + members.stream().map(m -> "user id: " +
                m.getRegUser() +
                "nickname: " + m.getNickname() +
                "isAdmin: " + String.valueOf(m.isAdmin() + ", ")).toList().toString() + "]";
    }

    // mmm, esto se puede optimizar creo
    public boolean tryAddMember(final Member member) {
        for (Member m : this.members) {
            if (m.getRegUser() != null &&
                    m.getRegUser().equals(member.getRegUser())) {
                return false;
            }
        }

        this.members.add(member);
        member.setGroup(this);
        return true;
    }
}
