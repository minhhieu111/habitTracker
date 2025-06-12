package com.example.habittracker.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

//    @RequestMapping("/error")
//    public String handleError(HttpServletRequest request) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        if (status != null) {
//            int statusCode = Integer.valueOf(status.toString());
//            if (statusCode == HttpStatus.FORBIDDEN.value()) {
//                // Xử lý lỗi 403
//                return "error/403";  // Trả về trang error/403.html hoặc error/403.jsp
//            }
//        }
//        return "error/general"; // Trang lỗi mặc định
//    }
}

