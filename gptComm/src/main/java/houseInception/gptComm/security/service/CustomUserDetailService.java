package houseInception.gptComm.security.service;

import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.User;
import houseInception.gptComm.repository.UserRepository;
import houseInception.gptComm.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static houseInception.gptComm.domain.Status.ALIVE;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndStatus(email, ALIVE).orElse(null);
        if(user == null){
            return null;
        }
        return new CustomUserDetails(user);
    }
}
