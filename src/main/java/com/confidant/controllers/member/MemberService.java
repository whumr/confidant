package com.confidant.controllers.member;

import com.confidant.common.BaseService;
import com.confidant.entity.Member;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2015/1/29.
 */
@Service("memberService")
public class MemberService<E extends Member> extends BaseService<E> {

    static {
        NAMESPACE = "member.";
    }

    /**
     * 验证用户名重复
     * @param account
     * @return
     */
    public Member getMemberByAccount(String account) {
        List<Member> list = baseSqlSession.selectList(NAMESPACE + "getMemberByAccount", account);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 验证用户名密码
     * @param member
     * @return
     */
    public Member getMemberByAccountPassword(Member member) {
        List<Member> list = baseSqlSession.selectList(NAMESPACE + "getMemberByAccountPassword", member);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 更新用户基本资料
     * @param member
     */
    public void updateMember(Member member) {
        baseSqlSession.update(NAMESPACE + "updateMember", member);
    }
}
