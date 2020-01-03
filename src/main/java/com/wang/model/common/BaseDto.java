package com.wang.model.common;

import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页排序通用Dto
 * @author dolyw.com
 * @date 2018/9/10 10:10
 */
public class BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页数 */
    @Transient
    @Min(value = 1, message = "当前页数不能小于1")
    private Integer page;

    /** 每页条数 */
    @Transient
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 50, message = "每页条数不能大于50")
    private Integer rows;

    /** 排序的列名 */
    @Transient
    private String sidx;

    /** 排序规则(DESC或者ESC) */
    @Transient
    private String sord;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getSidx() {
        return sidx;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public String getSord() {
        return sord;
    }

    public void setSord(String sord) {
        this.sord = sord;
    }
}
