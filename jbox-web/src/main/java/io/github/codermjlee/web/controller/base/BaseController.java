package io.github.codermjlee.web.controller.base;

import io.github.codermjlee.web.msg.Msg;
import io.github.codermjlee.web.msg.MsgPVo;
import io.github.codermjlee.web.msg.Msgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

@Validated
@Slf4j
public abstract class BaseController<Service> {
    @Autowired
    protected Service service;

    protected static <T> MsgPVo<T> process(Supplier<T> function, String error) {
        return process(function, Msgs.error(Msgs.SC_400, error));
    }

    protected static <T> MsgPVo<T> process(Supplier<T> function) {
        return process(function, Msgs.OPERATE_ERROR);
    }

    protected static <T> MsgPVo<T> process(Supplier<T> function,
                                      Msg ce) {
        try {
            T result = function.get();
            // 如果返回的是Boolean，判断执行成功还是失败
            if (result instanceof Boolean) {
                if ((Boolean) result) {
                    return new MsgPVo<>();
                }
            } else { // 如果不是Boolean
                return new MsgPVo<>(result);
            }
        } catch (Exception e) {
            if (e instanceof Msg) {
                throw (Msg) e;
            } else {
                log.error(null, e);
            }
        }
        return ce.raise();
    }

    @FunctionalInterface
    public interface Supplier<T> {
        T get() throws Exception;
    }
}
