package com.confidant.controllers.user;

import com.confidant.common.BaseService;
import com.confidant.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2015/1/29.
 */
@Service("userService")
public class UserService<E extends User> extends BaseService<E> {

    static {
        NAMESPACE = "user.";
    }

    /**
     * 验证用户名重复
     * @param account
     * @return
     */
    public User getUserByAccount(String account) {
        List<User> list = baseSqlSession.selectList(NAMESPACE + "getUserByAccount", account);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 验证用户名密码
     * @param user
     * @return
     */
    public User getUserByAccountPassword(User user) {
        List<User> list = baseSqlSession.selectList(NAMESPACE + "getUserByAccountPassword", user);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 更新用户基本资料
     * @param user
     */
    public void updateUser(User user) {
        baseSqlSession.update(NAMESPACE + "updateUser", user);
    }
}
