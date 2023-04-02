package utils;

import javax.servlet.http.HttpServletRequest;

public class ParameterGetter {
    HttpServletRequest request;

    public ParameterGetter(HttpServletRequest request) {
        this.request = request;
    }

    public String getLastParameter(){
        String[] parts = request.getRequestURI().split("/");
        return parts[parts.length - 1];
    }
    public String getLastButOneParameter(){
        String[] parts = request.getRequestURI().split("/");
        return parts[parts.length - 2];
    }
}
