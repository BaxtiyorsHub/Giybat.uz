package api.giybat.uz.service;

import api.giybat.uz.dto.AuthorizationDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.dto.SmsVerificationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exp.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.utils.PhoneCheck;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final ProfileRoleService profileRoleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailSenderService emailSenderService;
    private final SmsSenderService smsSenderService;

    @SneakyThrows
    public String registration(RegistrationDTO dto) {
        // check
        if (!dto.getUsername().isBlank()) throw new AppBadException("Something went wrong");

        Optional<ProfileEntity> existOptional = profileRepository.findByUsernameAndVisibleIsTrue(dto.getUsername());
        if (existOptional.isPresent()) {
            ProfileEntity existsProfile = existOptional.get();
            if (existsProfile.getStatus().equals(GeneralStatus.INACTIVE)) {
                profileRoleService.deleteRolesByProfileId(existsProfile.getId());
                profileRepository.deleteById(existsProfile.getId());
            } else {
                throw new AppBadException("Username already exists");
            }
        }
        // create profile
        ProfileEntity profile = new ProfileEntity();
        profile.setName(dto.getName());
        if (profile.getUsername().contains("@")) { // email va phone ga tekshirishni o'zgartirsa bo'ladi.
            // Email Send
            emailSenderService.sendRegistration(profile.getUsername());
            profile.setUsername(dto.getUsername());
        } else {
            // SMS Send
            smsSenderService.sendRegistrationSMS(profile.getUsername());
            profile.setUsername(dto.getUsername());
        }
        profile.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        profile.setStatus(GeneralStatus.INACTIVE);
        profileRepository.save(profile);
        // create profile roles
        profileRoleService.create(profile, ProfileRole.USER);
        // send verification code
        if (profile.getUsername().contains("@")) { // email va phone ga tekshirishni o'zgartirsa bo'ladi.
            // Email Send
            return emailSenderService.sendRegistration(profile.getUsername());
        } else if (PhoneCheck.normalize(dto.getUsername()).startsWith("+998")){
            // SMS Send
            return smsSenderService.sendRegistrationSMS(profile.getUsername());
        }
        throw new AppBadException("Something went wrong");
    }

    public String emailVerification(String token) {
        return null;
    }

    public String smsVerification(SmsVerificationDTO dto) {
        return null;
    }

    public ProfileDTO login(AuthorizationDTO dto) {
        return null;
    }
}
