package io.github.codermjlee.pojo.po;


import io.github.codermjlee.pojo.base.MarkPo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Kvs extends MarkPo {
    /**
     * id
     */
    private Integer id;
    private String k;
    private String v;
}
