package com.littlestar.task.Exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 웹 (Thymeleaf)
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model, HttpServletResponse response) {

        HttpStatus status = e.getErrorCode().getStatus();

        response.setStatus(status.value());

        model.addAttribute("status", status.value());
        model.addAttribute("errorMessage", e.getErrorCode().getMessage());
        model.addAttribute("errorCode", e.getErrorCode().getCode());

        return "error"; // error.html
    }

    // ✅ API (JSON)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<String> handleMessage(MessageException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

}
