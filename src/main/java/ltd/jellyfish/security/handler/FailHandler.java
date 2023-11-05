package ltd.jellyfish.security.handler;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ltd.jellyfish.http.model.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream servletOutputStream = response.getOutputStream();
        Result<Void> result = Result.err(exception.getMessage());
        String json = JSON.toJSONString(result);
        servletOutputStream.write(json.getBytes(StandardCharsets.UTF_8));
        servletOutputStream.flush();
        servletOutputStream.close();
    }
}
