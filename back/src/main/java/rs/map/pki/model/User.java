package rs.map.pki.model;

import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "{user.email.blank}")
    @Email(message = "{user.email.invalid}")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "{user.password.blank}")
    @Size(min = 8, max = 64, message = "{user.password.size}")
    private String password;

    @NotBlank(message = "{user.name.blank}")
    private String name;

    @NotBlank(message = "{user.surname.blank}")
    private String surname;

    @NotBlank(message = "{user.organization.blank}")
    private String organization;

    @NotNull(message = "{user.role.null}")
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull(message = "{user.status.null}")
    @Enumerated(EnumType.STRING)
    private Status status;

//    @Transient
//    public String getFullName() {
//        return name + " " + surname;
//    }

    public static enum Role {
        ROLE_ADMIN, ROLE_CA, ROLE_SIMPSON
    }

    public static enum Status {
        ACTIVE, INACTIVE
    }
}