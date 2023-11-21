package cbs.wantACoffe.service.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;
import cbs.wantACoffe.repository.IRegisteredUserRepo;
import cbs.wantACoffe.service.auth.IEncryptService;
import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio {@link IRegisteredUserService}
 */
@Service
@RequiredArgsConstructor
public class RegisterdUserServiceImpl implements IRegisteredUserService {

    private final IRegisteredUserRepo userRepo;

    private final IEncryptService cryptService;

    
    @Override
    public RegisteredUser saveNewUser(RegisteredUser user)
            throws NullValueInUserDataException, UsernameEmailAlreadyExistsException {

        // check emtpy values
        if (this.isStringNullEmpty(user.getUsername()) ||
                this.isStringNullEmpty(user.getEmail()) ||
                this.isStringNullEmpty(user.getPassword())) {
            throw new NullValueInUserDataException();
        }

        try {
            // encode password
            user.setPassword(this.cryptService.encryptPassword(user.getPassword()));
            // encode email
            return this.userRepo.save(user);
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getClass());
            throw new UsernameEmailAlreadyExistsException();
        }
    }

    private boolean isStringNullEmpty(final String value) {
        return value == null || value.isEmpty();
    }

    @Override
    public void deleteUser(RegisteredUser user) {
        this.userRepo.delete(user);
    }

    // Find
    @Override
    public RegisteredUser findByUsername(String username) throws UserNotExistsException {
        return this.userRepo.findByUsername(username)
            .orElseThrow(() -> new UserNotExistsException());
    }

    @Override
    public RegisteredUser findByEmailAndCheckPass(final String email, final String pass)
            throws UserNotExistsException, IncorrectPasswordException {
        RegisteredUser u = this.findByEmail(email);
        if (this.cryptService.isSamePassword(pass, u.getPassword())) {
            return u;
        }

        throw new IncorrectPasswordException();
    }

    @Override
    public RegisteredUser findByEmail(String email) throws UserNotExistsException {
        return this.userRepo.findByEmail(email).orElseThrow(UserNotExistsException::new);
    }

    @Override
    public RegisteredUser findById(Long id) throws UserNotExistsException {
        return this.userRepo.findById(id).orElseThrow(UserNotExistsException::new);
    }

    @Override
    public void deleteUserById(Long id) {
        this.userRepo.deleteById(id);
    }

}
