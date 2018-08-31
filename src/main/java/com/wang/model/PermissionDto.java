package com.wang.model;

import com.wang.model.entity.Permission;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * TODO：
 * @author Wang926454
 * @date 2018/8/30 10:48
 */
@Table(name = "permission")
public class PermissionDto extends Permission implements Serializable {

    private static final long serialVersionUID = 2342125681951782518L;

}