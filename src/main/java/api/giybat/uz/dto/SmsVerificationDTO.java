package api.giybat.uz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SmsVerificationDTO {
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String smsCode;
}
