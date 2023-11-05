package ltd.jellyfish.security.web.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class BaseUser {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;


    public Integer getId() {
        return id;
    }

    public BaseUser setId(Integer id) {
        this.id = id;
        return this;
    }

    public BaseUser() {
    }

    public BaseUser(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public BaseUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public BaseUser setPassword(String password) {
        this.password = password;
        return this;
    }
}
