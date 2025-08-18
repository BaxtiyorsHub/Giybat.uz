package api.giybat.uz.service;

import api.giybat.uz.dto.*;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exp.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.service.emailServices.EmailHistoryService;
import api.giybat.uz.service.emailServices.EmailSenderService;
import api.giybat.uz.utils.JwtUtil;
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
    private final EmailHistoryService emailHistoryService;

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
        } else if (PhoneCheck.normalize(dto.getUsername()).startsWith("+998")) {
            // SMS Send
            return smsSenderService.sendRegistrationSMS(profile.getUsername());
        }
        throw new AppBadException("Something went wrong");
    }

    @SneakyThrows
    public String emailVerification(String token) {
        if (token.isBlank()) throw new AppBadException("Wrong token, something went wrong");

        JwtDTO jwtDTO = JwtUtil.decode(token);

        Optional<ProfileEntity> dbEntity = profileRepository.findByUsernameAndVisibleIsTrue(jwtDTO.getUsername());

        if (dbEntity.isPresent()) {
            ProfileEntity profileEntity = dbEntity.get();
            if (profileEntity.getStatus().equals(GeneralStatus.INACTIVE)
                    && emailHistoryService.isSmsValid(jwtDTO.getUsername(), jwtDTO.getCode())) {

                profileEntity.setStatus(GeneralStatus.ACTIVE);
                profileRepository.save(profileEntity);
                return "Registration completed";
            }
        }
        throw new AppBadException("Verification failed");
    }

    public String smsVerification(SmsVerificationDTO dto) {
        return null;
    }

    public ProfileDTO login(AuthorizationDTO dto) {
        return null;
    }
}
