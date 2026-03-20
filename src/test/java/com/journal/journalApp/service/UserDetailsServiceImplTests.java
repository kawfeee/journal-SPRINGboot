package com.journal.journalApp.service;

import com.journal.journalApp.repository.UserRepository;
import com.journal.journalApp.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTests {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadByUsernameTest(){
        User testUser = new User(null, "ram", "hingobingo", new ArrayList<>(), new ArrayList<>());
        when(userRepository.findByUserName(ArgumentMatchers.anyString()))
                .thenReturn(testUser);
        UserDetails user = userDetailsService.loadUserByUsername("ram");
        Assertions.assertNotNull(user);
    }
}
