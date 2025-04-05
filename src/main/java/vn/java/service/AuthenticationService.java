package vn.java.service;

import vn.java.dto.request.SignInRequest;
import vn.java.dto.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse getAccessToken(SignInRequest request);

    TokenResponse getRefreshToken(String request);
}
