package vn.java.service;

import vn.java.common.TokenType;

import java.util.List;

public interface JwtService {

    String generateAccessToken( String email, List<String> authorities);

    String generateRefreshToken(String email, List<String> authorities);

    String extractUserName(String token, TokenType tokenType);

}
