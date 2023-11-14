package cbs.wantACoffe.service.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cbs.wantACoffe.entity.AdminUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.repository.IAdminUserRepo;
import cbs.wantACoffe.repository.IRegisteredMemberRepo;
import cbs.wantACoffe.repository.IRegisteredMemberRepo.IBasicData;
import cbs.wantACoffe.service.auth.IEncryptService;
import lombok.RequiredArgsConstructor;

/**
 * Implementación de servicio para {@link IAdminService}
 * 
 * TODO: ampliar conforme añadamos funcionalidades a {@link GroupController}
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements IAdminService{

    private final IAdminUserRepo adminRepo;

    private final IRegisteredMemberRepo userRepo;

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
    public List<IBasicData> findAllRegisteredUsers() {
        

        return this.userRepo.findAllBasicData();
    }

   
    
}
