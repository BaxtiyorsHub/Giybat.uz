package api.giybat.uz.service.smsServices;

import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.exp.AppBadException;
import api.giybat.uz.repository.SmsHistoryRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmsHistoryService {

    private final SmsHistoryRepository smsHistoryRepository;

    public boolean isValidSms(@NotBlank String smsCode, @NotBlank String phoneNumber) {
        SmsHistoryEntity smsByPhone = getSmsByPhone(phoneNumber);

        // check time
        if (LocalDateTime.now().isAfter(smsByPhone.getCreatedDate().plusMinutes(1))) return false;

        // check attempt count
        if (smsByPhone.getAttemptCount() >= 5) return false;

        if (!smsCode.equals(smsByPhone.getCode())) {
            //increase attempt count
            increaseAttempt(smsByPhone.getId());
            return false;
        }
        return true;
    }

    private void increaseAttempt(String id) {
        smsHistoryRepository.increaseAttempt(id);
    }

    private SmsHistoryEntity getSmsByPhone(String phoneNumber) {
        return smsHistoryRepository
                .findTopByPhoneNumberOrderByCreatedDateDesc(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Sms not found for phone: " + phoneNumber));
    }

}
