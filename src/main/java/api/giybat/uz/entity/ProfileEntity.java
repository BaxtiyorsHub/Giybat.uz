package api.giybat.uz.entity;

import api.giybat.uz.enums.GeneralStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "profile")
@Entity
@Setter
@Getter
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GeneralStatus status;

    @Column(name = "visible")
    private Boolean visible = true;

    @Column(name = "created_date")
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdDate;
}
