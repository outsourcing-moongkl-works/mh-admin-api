package org.outsourcing.mhadminapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.entity.Admin;
import org.outsourcing.mhadminapi.exception.AdminErrorResult;
import org.outsourcing.mhadminapi.exception.AdminException;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final AdminRepository adminRepository;

    @Override
    public AdminDto.CreateAdminResponse createAdmin(AdminDto.CreateAdminRequest request) {

        Admin admin = Admin.builder()
                .adminId(request.getAdminId())
                .password(request.getPassword())
                .role(Role.valueOf(request.getRole()))
                .build();

        adminRepository.save(admin);

        log.info("admin created: {}", admin.getId());

        return AdminDto.CreateAdminResponse.builder()
                .id(admin.getId().toString())
                .role(admin.getRole().name())
                .build();
    }
}
