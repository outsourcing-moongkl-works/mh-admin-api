package org.outsourcing.mhadminapi.vo;
public enum Role {
    MASTER("ROLE_MASTER"),
    ADMIN("ROLE_SUPERVISOR"),
    MANAGER("ROLE_MANAGER"),
    STAFF("ROLE_STAFF"),
    ;

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}

