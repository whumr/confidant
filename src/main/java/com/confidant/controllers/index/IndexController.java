package com.confidant.controllers.index;

import com.confidant.common.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2015/1/29.
 */
@Controller("indexController")
@RequestMapping("")
public class IndexController extends BaseController {

    @RequestMapping({"", "/index"})
    public String index() throws Exception {
        return "index";
    }

    @RequestMapping("/{path}")
    public String jsp(HttpServletRequest request, HttpServletResponse response, @PathVariable("path") String path) throws Exception {
        if ("login".equals(path) || "reg".equals(path))
            redirect(response, "/member/" + path);
        return null;
    }
}
