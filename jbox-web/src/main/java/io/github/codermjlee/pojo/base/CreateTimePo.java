package io.github.codermjlee.pojo.base;

import io.github.codermjlee.pojo.IdObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author MJ
 */
@Getter
@Setter
public abstract class CreateTimePo implements IdObject {
    /**
     * 创建时间
     */
    private Date createTime;
}
