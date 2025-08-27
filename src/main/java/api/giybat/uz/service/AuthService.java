package api.giybat.uz.service;

import api.giybat.uz.dto.*;
import api.giybat.uz.entity.profileEntities.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exp.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.service.emailServices.EmailHistoryService;
import api.giybat.uz.service.emailServices.EmailSenderService;
import api.giybat.uz.service.smsServices.SmsHistoryService;
import api.giybat.uz.utils.JwtUtil;
import api.giybat.uz.utils.PhoneCheck;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
    private final SmsHistoryService smsHistoryService;

    @SneakyThrows
    public String registration(@Valid RegistrationDTO dto) {
        // check
        if (dto.getUsername().isBlank()) throw new AppBadException("Something went wrong");

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

        if (dto.getUsername().contains("@")) emailSenderService.sendRegistration(dto.getUsername());
        else smsSenderService.sendRegistrationSMS(dto.getUsername());

        profile.setUsername(dto.getUsername());
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

    @SneakyThrows
    public String smsVerification(@Valid SmsVerificationDTO dto) {
        if (smsHistoryService.isValidSms(dto.getSmsCode(), dto.getPhoneNumber())) {
            profileRepository.setStatusByUsername(GeneralStatus.ACTIVE, dto.getPhoneNumber());
            return "Verification Successfully Completed!";
        }
        throw new AppBadException("Verification failed");
    }

    @SneakyThrows
    public ProfileDTO login(AuthorizationDTO dto) {
        Optional<ProfileEntity> dbEntity = profileRepository.findByUsernameAndVisibleIsTrue(dto.getUsername());

        if (dbEntity.isPresent()) {
            ProfileEntity profileEntity = dbEntity.get();
            if (bCryptPasswordEncoder.matches(dto.getPassword(), profileEntity.getPassword())) {
                return toDTO(profileEntity);
            }
        }
        throw new AppBadException("Login failed");
    }

    @SneakyThrows
    @Transactional
    public ProfileDTO resetPassword(@Valid String username) {
        return (ProfileDTO) profileRepository.findByUsernameAndVisibleIsTrue(username)
                .stream()
                .map(this::toDTO);
    }

    private ProfileDTO toDTO(ProfileEntity profileEntity) {
        ProfileDTO response = new ProfileDTO();
        response.setId(profileEntity.getId());
        response.setName(profileEntity.getName());
        response.setUsername(profileEntity.getUsername());
        response.setRoleList(profileRoleService.getByProfileId(profileEntity.getId()));
        response.setJwt(JwtUtil.encode(profileEntity.getUsername(), response.getRoleList()));
        return response;
    }

    private Optional<ProfileEntity> getEntityFromDB(String username) {
        return profileRepository.findByUsernameAndVisibleIsTrue(username);
    }

    @SneakyThrows
    public ProfileDTO resetConfirm(@Valid AuthorizationDTO dto) {
        Optional<ProfileEntity> entityFromDB = getEntityFromDB(dto.getUsername());

        if (entityFromDB.isPresent()) {
            entityFromDB.get().setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
            profileRepository.save(entityFromDB.get());
            return toDTO(entityFromDB.get());
        }
        throw new AppBadException("Confirmation failed");
    }
}
