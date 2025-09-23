package api.giybat.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_token")
@Setter
@Getter
public class SmsTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token", columnDefinition = "text")
    private String token;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
