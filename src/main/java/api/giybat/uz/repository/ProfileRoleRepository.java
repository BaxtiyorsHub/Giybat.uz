package api.giybat.uz.repository;

import api.giybat.uz.entity.profileEntities.ProfileRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRoleRepository extends JpaRepository<ProfileRoleEntity,String> {
    ProfileRoleEntity findByProfileId(String profileId);
}
