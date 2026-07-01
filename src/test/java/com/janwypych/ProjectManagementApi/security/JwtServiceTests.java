package com.janwypych.ProjectManagementApi.security;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.entities.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtServiceTests {
    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateToken() {
        User user = TestDataUtil.user();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractUserIdFromGeneratedToken() {
        User user = TestDataUtil.user();

        String token = jwtService.generateToken(user);
        Long userId = jwtService.extractUserId(token);

        assertEquals(1L, userId);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        assertThrows(
                JwtException.class,
                () -> jwtService.extractUserId("invalid-token")
        );
    }
}
