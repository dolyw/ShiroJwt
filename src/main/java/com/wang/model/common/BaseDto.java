package com.wang.model.common;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * 所有Dto的基类
 * @author Wang926454
 * @date 2018/9/10 10:10
 */
public class BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页数 */
    @Transient
    private int page;

    /** 每页条数 */
    @Transient
    private int rows;

    /** 排序的列名 */
    @Transient
    private String sidx;

    /** 排序规则(DESC或者ESC) */
    @Transient
    private String sord;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
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
