package rs.map.pki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginRequestDto {

    @NotBlank(message = "{login.email.blank}")
    private String email;

    @NotBlank(message = "{login.password.blank")
    private String password;

}
