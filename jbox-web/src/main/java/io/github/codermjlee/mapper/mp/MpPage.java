package io.github.codermjlee.mapper.mp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.codermjlee.pojo.vo.req.page.PageQVo;
import io.github.codermjlee.pojo.vo.resp.page.PageListPVo;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class MpPage<T> extends Page<T> {
    private static final int PAGE_SIZE = 10;

    public MpPage(PageQVo qVo) {
        super(qVo.getPageNo(), Math.max(qVo.getPageSize(), PAGE_SIZE));
    }

    private <N> PageListPVo<N> commonBuildPVos(List<N> data) {
        PageListPVo<N> pVo = new PageListPVo<>();
        pVo.setPageNo(getCurrent());
        pVo.setPageSize(getSize());
        pVo.setTotal(getTotal());
        pVo.setPages(getPages());
        pVo.setData(data);
        return pVo;
    }

    public PageListPVo<T> buildPVos() {
        return commonBuildPVos(getRecords());
    }

    public <R> PageListPVo<R> buildPVos(Function<List<T>, List<R>> function) {
        if (function == null) return (PageListPVo<R>) buildPVos();
        return commonBuildPVos(function.apply(getRecords()));
    }
}
