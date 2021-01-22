package com.kahago.kahagoservice.security;

import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.repository.MUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hendro yuwono
 */
@Service
public class UserServiceSecurity implements UserDetailsService {

    @Autowired
    private MUserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepo.findById(s).map(this::transformUser).orElseThrow(() -> new NotFoundException("user not found"));
    }

    private User transformUser(MUserEntity entity) {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(entity.getUserCategory().getRoleName());
        return new User(entity.getUserId(), entity.getPassword(), authorities);
    }
}
