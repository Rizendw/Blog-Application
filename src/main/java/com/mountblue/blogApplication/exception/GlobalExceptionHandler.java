package com.mountblue.blogApplication.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 🟠 handle AccessDenied (403)
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex, HttpServletRequest req, Model model) {
        return handle(req, HttpStatus.FORBIDDEN, ex.getMessage(), model);
    }

    // 🔴 bad request or invalid args
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req, Model model) {
        return handle(req, HttpStatus.BAD_REQUEST, ex.getMessage(), model);
    }

    // 🔵 not found (post/comment/user)
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public Object handleNotFound(Exception ex, HttpServletRequest req, Model model) {
        return handle(req, HttpStatus.NOT_FOUND, ex.getMessage(), model);
    }

    // ⚫ fallback
    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, HttpServletRequest req, Model model) {
        return handle(req, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.", model);
    }


    // ---------------- internal helper ----------------

    private Object handle(HttpServletRequest req, HttpStatus status, String message, Model model) {
        boolean wantsJson =
                req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json");

        if (wantsJson) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", Instant.now().toString());
            body.put("status", status.value());
            body.put("error", status.getReasonPhrase());
            body.put("message", message);
            body.put("path", req.getRequestURI());
            return new ResponseEntity<>(body, status);
        } else {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("status", status.value());
            mav.addObject("error", status.getReasonPhrase());
            mav.addObject("message", message);
            mav.addObject("path", req.getRequestURI());
            return mav;
        }
    }
}
