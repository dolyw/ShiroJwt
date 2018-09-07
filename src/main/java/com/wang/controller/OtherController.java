package com.wang.controller;

import com.wang.exception.UnauthorizedException;
import com.wang.model.common.ResponseBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Wang926454
 * @date 2018/9/3 11:12
 */
@RestController
public class OtherController {
    /**
     * 401没有权限异常
     * @param
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:18
     */
    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseBean unauthorized(HttpServletRequest request) {
        throw new UnauthorizedException(request.getAttribute("msg").toString());
        // return new ResponseBean(401, "Unauthorized", null);
    }
}
