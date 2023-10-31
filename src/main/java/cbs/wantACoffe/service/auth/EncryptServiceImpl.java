package cbs.wantACoffe.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Implementación de servicio {@link IEncryptService}.
 * De momento sólo encripta el password
 */
@Service
@RequiredArgsConstructor
public class EncryptServiceImpl implements IEncryptService {

    private final PasswordEncoder encoder;

    @Override
    public String encryptPassword(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean isSamePassword(String originalPass, String cryptoPass) {
        return encoder.matches(originalPass, cryptoPass);
    }    
    
}
