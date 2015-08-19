package com.confidant.controllers.member;

import com.alibaba.fastjson.JSONObject;
import com.confidant.common.BaseController;
import com.confidant.common.Constants;
import com.confidant.entity.Member;
import com.confidant.util.FileUtil;
import com.confidant.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2015/1/29.
 */
@Controller("memberrController")
@RequestMapping("/member")
public class MemberController extends BaseController {

    private static String BATH_PATH = "member/";

    @Autowired
    private MemberService<Member> memberService;

    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute(Constants.Keys.Session.KeyMember) != null)
            redirect(response, "/");
        else
            return BATH_PATH + "login";
        return null;
    }

    @RequestMapping("/login_check")
    public String login_check(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute(Constants.Keys.Session.KeyMember) != null)
            redirect(response, "/");
        else {
            String account = request.getParameter("account");
            String password = request.getParameter("password");
            String error;
            if (chekValuesEmpty(new String[]{account, password}))
                error = Constants.ErrorMsg.Common.IllegalArgument;
            else {
                Member param = new Member();
                param.setAccount(account);
                param.setPassword(password);
                Member member = memberService.getMemberByAccountPassword(param);
                if (member == null)
                    error = Constants.ErrorMsg.Member.LoginFail;
                else {
                    request.getSession().setAttribute(Constants.Keys.Session.KeyMember, member);
                    redirect(response, "/");
                    return null;
                }
            }
            if (error != null) {
                request.setAttribute("account", account);
                request.setAttribute("error", error);
                return login(request, response);
            }
        }
        return null;
    }

    @RequestMapping("/reg")
    public String reg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute(Constants.Keys.Session.KeyMember) != null)
            redirect(response, "/");
        else
            return BATH_PATH + "reg";
        return null;
    }

    @RequestMapping("/save")
    public String saveUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute(Constants.Keys.Session.KeyMember) != null)
            redirect(response, "/");
        else {
            String account = request.getParameter("account");
            String password = request.getParameter("password");
            String error;
            if (chekValuesEmpty(new String[]{account, password}) || !Validator.isEmail(account) || !Validator.isPassword(password))
                error = Constants.ErrorMsg.Common.IllegalArgument;
            else {
                Member member = new Member();
                member.setAccount(account);
                Member check_member = memberService.getMemberByAccount(account);
                if (check_member != null)
                    error = Constants.ErrorMsg.Member.AccountExists;
                else {
                    member.setPassword(password);
                    String nick_name = account.indexOf("@") > 0 ? account.substring(0, account.indexOf("@")) : account;
                    member.setNick_name(nick_name);
                    Date now = new Date();
                    member.setCreate_time(now);
                    member.setLast_updated(now);
                    memberService.insert(member);
                    request.getSession().setAttribute(Constants.Keys.Session.KeyMember, member);
                    redirect(response, "/");
                    return null;
                }
            }
            if (error != null) {
                request.setAttribute("account", account);
                request.setAttribute("error", error);
                return reg(request, response);
            }
        }
        return null;
    }

    @RequestMapping("/checkAccount")
    public void checkAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String account = request.getParameter("account");
        if (chekValuesEmpty(new String[]{account}))
            fail(response, Constants.ErrorMsg.Common.IllegalArgument);
        else {
            JSONObject result = successJson();
            Member member = memberService.getMemberByAccount(account);
            result.put("exists", member == null ? 0 : 1);
            renderJson(response, result);
        }
    }

    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(Constants.Keys.Session.KeyMember, null);
        redirect(response, "/");
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
                Member member = new Member();
                member.setId(userId);
                member.setImage_url(imgPath);
                member.setLast_updated(new Date());
                memberService.updateMember(member);
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
            Member member = new Member();
            member.setId(getCurrentUser(request).getId());
            member.setNick_name(nick_name);
            member.setLast_updated(new Date());
            memberService.updateMember(member);
            JSONObject json = successJson();
            json.put("member", member);
            renderJson(response, json);
        }
    }
}
