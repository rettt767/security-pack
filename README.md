# security-pack

此项目为SpringSecurity的简化框架

## 使用方法

此项目中整合了mybatis-Plus

### 定义实体类
当你在使用这个框架的时候，需要首先定义三个实体类  
这三个实体类分别是：`User`, `Role`, `RoleToUser`，他们需要分别继承三个类  
`ltd.jellyfish.security.web.model.BaseUser`  
`ltd.jellyfish.security.web.model.BaseRole`  
`ltd.jellyfish.security.web.model.BaseRoleToUser`  
继承关系为：  
`User` <-- `BaseUser`  
`Role` <-- `BaseRole`  
`RoleToUser` <-- `BaseRoleToUser`  

### 定义Mapper
我们需要定义三个Mapper以对应三个实体类  
创建三个Interface和Mapper.xml  
`UserMapper.java`::`UserMapper.xml`  
`RoleMapper.java`::`RoleMapper.xml`  
`RoleToUserMapper.java`::`RoleToUserMapper.xml`  
这三个Mapper均要继承BaseMapper，并且指明其泛型

### 配置PasswordEncoder
我们需要对密码进行加密，来确保密码的安全性:

```java
package com.xx.xx;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // 这里可以选择很多种加密方式

//        return new Pbkdf2PasswordEncoder();
//        Pbkdf2加密方式

//        return new SCryptPasswordEncoder();
//        SCrypt加密方式
    }
}
```


### 配置Token工具类
此包种包含一个Token工具类，当我们要使用它时，首先要将其放入bean种  
同时，你需要在application.yml中写入相关配置

`TokenConfig.java`:
```java
package com.xx.xx;

import ltd.jellyfish.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class TokenConfig {

    @Value("${jwt.expire}")
    private long expire; // token过期时间，单位为秒

    @Value("${jwt.secret}")
    private String secret; // token秘钥

    @Value("${jwt.header}")
    private String header; // token的头

    @Bean
    public TokenUtils tokenUtils() {
        return new TokenUtils(expire, secret, header);
    }
}
```

`application.yml`:
```yaml
jwt:
  expire: 604800
  secret: 12345678
  header: header
```


### 配置客制化Bean
在使用当前框架的时候我们需要初始化一些东西  

我们已经创建了`UserMapper`,`RoleMapper`,`RoleToUserMapper`  
也已经创建完成了`TokenUtil`,`PasswordEncoder`  
现在我们需要对我们的组件进行初始化：

```java
package com.xx.xx;

import ltd.jellyfish.bean.ConfigBean;
import ltd.jellyfish.security.web.adapter.ManageAuth;
import ltd.jellyfish.security.web.config.WebSecureConfig;
import ltd.jellyfish.security.web.service.UserDetailsServiceImpl;
import ltd.jellyfish.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomConfigBean {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleToUserMapper roleToUserMapper;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ConfigBean configBean() {
        Map<String, Object> tokenParams = new HashMap<>();
        // 你可以自定义token的参数列表
        return new ConfigBean(userMapper, roleMapper, roleToUserMapper, passwordEncoder, tokenUtils, /*指定你的项目的包的所在位置*/"com", tokenParams);
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        return configBean().userDetailsService();
    }

    @Bean
    public ManageAuth manageAuth() {
        return configBean().manageAuth();
    }

    @Bean
    public WebSecureConfig webSecureConfig(){
        return configBean().webSecureConfig();
    }
}
```

### 将当前的配置应用的SpringSecurity中
现在我们已经完成了对重要组件的初始化设置，我们可以对当前的组件装配进SpringSecurity了

```java
package com.xx.xx;

import ltd.jellyfish.security.web.config.WebSecureConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private WebSecureConfig webSecureConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return webSecureConfig.matchers(http, "/login"); //可以自己配置登录的url
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return webSecureConfig.authenticationManager();
    }
}
```

### 在controller中使用你的自配置
现在我们的装配基本完成，但是我们怎么定义指定的url有特定的权限呢？  
我们可以使用`@Secure`注解来完成  
- 此注解仅使用于方法

```java

import ltd.jellyfish.security.Secure;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping
    @Secure
    public String testUrl() {
        return "";
    }
}
```

在默认情况下，`@Secure`注解默认此URL为`USER`权限，当这个方法上没有此注解时，则视为所有权限都可以通过，包括未登录用户  
在此注解指定权限的情况下：  
```java
import ltd.jellyfish.security.Secure;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping
    @Secure("ADMIN")
    public String testUrl() {
        return "";
    }
}
```
当然，我们也可以定义多个权限：
```java
import ltd.jellyfish.security.Secure;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping
    @Secure({"USER","ADMIN"})
    public String testUrl() {
        return "";
    }
}
```

### token过滤器
我们可以装配token过滤器来实现长时登录：

```java
import ltd.jellyfish.security.Secure;
import ltd.jellyfish.security.Token;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping
    @Secure({"USER", "ADMIN"})
    @Token(enable = true)
    public String testUrl() {
        return "";
    }
}
```

`@Token`注解默认情况下不生效，而在没有增加此注解或者是没有指明`enable=true`的情况下，此请求不验证请求头中的`token`字段