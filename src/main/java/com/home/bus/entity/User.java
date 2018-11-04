package com.home.bus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.home.bus.model.ISysPermission;
import com.home.bus.model.IUserRole;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: xu.dm
 * @Date: 2018/6/10 17:14
 * @Description:
 */

@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 5887833463461262508L;
    @Id
    @GenericGenerator(name="generator",strategy = "native")
    @GeneratedValue(generator = "generator")
    private Integer userId;
    @Column(nullable = false, unique = true,length = 60)
    private String userName; //登录用户名，账号
    @Column(nullable = false)
    private String name;//名称（昵称或者真实姓名，根据实际情况定义）
    @Column(nullable = false)
    private String password;
    private String salt;//加密密码的盐
    @Transient
    private String credentialsSalt;
    private byte state;//用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.
//    @ManyToMany(fetch= FetchType.EAGER)//立即从数据库中进行加载数据;
//    @JoinTable(name = "SysUserRole", joinColumns = { @JoinColumn(name = "userId") }, inverseJoinColumns ={@JoinColumn(name = "roleId") })
    @Transient
    private List<IUserRole> roleList;// 一个用户具有多个角色
    @Transient
    private List<ISysPermission> permissionList;//用户的权限
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //格式化前台页面收到的json时间格式，不指定的话会变成缺省的"yyyy-MM-dd'T'HH:mm:ss"
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;//创建时间
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiredDate;//过期日期
    private String email;
    private String tel;

    public User() {
    }

    public List<IUserRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<IUserRole> roleList) {
        this.roleList = roleList;
    }

    public List<ISysPermission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<ISysPermission> permissionList) {
        this.permissionList = permissionList;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    /**
     * 密码盐.
     * @return
     */ //重新对盐重新进行了定义，用户名+salt，这样就更加不容易被破解
    public String getCredentialsSalt(){
        return this.userName+this.salt;
    }

    public void setCredentialsSalt(String credentialsSalt) {
        this.credentialsSalt = credentialsSalt;
    }
}
