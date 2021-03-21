package com.entity;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.relation.Role;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: wangsaichao
 * @date: 2018/5/11
 * @description: 用户信息
 */
@Entity
public class User implements Serializable{

    /**
     * 用户id(主键 自增)
     */
    private Integer id;

    /**
     * 用户名
     */
    private String BM;

    /**
     * 登录密码
     */
    private String MC;

    /**
     * 用户真实姓名
     */
    private String username;

    /**
     * 身份证号
     */
    private String password;

    /**
     * 用户状态：0:正常状态,1：用户被锁定
     */
    private String loginlock;

    /**
     * 用户状态：0:正常状态,1：用户被锁定
     */
    private String DH;


    /**
     * 用户状态：0:正常状态,1：用户被锁定
     */
    private String grouping_id;/**
     * 用户状态：0:正常状态,1：用户被锁定
     */
    private String grouping_name;/**
     * 用户状态：0:正常状态,1：用户被锁定
     */
    private String operation_pwd;/**
     * 用户状态：0:正常状态,1：用户被锁定
     */
    private String user_img;

    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBM() {
        return BM;
    }

    public void setBM(String BM) {
        this.BM = BM;
    }

    public String getMC() {
        return MC;
    }

    public void setMC(String MC) {
        this.MC = MC;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginlock() {
        return loginlock;
    }

    public void setLoginlock(String loginlock) {
        this.loginlock = loginlock;
    }

    public String getDH() {
        return DH;
    }

    public void setDH(String DH) {
        this.DH = DH;
    }

    public String getGrouping_id() {
        return grouping_id;
    }

    public void setGrouping_id(String grouping_id) {
        this.grouping_id = grouping_id;
    }

    public String getGrouping_name() {
        return grouping_name;
    }

    public void setGrouping_name(String grouping_name) {
        this.grouping_name = grouping_name;
    }

    public String getOperation_pwd() {
        return operation_pwd;
    }

    public void setOperation_pwd(String operation_pwd) {
        this.operation_pwd = operation_pwd;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }
}
