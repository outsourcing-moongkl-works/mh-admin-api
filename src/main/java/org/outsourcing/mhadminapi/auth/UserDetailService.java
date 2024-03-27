package org.outsourcing.mhadminapi.auth;

import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.entity.Admin;
import org.outsourcing.mhadminapi.entity.Enterprise;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.outsourcing.mhadminapi.repository.EnterpriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername: {}", email);

        // Admin 조회
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            return new UserPrincipal(adminOpt.get());
        }

        // Enterprise 조회
        Optional<Enterprise> enterpriseOpt = enterpriseRepository.findByLoginId(email);
        if (enterpriseOpt.isPresent()) {
            return new UserPrincipal(enterpriseOpt.get());
        }

        // 두 엔터티 모두에서 찾을 수 없는 경우
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
