package cn.ittiger.im.util;

import cn.ittiger.im.bean.MemberBean;
import cn.ittiger.im.bean.User;
import cn.ittiger.util.PreferenceHelper;

/**
 * Created by laohu on 16-12-14.
 */
public class LoginHelper {

    private static final String KEY_REMEMBER_PASSWORD = "pre_key_remember_password";
    private static final String KEY_USER = "pre_key_user";
    private static final String KEY_MEMBER = "saveMemberBean";

    /**
     * 是否记住密码
     *
     * @return
     */
    public static boolean isRememberPassword() {

        return PreferenceHelper.getBoolean(KEY_REMEMBER_PASSWORD, false);
    }

    public static void rememberRassword(boolean isRemember) {

        PreferenceHelper.putBoolean(KEY_REMEMBER_PASSWORD, isRemember);
    }

    public static User getUser() {

        User user = (User) PreferenceHelper.get(KEY_USER);
        if(user == null) {
            user = new User("", "");
        }
        return user;
    }

    public static void saveUser(User user) {

        PreferenceHelper.put(KEY_USER, user);
    }

    public static void saveMemberBean(MemberBean bean) {

        PreferenceHelper.put(KEY_MEMBER, bean);
    }

    public static MemberBean getMemberBean() {

        Object o = PreferenceHelper.get(KEY_MEMBER);
        if (o != null) {
            return (MemberBean) o;
        }
        return null;
    }
}