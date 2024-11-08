package vn.it.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import vn.it.jobhunter.domain.User;
import vn.it.jobhunter.domain.request.ReqLoginDTO;
import vn.it.jobhunter.domain.response.ResLoginDTO;
import vn.it.jobhunter.domain.response.user.ResCreateUserDTO;
import vn.it.jobhunter.service.UserService;
import vn.it.jobhunter.utils.SecurityUtil;
import vn.it.jobhunter.utils.annotation.ApiMessage;
import vn.it.jobhunter.utils.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder auth, SecurityUtil securityUtil, UserService userService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = auth;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // nạp input gốm username/password vào security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        // xác thực người dùng -> viết lại hàm loadUserByUsername
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // tạo access token
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currUser = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        if (currUser != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currUser.getId(),
                    currUser.getEmail(), currUser.getName(), currUser.getRole());
            resLoginDTO.setUser(userLogin);
        }
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(access_token);
        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);
        // update user
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());
        // set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true).secure(true).path("/").maxAge(refreshTokenExpiration).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(resLoginDTO);
    }

    @GetMapping("/auth/refresh")
    @PreAuthorize("permitAll()")
    @ApiMessage("get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
            throws IdInvalidException {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> DA CHAY VAO HAM REFRESH DAY ROI");
        if (refresh_token.equals("abc")) {
            throw new IdInvalidException("Ban khong co refresh token o cookie");
        }
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> CHECK VALID TOKEN THANH CONG");
        String email = decodedToken.getSubject();
        // check user by token + email
        User currUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currUser == null) {
            throw new IdInvalidException(">>>>>>>>>>> refresh token khong hop le");
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> check user by token + email THANH CONG");
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currUser.getId(),
                currUser.getEmail(), currUser.getName(), currUser.getRole());
        resLoginDTO.setUser(userLogin);
        String access_token = this.securityUtil.createAccessToken(email, resLoginDTO);
        System.out.println("?????????????????????????" + access_token + "?????????????????????????");
        resLoginDTO.setAccessToken(access_token);
        // create refresh token
        String new_refreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);
        // update user
        this.userService.updateUserToken(new_refreshToken, email);
        // set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", new_refreshToken)
                .httpOnly(true).secure(true).path("/").maxAge(refreshTokenExpiration).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currUser = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currUser != null) {
            userLogin = new ResLoginDTO.UserLogin(currUser.getId(),
                    currUser.getEmail(), currUser.getName(), currUser.getRole());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("logout user")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new IdInvalidException("access token khong hop le");
        }
        // update refresh token=null
        this.userService.updateUserToken(null, email);
        // remove refresh token cookie
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true).secure(true).path("/").maxAge(0).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body(null);
    }

    @PostMapping("/auth/register")
    @ApiMessage("register user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("email + " + user.getEmail() + " da ton tai");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User u = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(u));
    }
}
