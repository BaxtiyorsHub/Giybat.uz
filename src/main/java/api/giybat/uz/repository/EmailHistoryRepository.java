package api.giybat.uz.repository;

import api.giybat.uz.entity.EmailHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistoryEntity, String> {
    @Query("from EmailHistoryEntity e where e.toAccount=?1 order by e.createdDate desc limit 1")
    Optional<EmailHistoryEntity> findLastByAccount(String email);

    List<EmailHistoryEntity> findByToAccount(String toAccount);
}
