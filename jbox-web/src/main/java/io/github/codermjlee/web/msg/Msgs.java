package io.github.codermjlee.web.msg;

import javax.servlet.http.HttpServletResponse;

/**
 * @author MJ
 */
public interface Msgs {
    int SC_200 = HttpServletResponse.SC_OK;
    int SC_400 = HttpServletResponse.SC_BAD_REQUEST;
    int SC_401 = HttpServletResponse.SC_UNAUTHORIZED;
    int SC_403 = HttpServletResponse.SC_FORBIDDEN;
    int SC_404 = HttpServletResponse.SC_NOT_FOUND;
    int SC_500 = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    Msg BAD_REQUEST = error(SC_401, "请求出错");
    Msg UNAUTHORIZED = error(SC_401, SC_401, "未授权");
    Msg FORBIDDEN = error(SC_403, SC_403, "禁止访问");
    Msg NOT_FOUND = error(SC_404, SC_404, "资源不存在");
    Msg INTERNAL_SERVER_ERROR = error(SC_500, SC_500, "服务器内部错误");

    Msg OPERATE_ERROR = error(66640000, "操作失败");
    Msg LIST_ERROR = error(66640001, "查询失败");
    Msg SAVE_ERROR = error(66640002, "添加失败");
    Msg UPDATE_ERROR = error(66640003, "更新失败");
    Msg REMOVE_ERROR = error(66640004, "删除失败");
    Msg WRONG_PARAM = error(66640005, "请求参数不符合要求");
    Msg DATE_FMT_ERROR = error(66640006, "日期格式错误");

    Msg WRONG_ACCOUNT = Msgs.error(SC_401, 66650000, "账号不存在");
    Msg WRONG_PASSWORD = Msgs.error(SC_401, 66650001, "密码错误");
    Msg USER_DISABLE = Msgs.error(SC_401, 66650002, "用户被禁用，请联系管理员");
    Msg WRONG_CAPTCHA = Msgs.error(SC_401, 66650003, "验证码错误");
    Msg NO_AUTHENTICATION = Msgs.error(SC_401, 66650004, "请先登录");
    Msg AUTHENTICATION_ERROR = Msgs.error(SC_401, 66650005, "请重新登录");
    Msg AUTHENTICATION_EXPIRED = Msgs.error(SC_401, 66650006, "因长时间未登录，请重新登录");
    Msg NO_AUTHORIZATION = Msgs.error(SC_403, 66650007, "没有相关的操作权限");
    Msg WRONG_ROLE = Msgs.error(SC_401, 66650008, "角色错误");
    Msg LOGIN_ERROR = Msgs.error(SC_401, 66650009, "登录失败");

    Msg WS_HEART = note(66661000, "心跳包");
    Msg WS_SHOW_TEXT = note(66661001, "显示信息");

    static Msg error(int status, int code, String msg) {
        return new Msg(status, code, msg);
    }

    static Msg error(int code, String msg) {
        return error(SC_401, code, msg);
    }

    static Msg note(int code, String msg) {
        return new Msg(SC_200, code, msg, MsgPVo.MsgType.NOTIFICATION);
    }

    static <T> T raise(String msg) {
        throw error(SC_400, msg);
    }
}
