package com.wang.model;

import com.wang.model.entity.Role;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * TODO：
 * @author Wang926454
 * @date 2018/8/30 10:47
 */
@Table(name = "role")
public class RoleDto extends Role implements Serializable {

    private static final long serialVersionUID = 3579229511983020043L;

}