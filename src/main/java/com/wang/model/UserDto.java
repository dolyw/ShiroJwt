package com.wang.model;

import com.wang.model.entity.User;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * TODO：
 * @author Wang926454
 * @date 2018/8/30 10:34
 */
@Table(name = "user")
public class UserDto extends User implements Serializable {

    private static final long serialVersionUID = -3151323560739017920L;

}
