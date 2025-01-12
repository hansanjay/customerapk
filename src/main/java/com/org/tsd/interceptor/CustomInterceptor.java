package com.org.tsd.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.tsd.models.APIResponse;
import com.org.tsd.models.AuthResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomInterceptor implements HandlerInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomInterceptor.class);
	
	@Autowired
    private CustomerAuthService customerAuthService;

    @Autowired
    private ObjectMapper mapper;

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("Interceptor triggered for URI: " + request.getRequestURI());
//        return true;
//    }
    
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		logger.info("Request received for :: " + request.getRequestURI());
		
		if(request.getRequestURI().contains("/otp")
				|| request.getRequestURI().contains("/auth")) {
			return true;
		}else {
			AuthResponse authResponse = customerAuthService.isAuthenticated(request);
			if (authResponse.isResult()) {
				return true;
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				APIResponse apiResponse = new APIResponse(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed",
						authResponse.getMessage());
				response.getOutputStream().write(mapper.writeValueAsBytes(apiResponse));
				return false; // Stop further processing
			}
		}
	}
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                    Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                         Object handler, Exception ex) throws Exception {
        if (ex != null) ex.printStackTrace();
    }
}
	