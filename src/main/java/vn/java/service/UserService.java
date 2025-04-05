package vn.java.service;

import jakarta.mail.MessagingException;
import vn.java.dto.request.UserCreationRequest;
import vn.java.dto.request.UserPasswordRequest;
import vn.java.dto.request.UserUpdateRequest;
import vn.java.dto.response.UserPageResponse;
import vn.java.dto.response.UserResponse;

import java.io.UnsupportedEncodingException;

public interface UserService {
    UserPageResponse findAll(String keyword, String sort, int page, int size);

    UserResponse findById(Long id);

    long save(UserCreationRequest req) throws MessagingException, UnsupportedEncodingException;

    void update(UserUpdateRequest req);

    void changePassword(UserPasswordRequest req);

    void delete(Long id);
}