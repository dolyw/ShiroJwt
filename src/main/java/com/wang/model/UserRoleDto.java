package com.wang.model;

import com.wang.model.entity.UserRole;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * TODO：
 * @author Wang926454
 * @date 2018/8/30 10:49
 */
@Table(name = "user_role")
public class UserRoleDto extends UserRole implements Serializable {

    private static final long serialVersionUID = 1588556934729524096L;

}