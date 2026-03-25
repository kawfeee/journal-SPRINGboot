package com.journal.journalApp.service;

import com.journal.journalApp.repository.UserRepository;
import com.journal.journalApp.entity.User;
import org.junit.jupiter.api.Disabled;
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

@Disabled("Disabled while debugging repository tests")
@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTests {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadByUsernameTest(){
        // use Lombok builder so test compiles with current User class
        User testUser = User.builder()
                .id(null)
                .userName("ram")
                .password("hingobingo")
                .journalEntries(new ArrayList<>())
                .roles(new ArrayList<>())
                .build();
        when(userRepository.findByUserName(ArgumentMatchers.anyString()))
                .thenReturn(testUser);
        UserDetails user = userDetailsService.loadUserByUsername("ram");
        Assertions.assertNotNull(user);
    }
}
