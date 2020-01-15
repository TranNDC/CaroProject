package ktpm.project.controller.http;

import ktpm.project.dto.*;
//import ktpm.project.service.UserService;
import ktpm.project.jwt.JwtUtil;
import ktpm.project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UserController {
    @Autowired
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping(value = "/api/register")
    public ResponseEntity<?> register(@RequestBody SingInForm req){
        Integer res = userService.registerUser(req);

        if (res.equals(UserService.SUCCESS)){
            TokenDTO response = new TokenDTO();
            response.setToken(jwtUtil.generateToken(req.getUsername()));
            return ResponseEntity.ok(response);
        }
        else if (res.equals(UserService.USERNAME_EXISTED)){
            ErrorDTO errorDTO = new ErrorDTO("Register Fail", "Username existed");
            return new ResponseEntity<>(errorDTO,HttpStatus.BAD_REQUEST);
        }
        else if (res.equals(UserService.DTB_FAIL)){
            ErrorDTO errorDTO = new ErrorDTO("Register Fail", "Something was wrong. Please try again");
            return new ResponseEntity<>(errorDTO,HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping(value = "/api/auth")
    public ResponseEntity<?> login(@RequestBody SingInForm req){
        List<Object> loginResult = userService.login(req);
        if (loginResult.get(0) == UserService.SUCCESS){
            AuthenticationDTO authenticationDTO = new AuthenticationDTO();
            authenticationDTO.setToken(jwtUtil.generateToken(req.getUsername()));
            authenticationDTO.setUser((UserDTO)loginResult.get(1));
            return ResponseEntity.ok(authenticationDTO);
        }
        else {
            ErrorDTO errorDTO = new ErrorDTO("Login Fail","Username or Password is incorrect.");
            return new ResponseEntity<>(errorDTO,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/api/users")
    public ResponseEntity<?> getUser(@RequestParam String username){
        UserDTO userDTO = userService.getUserByUsername(username);

        if (userDTO != null){
            return ResponseEntity.ok(userDTO);
        }
        else {
            ErrorDTO errorDTO = new ErrorDTO("404","Username is not existed.");
            return new ResponseEntity<>(errorDTO,HttpStatus.NOT_FOUND);
        }
    }
}
