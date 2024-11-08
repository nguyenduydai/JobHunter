package vn.it.jobhunter.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.it.jobhunter.domain.response.RestResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        System.out.println("Request URL: " + request.getRequestURI());
        // if (isEndpointInWhiteList(request.getRequestURI())) {
        // response.setContentType("application/json;charset=UTF-8");

        // RestResponse<Object> res = new RestResponse<>();
        // res.setStatusCode(440);
        // String errorMessage = Optional.ofNullable(authException.getCause())
        // .map(Throwable::getMessage)
        // .orElse(authException.getMessage());
        // res.setError(errorMessage);
        // res.setMessage("Token het han ");
        // mapper.writeValue(response.getWriter(), res);
        // }
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        String errorMessage = Optional.ofNullable(authException.getCause())
                .map(Throwable::getMessage)
                .orElse(authException.getMessage());
        res.setError(errorMessage);
        res.setMessage("Token khong hop le ");
        mapper.writeValue(response.getWriter(), res);
    }

    // private boolean isEndpointInWhiteList(String requestURI) {
    // String[] whiteList = {
    // "/",
    // "/api/v1/auth/login",
    // "/api/v1/auth/refresh",
    // "/api/v1/auth/register", "/api/v1/auth/account"
    // };
    // for (String path : whiteList) {
    // if (requestURI.startsWith(path)) {
    // return true;
    // }
    // }
    // return false;
    // }
}
