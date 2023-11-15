package cbs.wantACoffe.service.group;

import java.util.List;

import org.springframework.stereotype.Service;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.repository.IGroupRepo;
import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio {@link IGroupService}
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements IGroupService {

    private final IGroupRepo repo;
    

    @Override
    public Group saveGroup(Group group) {
        if (group.getGroupName() == null || group.getGroupName().isEmpty()) {
            // error, no group name
        }

        //group.setGroupOwner(owner);
        return this.repo.save(group);
    }
    
    @Override
    public void tryAddMemberToGroup(final Member member, final Group group) {
        if (group.tryAddMember(member)) {
            this.repo.save(group);
        }
    }
    
    @Override
    public Group findGroupById(Long id) throws Exception {
        return this.repo.findById(id).orElseThrow(() -> new Exception());
    }
    
    @Override
    public List<Group> findAllByRegUser(RegisteredUser user) {
        return this.repo.findAllByMembersRegUser(user);
    }

    @Override
    public List<Group> findAllByRegUserIsAdmin(RegisteredUser user, boolean isAdmin) {
        return this.repo.findAllByMembersAndMembersIsAdminTrue(user.getUserId(), isAdmin);
    }
    
    @Override
    public void deleteGroup(long id) {
        this.repo.deleteById(id);
    }

    


    @Override
    public List<Member> findAllGroupMembers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllGroupMembers'");
    }
   
    @Override
    public Member addGroupUser(Member user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addGroupUser'");
    }

    @Override
    public List<Group> findAllByMemberId(Long id) {
        
        // return this.repo.findAll
        return null;
    }

    @Override
    public boolean isUserInGroup() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isUserInGroup'");
    }

    
    
}
