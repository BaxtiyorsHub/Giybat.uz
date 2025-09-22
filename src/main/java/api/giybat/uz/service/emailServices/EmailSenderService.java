package api.giybat.uz.service.emailServices;

import api.giybat.uz.repository.EmailHistoryRepository;
import api.giybat.uz.utils.JwtUtil;
import api.giybat.uz.utils.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final EmailHistoryRepository emailHistoryRepository;
    @Value("${spring.mail.username}")
    private String fromAccount;
    @Value("${server.url}")
    private String serverURL;
    private final JavaMailSender javaMailSender;
    private final EmailHistoryService emailHistoryService;
    private final JwtUtil jwtUtil;

    public void sendRegistration(String toAccount) {
        int smsCode = RandomUtil.fiveDigit();
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1 style=\"text-align: center\">Gi'ybat.uz portaliga xush kelibsiz.</h1>\n" +
                "<br>\n" +
                "<h4>Ro'yhatdan o'tishni tugatish uchun quyidagi linkga bosing</h4>\n" +
                "<a style=\" background-color: indianred;\n" +
                "  color: black;\n" +
                "  padding: 10px 20px;\n" +
                "  text-align: center;\n" +
                "  text-decoration: none;\n" +
                "  display: inline-block;\"\n" +
                "   href=\"%s/api/v1/auth/registration/email/verification/%s\">Ro'yhatdan\n" +
                "    o'tishni tugatish</a>\n" +
                "\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        String jwtToken = jwtUtil.encodeForRegistration(toAccount, String.valueOf(smsCode));
        body = String.format(body, serverURL, jwtToken);
        // send
        sendMimeMessage(body, toAccount);
        // save to db
        emailHistoryService.create(body, String.valueOf(smsCode), toAccount);
    }

    private void sendMimeMessage(String body, String toAccount) {
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            msg.setFrom(fromAccount);

            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(toAccount);
            helper.setSubject("REGISTRATION");
            helper.setText(body, true);
            javaMailSender.send(msg);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
