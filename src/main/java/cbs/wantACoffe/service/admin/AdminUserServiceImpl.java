package cbs.wantACoffe.service.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cbs.wantACoffe.dto.group.IGroupInfo;
import cbs.wantACoffe.dto.user.IBasicUserInfo;
import cbs.wantACoffe.entity.AdminUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.repository.IAdminUserRepo;
import cbs.wantACoffe.repository.IGroupRepo;
import cbs.wantACoffe.repository.IRegisteredUserRepo;
import cbs.wantACoffe.service.auth.IEncryptService;
import lombok.RequiredArgsConstructor;

/**
 * Implementación de servicio para {@link IAdminService}
 * 
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements IAdminService{

    private final IAdminUserRepo adminRepo;

    private final IRegisteredUserRepo regUserRepo;

    private final IGroupRepo groupRepo;

    private final IEncryptService cryptService;
    
    @Override
    public AdminUser save(AdminUser admin) {
        admin.setPassword(
            this.cryptService.encryptPassword(admin.getPassword())
        );

        return this.adminRepo.save(admin);
        
    }

    @Override
    public AdminUser findByUsername(String username) throws UserNotExistsException {
        return adminRepo.findByUsername(username).orElseThrow(() -> new UserNotExistsException());
    }

     @Override
    public AdminUser findById(Long id) throws UserNotExistsException {
        return adminRepo.findById(id).orElseThrow(() -> new UserNotExistsException());
    }

    @Override
    public AdminUser findByUsernameAndCheckPass(String username, String password) throws UserNotExistsException, IncorrectPasswordException {
        AdminUser admin = this.adminRepo.findByUsername(username).orElseThrow(UserNotExistsException::new);
        // check pass
        if(cryptService.isSamePassword(password, admin.getPassword())){
            return admin;
        }

        throw new IncorrectPasswordException();
    }

    @Override
    public List<IBasicUserInfo> findAllRegisteredUsers() {
        

        return this.regUserRepo.findAllBasicData();
    }

    @Override
    public Long countGroups() {
        return this.groupRepo.count();
    }

    @Override
    public Long countRegisteredUsers() {
        return this.regUserRepo.count();
    }

    @Override
    public List<IGroupInfo> findAllGroupsAndCountMembers() {
        return this.groupRepo.findAllGroupsAndCountMembers();
    }


   
    
}
