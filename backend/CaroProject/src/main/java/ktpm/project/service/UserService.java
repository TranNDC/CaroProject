package ktpm.project.service;

import ktpm.project.dto.SingInForm;
import ktpm.project.dto.UserDTO;
import ktpm.project.model.UserDAO;
import ktpm.project.repository.RankRepo;
import ktpm.project.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    public static final Integer SUCCESS = 0;
    public static final Integer DTB_FAIL = 1;
    public static final Integer USERNAME_EXISTED = 2;
    public static final Integer USERNAME_NOT_EXISTED = 3;
    public static final Integer PASSWORD_INCORRECT = 4;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RankRepo rankRepo;

    @Value("${number.avatar}")
    Integer nAva;

    @Value("${init.points}")
    private Integer initPoint;

    @Autowired
    RankService rankService;

    public Integer registerUser(SingInForm user) {
        UserDAO userDAO = !userRepo.findFirstByUsername(user.getUsername()).isPresent() ? new UserDAO(user.getUsername(), passwordEncoder.encode(user.getPassword()))
                : null;
        if (userDAO == null)
            return USERNAME_EXISTED;
        userDAO.setRamdomAvatar(nAva);
        userDAO.setPoints(initPoint);
        try {
            userRepo.save(userDAO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return DTB_FAIL;
        }
        rankService.AddToRank(user.getUsername());
        return SUCCESS;
    }

    public  List<Object> login(SingInForm user) {
        UserDAO userDAO = userRepo.findFirstByUsername(user.getUsername()).orElse(null);
        if (userDAO == null)
            return Arrays.asList(USERNAME_NOT_EXISTED);

        if (passwordEncoder.matches(user.getPassword(),userDAO.getPassword())){
            return Arrays.asList(SUCCESS,new UserDTO(userDAO,rankRepo.getRankByUsername(user.getUsername())));
        }
        else{
            return Arrays.asList(PASSWORD_INCORRECT);
        }
    }

    public UserDTO getUserByUsername(String username) {
        UserDAO userDAO = userRepo.findFirstByUsername(username).orElse(null);
        if (userDAO == null) return null;
        logger.warn(username);
        logger.warn(String.valueOf(rankRepo.getRankByUsername(username)));
        return new UserDTO(userDAO,rankRepo.getRankByUsername(username));
    }


//    public UserDTO getUserById(String id) {
//        UserDAO userDAO = userRepo.findById(id).orElse(null);
//        if (userDAO == null) return null;
//        return new UserDTO(userDAO);
//    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return getUserByUsername(s);
    }
}