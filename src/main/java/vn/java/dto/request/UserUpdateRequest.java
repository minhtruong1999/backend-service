package vn.java.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import vn.java.common.Gender;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@ToString
public class UserUpdateRequest implements Serializable {

    @NotNull(message = "Id must be not null")
    @Min(value = 1, message = "Id must be greater than or equal to 1")
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;
    private String username;
    private Gender gender;
    private Date birthday;

    @Email(message = "Email should be valid")
    private String email;
    private String phone;
    private List<AddressRequest> addresses;
}