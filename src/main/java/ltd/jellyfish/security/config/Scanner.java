package ltd.jellyfish.security.config;


import ltd.jellyfish.security.Secure;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

public class Scanner {

    public Map<String, String[]> getUrlToRole(String basePackage){

        ClassesGetter classesGetter = new ClassesGetter();

        Set<Class<?>> classes = classesGetter.getClasses(basePackage);
        Map<String, String[]> reply = new HashMap<>();
        Method[] methods = null;
        for (Class c : classes){
            if (c.isAnnotationPresent(RestController.class)){
                methods = c.getMethods();
                String[] role = null;
                String url = null;
                for (Method method : methods){
                    if (method.isAnnotationPresent(RequestMapping.class)){

                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        url = requestMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                    if (method.isAnnotationPresent(PostMapping.class)){

                        PostMapping postMapping = method.getAnnotation(PostMapping.class);
                        url = postMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                    if (method.isAnnotationPresent(GetMapping.class)){

                        GetMapping getMapping = method.getAnnotation(GetMapping.class);
                        url = getMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                    if (method.isAnnotationPresent(PutMapping.class)){
                        PutMapping putMapping = method.getAnnotation(PutMapping.class);
                        url = putMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                    if (method.isAnnotationPresent(DeleteMapping.class)){
                        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
                        url = deleteMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                }
            }

            if (c.isAnnotationPresent(Controller.class)){
                methods = c.getMethods();
                String[] role = null;
                String url = null;
                for (Method method : methods){
                    if (method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        url = requestMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                    if (method.isAnnotationPresent(PostMapping.class)){
                        PostMapping postMapping = method.getAnnotation(PostMapping.class);
                        url = postMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                    if (method.isAnnotationPresent(GetMapping.class)){
                        GetMapping getMapping = method.getAnnotation(GetMapping.class);
                        url = getMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                    if (method.isAnnotationPresent(PutMapping.class)){
                        PutMapping putMapping = method.getAnnotation(PutMapping.class);
                        url = putMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                    if (method.isAnnotationPresent(DeleteMapping.class)){
                        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
                        url = deleteMapping.value()[0];
                        getRoles(method, url, reply);
                    }
                }
            }
        }
        return reply;

    }

    private void getRoles(Method method, String url, Map<String, String[]> relectes){
        String[] role = null;
        if (method.isAnnotationPresent(Secure.class)){
            Secure secure = method.getAnnotation(Secure.class);
            role = secure.value();
            relectes.put(url, role);
        }else {
            String[] roles = new String[1];
            roles[0] = "ALL";
            relectes.put(url, roles);
        }
    }


}
