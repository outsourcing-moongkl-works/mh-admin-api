package org.outsourcing.mhadminapi.service;

import org.outsourcing.mhadminapi.dto.AdminDto;

public interface AdminService {
    AdminDto.CreateAdminResponse createAdmin(AdminDto.CreateAdminRequest request);
}
