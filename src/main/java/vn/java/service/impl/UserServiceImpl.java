package vn.java.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.java.common.UserStatus;
import vn.java.dto.request.UserCreationRequest;
import vn.java.dto.request.UserPasswordRequest;
import vn.java.dto.request.UserUpdateRequest;
import vn.java.dto.response.UserPageResponse;
import vn.java.dto.response.UserResponse;
import vn.java.exception.InvalidDataException;
import vn.java.exception.ResourceNotFoundException;
import vn.java.model.Address;
import vn.java.model.User;
import vn.java.repository.AddressRepository;
import vn.java.repository.UserRepository;
import vn.java.service.MailService;
import vn.java.service.UserService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER_SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Override
    public UserPageResponse findAll(String keyword, String sort, int page, int size) {
        // Sorting
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            log.info("Sort user by: {}", sort);
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); //columnName:asc|desc
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    log.info("Sort user by: {} ascending", columnName);
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                } else {
                    log.info("Sort user by: {} descending", columnName);
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }

        // Paging
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<User> userEntities = null;

        if(StringUtils.hasLength(keyword)) {
            keyword = "%" + keyword.toLowerCase() + "%";
            userEntities = userRepository.searchByKeyword(keyword, pageable);
        } else {
            userEntities = userRepository.findAll(pageable);
        }

        return getUserPageResponse(page, size, userEntities);
    }

    private UserPageResponse getUserPageResponse(int page, int size, Page<User> userEntities) {
        List<UserResponse> userList = userEntities.stream().map(
                userEntity -> UserResponse.builder()
                        .id(userEntity.getId())
                        .firstName(userEntity.getFirstName())
                        .lastName(userEntity.getLastName())
                        .gender(userEntity.getGender())
                        .birthday(userEntity.getBirthday())
                        .username(userEntity.getUsername())
                        .phone(userEntity.getPhone())
                        .email(userEntity.getEmail())
                        .build()
        ).toList();

        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.setPageNumber(page);
        userPageResponse.setPageSize(size);
        userPageResponse.setTotalPages(userEntities.getTotalPages());
        userPageResponse.setTotalElements(userEntities.getTotalElements());
        userPageResponse.setUsers(userList);
        return userPageResponse;
    }

    @Override
    public UserResponse findById(Long id) {
        log.info("Find user by id: {}", id);

        User userEntity = getUserEntity(id);

        return UserResponse.builder()
                .id(id)
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .birthday(userEntity.getBirthday())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(UserCreationRequest req) throws MessagingException, UnsupportedEncodingException {
        log.info("Saved user: {}", req);

        User userByEmail = userRepository.findByEmail(req.getEmail());
        if (userByEmail != null) {
            throw new InvalidDataException("Email is already existed");
        }

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setBirthday(req.getBirthday());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setType(req.getType());
        user.setStatus(UserStatus.ACTIVE);

        User result = userRepository.save(user);
        log.info("Saved user: {}", user);

        if (result.getId() != null) {
            List<Address> addresses = new ArrayList<>();
            req.getAddresses().forEach(address -> {
                Address addressEntity = new Address();
                addressEntity.setApartmentNumber(address.getApartmentNumber());
                addressEntity.setFloor(address.getFloor());
                addressEntity.setBuilding(address.getBuilding());
                addressEntity.setStreetNumber(address.getStreetNumber());
                addressEntity.setStreet(address.getStreet());
                addressEntity.setCity(address.getCity());
                addressEntity.setCountry(address.getCountry());
                addressEntity.setAddressType(address.getAddressType());
                addressEntity.setUserId(result.getId());
                addresses.add(addressEntity);
            });
            addressRepository.saveAll(addresses);

            if (result.getId() != null) {
                // send email confirmation
                mailService.sendEmailConfirmation(user.getEmail(), user.getId());
            }

            log.info("Saved addresses: {}", addresses);
        }

        return result.getId();
    }

    @Override
    @Transactional
    public void update(UserUpdateRequest req) {
        log.info("Updated user: {}", req);

        User user = getUserEntity(req.getId());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setBirthday(req.getBirthday());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());

        userRepository.save(user);

        List<Address> addresses = new ArrayList<>();

        req.getAddresses().forEach(address -> {
            Address addressEntity = addressRepository.findByUserIdAndAddressType(req.getId(), address.getAddressType());
            if (addressEntity == null) {
                addressEntity = new Address();
            }
            addressEntity.setApartmentNumber(address.getApartmentNumber());
            addressEntity.setFloor(address.getFloor());
            addressEntity.setBuilding(address.getBuilding());
            addressEntity.setStreetNumber(address.getStreetNumber());
            addressEntity.setStreet(address.getStreet());
            addressEntity.setCity(address.getCity());
            addressEntity.setCountry(address.getCountry());

            addresses.add(addressEntity);
        });

        addressRepository.saveAll(addresses);
        log.info("Updated addresses: {}", addresses);
    }

    @Override
    public void changePassword(UserPasswordRequest req) {
        log.info("Changed password for user: {}", req);

        User user = getUserEntity(req.getId());
        if (!user.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        userRepository.save(user);
        log.info("Changed password for user: {}", user);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleted user with id: {}", id);

        User user = getUserEntity(id);
        user.setStatus(UserStatus.INACTIVE);

        userRepository.save(user);
        log.info("Deleted user: {}", user);
    }

    /**
     * Get user entity by id
     *
     * @param id user id
     * @return user entity
     */
    private User getUserEntity(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
