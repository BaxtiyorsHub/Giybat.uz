package api.giybat.uz.service;

import api.giybat.uz.dto.EmailHistoryDTO;
import api.giybat.uz.entity.EmailHistoryEntity;
import api.giybat.uz.repository.EmailHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailHistoryService {

    private EmailHistoryRepository emailHistoryRepository;

    @Transactional
    public void create(String body, String smsCode, String toAccount) {
        EmailHistoryEntity history = new EmailHistoryEntity();
        history.setBody(body);
        history.setCode(smsCode);
        history.setToAccount(toAccount);
        emailHistoryRepository.save(history);
    }

    public boolean isSmsValid(String email, String code) {
        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findLastByAccount(email);
        if (optional.isEmpty()) return false;

        EmailHistoryEntity entity = optional.get();
        if (!entity.getCode().equals(code)) return false;

        //  20:32:40           =   20:30.40  + 0:2:00
        LocalDateTime extDate = entity.getCreatedDate().plusMinutes(2);
        // now  20:31:30  >  20:32:40    |     now 20:35:30  >  20:32:40
        if (LocalDateTime.now().isAfter(extDate)) {
            return false;
        }
        return true;
    }

    public List<EmailHistoryDTO> getEmailHistoryByEmail(String email) {
        List<EmailHistoryEntity> entities = emailHistoryRepository.findByToAccount(email);

        return entities.stream().map(this::toDto).toList();
    }

    private EmailHistoryDTO toDto(EmailHistoryEntity entity) {
        if (entity == null) return null;

        EmailHistoryDTO dto = new EmailHistoryDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getToAccount());
        dto.setBody(entity.getBody());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}
