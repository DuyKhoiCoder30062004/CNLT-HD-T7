package com.example.ecommerce.controllers;

import com.example.ecommerce.dtos.auth.LoginRequest;
import com.example.ecommerce.dtos.auth.LoginResponse;
import com.example.ecommerce.dtos.auth.RegisterRequest;
import com.example.ecommerce.config.SecurityConfig;
import com.example.ecommerce.security.JwtService;
import com.example.ecommerce.interfaces.auth.IAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAuth authService;

	@MockitoBean
    private JwtService jwtService;

    @Test
    void shouldLoginSuccessfully() throws Exception{
        LoginResponse response = new LoginResponse();

        when(authService.login(any(LoginRequest.class)))
            .thenReturn(response);

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "phone":"0987654321",
                        "password":"password"
                    }
                    """
                )
        ).andExpect(status().isOk());
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception{
    	LoginResponse response = new LoginResponse();
    	when(authService.register(any(RegisterRequest.class)))
    		.thenReturn(response);

    	mockMvc.perform(
    		post("/api/auth/register")
    		.contentType(MediaType.APPLICATION_JSON)
    		.content("""
	    		{
	    			"username":"dr.phuc",
	    			"password":"password",
	    			"email":"2phuc@gmail.com",
	    			"phone":"0987654323"
	    		}
    			""")
    	).andExpect(status().isOk());
    }

    @Test
    void shouldGetCurrentUserSuccessfully() throws Exception {
        String dummyToken = "valid-dummy-token";
        String phone = "0987654321";

        when(jwtService.isValid(dummyToken)).thenReturn(true);
        when(jwtService.extractPhone(dummyToken)).thenReturn(phone);

        LoginResponse response = new LoginResponse();
        when(authService.me(phone)).thenReturn(response);
        UsernamePasswordAuthenticationToken mockAuth = new UsernamePasswordAuthenticationToken(
            phone,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        mockMvc.perform(
            get("/api/auth/me")
                .header("Authorization", "Bearer " + dummyToken)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuth))
        ).andExpect(status().isOk());
    }
}