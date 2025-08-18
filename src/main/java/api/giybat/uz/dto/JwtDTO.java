package api.giybat.uz.dto;

import api.giybat.uz.enums.ProfileRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class JwtDTO {
    @NonNull
    private String username;
    @NonNull
    private String code;
    private ProfileRole role;

}
