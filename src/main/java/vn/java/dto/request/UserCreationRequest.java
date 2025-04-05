package vn.java.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.java.common.Gender;
import vn.java.common.UserType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class UserCreationRequest implements Serializable {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String username;
    private String password;
    private Gender gender;
    private Date birthday;

    @Email(message = "Email should be valid")
    private String email;
    private String phone;
    private UserType type;
    private List<AddressRequest> addresses;
}