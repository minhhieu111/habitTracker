package com.example.habittracker.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        if (status != null) {
//            int statusCode = Integer.valueOf(status.toString());
//            if (statusCode == HttpStatus.FORBIDDEN.value()) {
//                return "error/403";
//            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
//                return "error/404";
//            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
//                return "error/500";
//            }
//        }
        return "error/general";
    }
}

