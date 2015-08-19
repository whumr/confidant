package com.confidant.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.confidant.entity.Member;
import com.confidant.util.Validator;
import org.springframework.stereotype.Controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2015/1/29.
 */
@Controller
public class BaseController {

    protected JSONObject SUCCESS_JSON = JSON.parseObject("{status:1}");
    protected JSONObject FAIL_JSON = JSON.parseObject("{status:0}");

    private static String MESSAGE = "error";

    public void renderJson(HttpServletResponse response, Object object) {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(JSON.toJSONString(object));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public void renderText(HttpServletResponse response, String text) {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public void renderJsp(HttpServletRequest request, HttpServletResponse response, String path) {
        try {
            RequestDispatcher dispatcher = request.getRequestDispatcher(path);
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void redirect(HttpServletResponse response, String path) {
        try {
            response.sendRedirect(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void succeed(HttpServletResponse response) {
        renderJson(response, SUCCESS_JSON);
    }

    public void fail(HttpServletResponse response) {
        renderJson(response, FAIL_JSON);
    }

    protected String checkParams(HttpServletRequest request, HttpServletResponse response, String[] keys, boolean[] notEmpty) {
        for (int i = 0; i < keys.length; i++) {
            String value = request.getParameter(keys[i]);
            if (notEmpty[i] ? value != null && !"".equals(value.trim()) : value != null)
                continue;
            else
                return keys[i];
        }
        return null;
    }

    protected boolean chekValuesEmpty(String[] values) {
        return chekValuesEmpty(values, true);
    }

    protected boolean chekValuesEmpty(String[] values, boolean trim) {
        for (String value : values)
            if (Validator.emptyString(value, trim))
                return true;
        return false;
    }

    public void fail(HttpServletResponse response, String msg) {
        JSONObject fail = (JSONObject)FAIL_JSON.clone();
        fail.put(MESSAGE, msg);
        renderJson(response, fail);
    }

    protected Member getCurrentUser(HttpServletRequest request) {
        Object o = request.getSession().getAttribute(Constants.Keys.Session.KeyMember);
        return o == null ? null : (Member)o;
    }

    protected JSONObject successJson() {
        return (JSONObject)SUCCESS_JSON.clone();
    }

    protected JSONObject failJson() {
        return (JSONObject)FAIL_JSON.clone();
    }
}
