package com.confidant.controllers.member;

import com.alibaba.fastjson.JSONObject;
import com.confidant.common.BaseController;
import com.confidant.common.Constants;
import com.confidant.entity.Member;
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
@Controller("memberrController")
@RequestMapping("/member")
public class MemberController extends BaseController {

    private static String BATH_PATH = "member/";

    @Autowired
    private MemberService<Member> memberService;

    @RequestMapping("/save")
    public void saveUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
//        String nick_name = request.getParameter("nick_name");
//        String sex = request.getParameter("sex");
        if (chekValuesEmpty(new String[]{account, password}))
            fail(response, Constants.ErrorMsg.Common.IllegalArgument);
        else {
            Member member = new Member();
            member.setAccount(account);
            Member check_member = memberService.getMemberByAccount(account);
            if (check_member != null)
                fail(response, Constants.ErrorMsg.User.UserExists);
            else {
                member.setPassword(password);
                String nick_name = account.indexOf("@") > 0 ? account.substring(0, account.indexOf("@")) : account;
                member.setNick_name(nick_name);
//                member.setSex(sex);
                Date now = new Date();
                member.setCreate_time(now);
                member.setLast_updated(now);
                memberService.insert(member);
                renderJson(response, member);
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
            Member member = memberService.getMemberByAccount(account);
            result.put("exists", member == null ? 0 : 1);
            renderJson(response, result);
        }
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute(Constants.Keys.Session.KeyUser) != null)
            redirect(response, "/");
        else
            return BATH_PATH + "login";
        return null;
//        String account = request.getParameter("account");
//        String password = request.getParameter("password");
//        if (chekValuesEmpty(new String[]{account, password}))
//            fail(response, Constants.ErrorMsg.Common.IllegalArgument);
//        else {
//            Member param = new Member();
//            param.setAccount(account);
//            param.setPassword(password);
//            Member member = memberService.getMemberByAccountPassword(param);
//            if (member == null)
//                fail(response);
//            else {
//                request.getSession().setAttribute(Constants.Keys.Session.KeyUser, member);
//                JSONObject json = successJson();
//                json.put("member", member);
//                renderJson(response, json);
//            }
//        }
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
