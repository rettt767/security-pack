package ltd.jellyfish.security.web.model;

public class BaseRoleToUser {

    private Integer userId;

    private Integer roleId;

    public BaseRoleToUser(Integer userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public BaseRoleToUser() {
    }

    public Integer getUserId() {
        return userId;
    }

    public BaseRoleToUser setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public BaseRoleToUser setRoleId(Integer roleId) {
        this.roleId = roleId;
        return this;
    }
}
