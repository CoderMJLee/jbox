package io.github.codermjlee.pojo.base;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author MJ
 */
@Getter
@Setter
public class StatusPo extends CreateTimePo {
    /**
     * 状态【0:正常，1:禁用】
     */
    protected Byte status;

    public boolean isStatusNormal() {
        return status == null || status == Status.NORMAL;
    }

    public boolean isStatusDisable() {
        return status != null && status != Status.NORMAL;
    }

    public interface Status {
        byte NORMAL = 0;
        byte DISABLED = 1;

        byte MIN = NORMAL;
        byte MAX = DISABLED;

        Map<Byte, String> MAP = new LinkedHashMap<Byte, String>(){{
            put(NORMAL, "正常");
            put(DISABLED, "禁用");
        }};
    }
}
