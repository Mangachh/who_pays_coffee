package cbs.wantACoffe.service.group;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.MemberNotInGroup;
import cbs.wantACoffe.repository.IMemberRepo;

/**
 * Implementación del sevicio {@link IMemerService}
 */
@Service
public class MemberServiceImpl implements IMemberService {
    
    @Autowired
    private IMemberRepo repo;

    @Override
    public Member saveGroupMember(Member user) throws MemberHasNoNicknameException {
        if (user.getNickname().isBlank()) {
            throw new MemberHasNoNicknameException();
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
    public Member findMemberByGroupIdAndRegUserId(Long groupId, Long regUserId) throws MemberNotInGroup {
        return this.repo.findMemberByGroupIdAndRegUserId(groupId, regUserId)
                .orElseThrow(MemberNotInGroup::new);
    }
    
    @Override
    public Member findMemberById(Long memberId) throws MemberNotInGroup {
        return this.repo.findById(memberId).orElseThrow(MemberNotInGroup::new);
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
    public void deleteGroupMemberById(Long id) {
        this.repo.deleteById(id);
    }       

    @Override
    public Member findMemberByGroupIdAndNickname(Long groupId, String nickname) throws MemberNotInGroup {
        return this.repo.findMemberByGroupIdAndNickname(groupId, nickname)
                    .orElseThrow(MemberNotInGroup::new);
    }

    


    

}
