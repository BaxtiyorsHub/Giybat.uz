package api.giybat.uz.service.smsServices;

import api.giybat.uz.config.RestTemplate;
import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.utils.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsSenderService {

    private final RestTemplate restTemplate;
    private final SmsHistoryService smsHistoryService;

    public void sendRegistrationSMS(String phoneNumber) {
        int code = RandomUtil.fiveDigit();
        String body = "<#>Kun.uz partali. Ro'yxatdan o'tish uchun tasdiqlash kodi (code) : " + code;

        try {
            sendMessage(phoneNumber, body, code);
            smsHistoryService.save(phoneNumber, code);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(String phoneNumber, String body, int code) {
        SmsHistoryEntity entity = new SmsHistoryEntity();
        entity.setPhoneNumber(phoneNumber);
        entity.setCode(String.valueOf(code));

        String url = "https://notify.eskiz.uz/api/message/sms/send";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + smsTokenService.getToken());
    }
}
