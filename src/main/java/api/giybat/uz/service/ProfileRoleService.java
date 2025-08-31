package api.giybat.uz.service;

import api.giybat.uz.entity.profileEntities.ProfileEntity;
import api.giybat.uz.entity.profileEntities.ProfileRoleEntity;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exp.AppBadException;
import api.giybat.uz.repository.ProfileRoleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileRoleService {

    private final ProfileRoleRepository profileRoleRepository;

    public void deleteRolesByProfileId(String id) {
        if (!id.isBlank()) profileRoleRepository.deleteByProfileId(id);
    }

    @SneakyThrows
    @Transactional
    public void create(@NotNull ProfileEntity profile, @NotNull ProfileRole profileRole) {
        if (profile == null || profileRole == null) throw new AppBadException("Null point exp");

        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setProfileId(profile.getId());
        profileRoleEntity.setProfile(profile);
        profileRoleEntity.setRole(profileRole);

        profileRoleRepository.save(profileRoleEntity);
    }

    public ProfileRole getByProfileId(String id) {
        return null;
    }
}
