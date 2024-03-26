package org.outsourcing.mhadminapi.vo;
public enum Role {
    MASTER("ROLE_MASTER"),
    ADMIN("ROLE_SUPERVISOR"),
    MANAGER("ROLE_MANAGER"),
    STAFF("ROLE_STAFF"),
    ENTERPRISE("ROLE_ENTERPRISE"),
    ;

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static boolean isValidRole(String role) {
        for (Role r : Role.values()) {
            if (r.name().equals(role)) {
                return true;
            }
        }
        return false;
    }
    public String getRoleName() {
        return roleName;
    }
}

