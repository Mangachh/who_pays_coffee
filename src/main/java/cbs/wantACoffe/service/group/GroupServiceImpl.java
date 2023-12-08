package cbs.wantACoffe.service.group;

import java.util.List;

import org.springframework.stereotype.Service;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.GroupHasNoNameException;
import cbs.wantACoffe.exceptions.GroupNotExistsException;
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
    public Group saveGroup(Group group) throws GroupHasNoNameException {
        if (group.getGroupName() == null || group.getGroupName().isEmpty()) {
            throw new GroupHasNoNameException(); 
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
    public Group getGroupById(Long id) throws GroupNotExistsException {
        return this.repo.findById(id).orElseThrow(() -> new GroupNotExistsException());
    }
    
    @Override
    public List<Group> getAllByRegUser(RegisteredUser user) {
        return this.repo.findAllByMembersRegUser(user);
    }

    @Override
    public List<Group> getAllByRegUserIsAdmin(RegisteredUser user, boolean isAdmin) {
        return this.repo.findAllByMembersAndMembersIsAdmin(user.getUserId(), isAdmin);
    }
    
    @Override
    public void deleteGroup(long id) {
        this.repo.deleteById(id);
    }

    @Override
    public List<Group> getAllByOwner(Long regUserId) {
        return this.repo.findByRegUserOwner(regUserId);
    }

    

        
    
}
