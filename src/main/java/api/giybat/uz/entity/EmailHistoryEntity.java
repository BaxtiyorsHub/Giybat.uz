package api.giybat.uz.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_history")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private String id;

    @Column(name = "to_account")
    private String toAccount;

    @Column(name = "body", columnDefinition = "text")
    private String body;

    @Column(name = "code")
    private String code;

    @Column(name = "created_date")
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdDate;

}
