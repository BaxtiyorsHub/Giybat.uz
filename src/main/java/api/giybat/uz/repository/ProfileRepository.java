package api.giybat.uz.repository;

import api.giybat.uz.entity.ProfileEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {
    Optional<ProfileEntity> findByUsernameAndVisibleIsTrue(@NotBlank(message = "Username required") String username);
}
