package org.outsourcing.mhadminapi.auth;

import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.entity.Admin;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String adminId) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByAdminId(adminId).orElseThrow(
                () -> new UsernameNotFoundException("Admin not found with id : " + adminId)
        );

        return UserPrincipal.create(admin);
    }
}
