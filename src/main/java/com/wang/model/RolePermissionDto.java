package com.wang.model;

import com.wang.model.entity.RolePermission;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * TODO：
 * @author Wang926454
 * @date 2018/8/30 10:49
 */
@Table(name = "role_permission")
public class RolePermissionDto extends RolePermission implements Serializable {

    private static final long serialVersionUID = -4521960433140312763L;

}