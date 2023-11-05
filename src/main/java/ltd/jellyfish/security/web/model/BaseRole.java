package ltd.jellyfish.security.web.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class BaseRole {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String role;

    public BaseRole(Integer id, String role) {
        this.id = id;
        this.role = role;
    }

    public BaseRole() {
    }

    public Integer getId() {
        return id;
    }

    public BaseRole setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getRole() {
        return role;
    }

    public BaseRole setRole(String role) {
        this.role = role;
        return this;
    }
}
