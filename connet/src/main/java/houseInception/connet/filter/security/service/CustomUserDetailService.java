package houseInception.connet.filter.security.service;

import houseInception.connet.domain.user.User;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.filter.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static houseInception.connet.domain.Status.ALIVE;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndStatus(email, ALIVE).orElse(null);
        if (user == null || user.getRefreshToken() == null) {
            return null;
        }
        return new CustomUserDetails(user);
    }
}
