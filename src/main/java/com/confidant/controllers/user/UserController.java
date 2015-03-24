package com.confidant.controllers.user;

import com.alibaba.fastjson.JSONObject;
import com.confidant.common.BaseController;
import com.confidant.common.Constants;
import com.confidant.entity.User;
import com.confidant.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2015/1/29.
 */
@Controller("userController")
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService<User> userService;

    @RequestMapping("/save")
    public void saveUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
//        String nick_name = request.getParameter("nick_name");
//        String sex = request.getParameter("sex");
        if (chekValuesEmpty(new String[]{account, password}))
            fail(response, Constants.ErrorMsg.Common.IllegalArgument);
        else {
            User user = new User();
            user.setAccount(account);
            User check_user = userService.getUserByAccount(account);
            if (check_user != null)
                fail(response, Constants.ErrorMsg.User.UserExists);
            else {
                user.setPassword(password);
                String nick_name = account.indexOf("@") > 0 ? account.substring(0, account.indexOf("@")) : account;
                user.setNick_name(nick_name);
//                user.setSex(sex);
                Date now = new Date();
                user.setCreate_time(now);
                user.setLast_updated(now);
                userService.insert(user);
                renderJson(response, user);
            }
        }
    }

    @RequestMapping("/checkAccount")
    public void checkAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String account = request.getParameter("account");
        if (chekValuesEmpty(new String[]{account}))
            fail(response, Constants.ErrorMsg.Common.IllegalArgument);
        else {
            JSONObject result = new JSONObject();
            User user = userService.getUserByAccount(account);
            result.put("exists", user == null ? 0 : 1);
            renderJson(response, result);
        }
    }

    @RequestMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (chekValuesEmpty(new String[]{account, password}))
            fail(response, Constants.ErrorMsg.Common.IllegalArgument);
        else {
            User param = new User();
            param.setAccount(account);
            param.setPassword(password);
            User user = userService.getUserByAccountPassword(param);
            if (user == null)
                fail(response);
            else {
                request.getSession().setAttribute(Constants.Keys.Session.KeyUser, user);
                JSONObject json = successJson();
                json.put("user", user);
                renderJson(response, json);
            }
        }
    }

    @RequestMapping("/uploadImg")
    public void uploadImg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String contentType = request.getContentType();
        String fileName = request.getParameter("file");
        int dataLength = request.getContentLength();
        if (chekValuesEmpty(new String[]{contentType, fileName}) || contentType.indexOf("multipart/form-data") == -1)
            fail(response, Constants.ErrorMsg.Common.IllegalArgument);
        else if (dataLength > FileUtil.MAX_FILE_SIZE)
            fail(response, Constants.ErrorMsg.Common.FileTooLarge);
        else {
            try {
                long userId = getCurrentUser(request).getId();
                String imgPath = userId + System.currentTimeMillis() +
                        fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                FileUtil.copyFile(request.getInputStream(), Constants.Path.UserImg + imgPath);
                User user = new User();
                user.setId(userId);
                user.setImage_url(imgPath);
                user.setLast_updated(new Date());
                userService.updateUser(user);
                JSONObject json = successJson();
                json.put("url", imgPath);
                renderJson(response, json);
            } catch (IOException e) {
                fail(response, e.getMessage());
            }
        }
    }

    @RequestMapping("/updateInfo")
    public void updateInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String nick_name = request.getParameter("nick_name");
        if (chekValuesEmpty(new String[]{nick_name}))
            fail(response, Constants.ErrorMsg.Common.IllegalArgument);
        else {
            User user = new User();
            user.setId(getCurrentUser(request).getId());
            user.setNick_name(nick_name);
            user.setLast_updated(new Date());
            userService.updateUser(user);
            JSONObject json = successJson();
            json.put("user", user);
            renderJson(response, json);
        }
    }
}
