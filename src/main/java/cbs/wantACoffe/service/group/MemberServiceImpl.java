package cbs.wantACoffe.service.group;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.repository.IMemberRepo;

/**
 * Implementación del sevicio {@link IMemerService}
 */
@Service
public class MemberServiceImpl implements IMemberService {
    
    @Autowired
    private IMemberRepo repo;

    @Override
    @Deprecated
    public Member saveGroupMember(Member user) {
        if (user.getNickname().isBlank()) {
            // exception
        }
        return this.repo.save(user);
    }

    @Override
    public Member saveGroupMember(RegisteredUser user, String nickname, boolean isAdmin) {
        Member m = Member.builder()
                .nickname(nickname)
                .regUser(user)
                .isAdmin(isAdmin)
                .build();
        return this.repo.save(m);
    }

    @Override
    public List<Group> findAllByRegUserIdAndIsAdminTrue(RegisteredUser user) {
        return this.repo.findGroupByRegUserAndIsAdminTrue(user);
    }

    @Override
    public List<Group> findAllByRegUserIdAndIsAdminFalse(RegisteredUser user) {
        return this.repo.findGroupByRegUserAndIsAdminFalse(user);
    }

    @Override
    public Member updateNickname(String newNickname) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateNickname'");
    }

    @Override
    public Member deleteGroupMemberById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteGroupMemberById'");
    }

    @Override
    public Member addGroupMember(Member regUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addGroupMember'");
    }

    @Override
    public Member deleteGroupMember(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteGroupMember'");
    }

    


    

}
