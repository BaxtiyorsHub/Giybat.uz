package api.giybat.uz.service;

import api.giybat.uz.config.CustomUserDetail;
import api.giybat.uz.entity.profileEntities.ProfileEntity;
import api.giybat.uz.entity.profileEntities.ProfileRoleEntity;
import api.giybat.uz.exp.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final ProfileRepository profileRepository;
    private final ProfileRoleRepository profileRoleRepository;

    @SneakyThrows
    public UserDetails loadUserByUsername(String username) {
        Optional<ProfileEntity> dbEntity = profileRepository.findByUsernameAndVisibleIsTrue(username);
        if (dbEntity.isPresent()){

            ProfileEntity profileEntity = dbEntity.get();

            ProfileRoleEntity role = profileRoleRepository.findByProfileId(profileEntity.getId());
            return new CustomUserDetail(profileEntity.getId(),
                    profileEntity.getUsername(),
                    profileEntity.getPassword(),
                    profileEntity.getStatus(),
                    role.getRole());
        }
        throw new AppBadException("Load user not found");
    }
}
