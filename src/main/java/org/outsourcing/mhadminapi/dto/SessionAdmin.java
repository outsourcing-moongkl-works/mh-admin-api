package org.outsourcing.mhadminapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionAdmin implements Serializable {

    private String role;
    private String adminId;
    private String password;
}