package org.outsourcing.mhadminapi.service;

import org.outsourcing.mhadminapi.dto.AdminDto;

public interface AdminService {
    AdminDto.CreateAdminResponse createAdmin(AdminDto.CreateAdminRequest request);
    AdminDto.LoginAdminResponse login(AdminDto.LoginAdminRequest request);
    AdminDto.DeleteAdminResponse deleteAdmin(AdminDto.DeleteAdminRequest request);
}
