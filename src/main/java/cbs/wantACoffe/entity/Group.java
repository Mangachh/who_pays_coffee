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
import jakarta.persistence.OneToMany;
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
@Table(name = "groups", 
        indexes = { @Index(name = "idx_group_group_name", columnList = "group_name"),
                //@Index(name = "idx_group_group_owner", columnList = "group_owner_id")
        })
public class Group {
    
    @Id
    @GeneratedValue(generator = "group_id_generator")
    @SequenceGenerator(name = "group__id_generator", initialValue = 1)
    private Long groupId;

    @Column(name = "group_name")
    private String groupName;

    // create
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REMOVE })
    @JsonBackReference
    @Builder.Default
    private List<Member> members = new ArrayList<>();

    @Override
    public String toString() {
        return "Group-> name: " + groupName + " members: [" + members.stream().map(m -> "user id: " +
                m.getRegUser() +
                "nickname: " + m.getNickname() +
                "isAdmin: " + String.valueOf(m.isAdmin() + ", ")).toList().toString() + "]";
    }
    
    public boolean tryAddMember(final Member member) {
        if (this.members.contains(member)) {
            // exception???
            return false;
        } else {
            this.members.add(member);
            member.setGroup(this);
            return true;
        }

    }
}
