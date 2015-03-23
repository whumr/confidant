package com.confidant.controllers.admin;

import com.confidant.common.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2015/1/29.
 */
@Controller("adminController")
@RequestMapping("/admin")
public class AdminController extends BaseController {

    public static final String PASS = "123456", ENTRY_KEY = "checked", INDEX_JSP = "c1.jsp";

    @RequestMapping({"/enter"})
    public String enter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute(ENTRY_KEY) == null) {
            String pass = request.getParameter("pass");
            if (pass != null && PASS.equals(pass)) {
                request.getSession().setAttribute(ENTRY_KEY, "1");
                redirect(response, "index");
            } else
                redirect(response, "/index");
        } else
            redirect(response, "index");
        return null;
    }

    @RequestMapping({"", "/index"})
    public String index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (checkLogin(request, response))
            return "admin/user_reg";
        return null;
    }

    @RequestMapping("/{jsp}")
    public String jsp(HttpServletRequest request, HttpServletResponse response, @PathVariable("jsp")String jsp) throws Exception {
        if (checkLogin(request, response)) {
            if (jsp == null || "".equals(jsp.trim()) || "index".equals(jsp.trim()))
                jsp = "index";
            return "admin/" + jsp;
        }
        return null;
    }

    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute(ENTRY_KEY) == null) {
            redirect(response, "/index");
            return false;
        }
        return true;
    }
}
