package api.giybat.uz.repository;

import api.giybat.uz.entity.profileEntities.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {
    @Query("from ProfileEntity p where p.visible=true and p.username=?1")
    Optional<ProfileEntity> findByUsernameAndVisibleIsTrue(@NotBlank(message = "Username required") String username);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set status=?1 where username=?2")
    void setStatusByUsername(GeneralStatus generalStatus, @NotBlank String phoneNumber);
}
