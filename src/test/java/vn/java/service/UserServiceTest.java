package vn.java.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.java.common.Gender;
import vn.java.common.UserStatus;
import vn.java.common.UserType;
import vn.java.dto.request.AddressRequest;
import vn.java.dto.request.UserCreationRequest;
import vn.java.dto.response.UserPageResponse;
import vn.java.dto.response.UserResponse;
import vn.java.exception.ResourceNotFoundException;
import vn.java.model.User;
import vn.java.repository.AddressRepository;
import vn.java.repository.UserRepository;
import vn.java.service.impl.UserServiceImpl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    private @Mock UserRepository userRepository;
    private @Mock AddressRepository addressRepository;
    private @Mock PasswordEncoder passwordEncoder;
    private @Mock MailService emailService;

    private static User user1;
    private static User user2;

    @BeforeAll
    static void beforeAll() {
        // Dữ liệu giả lập
        user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Tay");
        user1.setLastName("Java");
        user1.setGender(Gender.MALE);
        user1.setBirthday(new Date());
        user1.setEmail("quoctay87@gmail.com");
        user1.setPhone("0975118228");
        user1.setUsername("tayjava");
        user1.setPassword("password");
        user1.setType(UserType.USER);
        user1.setStatus(UserStatus.ACTIVE);

        user2 = new User();
        user2.setId(2L);
        user2.setFirstName("John");
        user2.setLastName("Doe");
        user2.setGender(Gender.FEMALE);
        user2.setBirthday(new Date());
        user2.setEmail("johndoe@gmail.com");
        user2.setPhone("0123456789");
        user2.setUsername("johndoe");
        user2.setPassword("password");
        user2.setType(UserType.USER);
        user2.setStatus(UserStatus.INACTIVE);
    }

    @BeforeEach
    void setUp() {
        // Khởi tạo lớp triển khai của UserService
        userService = new UserServiceImpl(userRepository, addressRepository, passwordEncoder, emailService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetListUsers_Success() {
        // Giả lập phương thức search của UserRepository
        Page<User> userPage = new PageImpl<>(List.of(user1, user2));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Gọi phương thức cần kiểm tra
        UserPageResponse result = userService.findAll(null, null, 0, 20);

        Assertions.assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testSearchUser_Success() {
        // Giả lập phương thức search của UserRepository
        Page<User> userPage = new PageImpl<>(List.of(user1, user1));
        when(userRepository.searchByKeyword(any(), any(Pageable.class))).thenReturn(userPage);

        // Gọi phương thức cần kiểm tra
        UserPageResponse result = userService.findAll("tay", null, 0, 20);

        Assertions.assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("tayjava", result.getUsers().get(0).getUsername());
    }

    @Test
    void testGetUserList_Empty() {
        // Giả lập hành vi của UserRepository
        Page<User> userPage = new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Gọi phương thức cần kiểm tra
        UserPageResponse result = userService.findAll(null, null, 0, 20);

        Assertions.assertNotNull(result);
        assertEquals(0, result.getUsers().size());
    }

    @Test
    void testGetUserById_Success() {
        // Giả lập hành vi của UserRepository
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // Gọi phương thức cần kiểm tra
        UserResponse result = userService.findById(1L);

        Assertions.assertNotNull(result);
        assertEquals("tayjava", result.getUsername());
    }

    @Test
    void testGetUserById_Failure() {
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> userService.findById(10L));
        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testSave_Success() throws MessagingException, UnsupportedEncodingException {
        // Giả lập hành vi của UserRepository
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setFirstName("Tay");
        userCreationRequest.setLastName("Java");
        userCreationRequest.setGender(Gender.MALE);
        userCreationRequest.setBirthday(new Date());
        userCreationRequest.setEmail("quoctay87@gmail.com");
        userCreationRequest.setPhone("0975118228");
        userCreationRequest.setUsername("tayjava");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setApartmentNumber("ApartmentNumber");
        addressRequest.setFloor("Floor");
        addressRequest.setBuilding("Building");
        addressRequest.setStreetNumber("StreetNumber");
        addressRequest.setStreet("Street");
        addressRequest.setCity("City");
        addressRequest.setCountry("Country");
        addressRequest.setAddressType(1);
        userCreationRequest.setAddresses(List.of(addressRequest));

        // Gọi phương thức cần kiểm tra
        long result = userService.save(userCreationRequest);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1L, result);
    }
}