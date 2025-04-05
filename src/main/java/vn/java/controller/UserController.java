package vn.java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.java.dto.request.UserCreationRequest;
import vn.java.dto.request.UserPasswordRequest;
import vn.java.dto.request.UserUpdateRequest;
import vn.java.dto.response.DataResponse;
import vn.java.dto.response.FailureResponse;
import vn.java.dto.response.UserPageResponse;
import vn.java.dto.response.UserResponse;
import vn.java.service.UserService;

import java.io.UnsupportedEncodingException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@Slf4j(topic = "USER_CONTROLLER")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public DataResponse<Object> getAllUsers(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        log.info("Retrieve all users");

        try {
            UserPageResponse result = userService.findAll(keyword, sort, page, size);
            return new DataResponse<>(HttpStatus.OK.value(), "Users retrieved successfully", result);
        } catch (Exception e) {
            log.error("Error Message: {}", e.getMessage());
            return new FailureResponse(BAD_REQUEST.value(), e.getMessage());
        }

    }

    @Operation(summary = "Get user details")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public DataResponse<Object> getUserDetails(@PathVariable @Min(value = 1, message = "UserId must be equal to or greater than 1") Long userId) {
        log.info("Get user details: {}", userId);

        try {
            UserResponse result = userService.findById(userId);
            return new DataResponse<>(HttpStatus.OK.value(), "User details retrieved successfully", result);
        } catch (Exception e) {
            log.error("Error Message: {}", e.getMessage());
            return new FailureResponse(BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Create new user", description = "API add mew user to database")
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse<Object> createNewUser(@RequestBody @Valid UserCreationRequest request) throws MessagingException, UnsupportedEncodingException {
        log.info("Create user: {}", request);

        try {
            long result = userService.save(request);
            return new DataResponse<>(HttpStatus.CREATED.value(), "User created successfully", result);
        } catch (Exception e) {
            log.error("Error Message: {}", e.getMessage());
            return new FailureResponse(BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Update User", description = "API add mew user to database")
    @PutMapping("/upd")
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public DataResponse<Object> updateUser(@RequestBody @Valid UserUpdateRequest request) {
        log.info("Update user: {}", request);

        try {
            userService.update(request);
            return new DataResponse<>(HttpStatus.ACCEPTED.value(), "User updated successfully");
        } catch (Exception e) {
            log.error("Error Message: {}", e.getMessage());
            return new FailureResponse(BAD_REQUEST.value(), e.getMessage());
        }

    }

    @Operation(summary = "Change User Password", description = "API change password for user to database")
    @PatchMapping("/change-pwd")
    @PreAuthorize("hasRole('USER')")
    public DataResponse<Object> changePassword(@RequestBody @Valid UserPasswordRequest request) {
        log.info("Change password for user: {}", request);

        try {
            userService.changePassword(request);
            return new DataResponse<>(HttpStatus.ACCEPTED.value(), "Password updated successfully");
        } catch (Exception e) {
            log.error("Error Message: {}", e.getMessage());
            return new FailureResponse(BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Delete User", description = "API activate user to database")
    @DeleteMapping("/del/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse<Object> deleteUser(@PathVariable @Min(value = 1, message = "userId must be equals or greater than 1") Long userId) {
        log.info("Deleting user: {}", userId);

        try {
            userService.delete(userId);
            return new DataResponse<>(HttpStatus.RESET_CONTENT.value(), "User deleted successfully");
        } catch (Exception e) {
            log.error("Error Message: {}", e.getMessage());
            return new FailureResponse(BAD_REQUEST.value(), e.getMessage());
        }
    }
}
