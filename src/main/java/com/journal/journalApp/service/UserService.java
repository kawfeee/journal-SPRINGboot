package com.journal.journalApp.service;

import com.journal.journalApp.entity.User;
import com.journal.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void saveNewUser(User user){
        // FOR LOGGING PRACTICE, check for existing username first to avoid driver DuplicateKeyException bubbling up
        if(userRepository.findByUserName(user.getUserName()) != null){
            log.error("boohooboohoo");
//            logger.warn("Attempt to create user with existing userName={}", user.getUserName());
//            logger.info("boohooboohoo");
            logger.debug("boohooboohoo");
//            logger.trace("boohooboohoo");
            throw new RuntimeException("Username already exists: " + user.getUserName());
        }

        if(user.getRoles() == null || user.getRoles().isEmpty()){
            user.setRoles(List.of("USER"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void saveAdmin(User user){
        if(user.getRoles() == null || user.getRoles().isEmpty()){
            user.setRoles(List.of("USER", "ADMIN"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id){
        return userRepository.findById(id);
    }

    public void deleteById(ObjectId id){
        userRepository.deleteById(id);
    }

    public User findByUserName(String userName){
        return userRepository.findByUserName(userName);
    }
}

//controller --> service --> repository
