package io.github.codermjlee.pojo.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;

/**
 * @author MJ
 */
@Getter
@Setter
public abstract class LogicDeletePo extends CreateTimePo {
    /**
     * 是否删除
     */
    @TableLogic
    @TableField(select = false)
    private Byte deleted;

    public interface Deleted {
        byte NO = 0;
        byte YES = 1;
    }
}
