package vn.java.dto.response;

import lombok.*;
import vn.java.common.Gender;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private Gender gender;
    private Date birthday;
    private String email;
    private String phone;
}
