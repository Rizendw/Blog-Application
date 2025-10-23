package com.mountblue.blogApplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class FaviconController {
    @GetMapping("favicon.ico")
    public void favicon(HttpServletResponse response) throws IOException {
        response.sendRedirect("/path-to-your-favicon.ico");
    }
}
