package ltd.jellyfish.security.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ltd.jellyfish.http.model.Result;
import ltd.jellyfish.security.web.model.BaseUser;
import ltd.jellyfish.utils.TokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SuccessHandler implements AuthenticationSuccessHandler {

    private TokenUtils tokenUtils;

    private Map<String, Object> tokenParams;

    public SuccessHandler(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    public SuccessHandler(TokenUtils tokenUtils, Map<String, Object> tokenParams) {
        this.tokenUtils = tokenUtils;
        this.tokenParams = tokenParams;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream servletOutputStream = response.getOutputStream();

        String username = authentication.getName();

        tokenParams.put("username", username);


        String token = tokenUtils.generateToken(tokenParams, "Authorized");

        Result<String> result = Result.ok(200, "登录成功", token);
        String json = JSON.toJSONString(result);
        servletOutputStream.write(json.getBytes(StandardCharsets.UTF_8));
        servletOutputStream.flush();
        servletOutputStream.close();
    }
}
