package ltd.jellyfish.security.config;

import ltd.jellyfish.security.Token;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TokenScanner {

    public Map<String, Boolean> getTokenFilterUrl(String basePackage){
        ClassesGetter classesGetter = new ClassesGetter();
        Set<Class<?>> classes = classesGetter.getClasses(basePackage);
        Map<String, Boolean> reply = new HashMap<>();
        for (Class c : classes){
            if (c.isAnnotationPresent(RestController.class) || c.isAnnotationPresent(Controller.class)){
                Method[] methods = c.getMethods();
                for (Method method : methods){
                    String url = null;
                    if (method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        url = requestMapping.value()[0];
                    }
                    if (method.isAnnotationPresent(GetMapping.class)){
                        url = method.getAnnotation(GetMapping.class).value()[0];
                    }
                    if (method.isAnnotationPresent(PostMapping.class)){
                        url = method.getAnnotation(PostMapping.class).value()[0];
                    }
                    if (method.isAnnotationPresent(PutMapping.class)){
                        url = method.getAnnotation(PutMapping.class).value()[0];
                    }
                    if (method.isAnnotationPresent(DeleteMapping.class)){
                        url = method.getAnnotation(DeleteMapping.class).value()[0];
                    }

                    if (method.isAnnotationPresent(Token.class)){
                        Boolean useToken = method.getAnnotation(Token.class).enable();
                        reply.put(url, useToken);
                    }else{
                        reply.put(url, false);
                    }
                }
            }
        }
        return reply;
    }
}
