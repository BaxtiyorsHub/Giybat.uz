package api.giybat.uz.repository;

import api.giybat.uz.entity.SmsTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmsTokenRepository extends JpaRepository<SmsTokenEntity,String> {
    Optional<SmsTokenEntity> findTopByOrderByCreatedDateDesc();
}
