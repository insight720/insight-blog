package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.*;
import pers.project.blog.entity.UserAuthEntity;
import pers.project.blog.vo.*;

import java.util.List;

/**
 * 针对表【tb_user_auth】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-23
 */
public interface UserAuthService extends IService<UserAuthEntity> {

    /**
     * 获取用户属地分布
     *
     * @param conditionVO 查询条件
     * @return 用户属地分布
     */
    List<UserAreaDTO> listUserAreas(ConditionVO conditionVO);

    /**
     * 查询后台用户列表
     *
     * @param conditionVO 查询条件
     * @return 分页的视图对象
     */
    PageDTO<AdminUserDTO> listBackgroundUserDTOs(ConditionVO conditionVO);

    /**
     * 修改管理员密码
     *
     * @param passwordVO 密码信息
     */
    void updateAdminPassword(PasswordVO passwordVO);

    /**
     * 用户注册
     *
     * @param userVO 用户信息
     */
    void register(UserVO userVO);
// TODO: 2023/1/14 邮箱号 ？ 用户名 ？

    /**
     * 发送邮箱验证码
     *
     * @param username 邮箱号
     */
    void sendCode(String username);

    /**
     * QQ 登录
     *
     * @param qqLoginVO QQ 登录信息
     * @return {@code  UserInfoDTO} 用户信息数据
     */
    UserInfoDTO qqLogin(QQLoginVO qqLoginVO);

    /**
     * 获取用户账号
     *
     * @return {@code  UserAuthEntity} 用户账信息
     */
    UserAuthEntity getUserAuth(SocialTokenDTO socialToken);

    /**
     * 获取用户信息
     *
     * @param userAuth  用户账号
     * @param ipAddress IP 地址
     * @param ipSource  IP 源
     * @return {@code  UserDetailsDTO} 用户信息
     */
    UserDetailsDTO getUserDetails(UserAuthEntity userAuth, String ipAddress, String ipSource);

    /**
     * 新增用户信息
     *
     * @param socialToken Token信息
     * @param ipAddress   Ip 地址
     * @param ipSource    Ip 源
     * @return {@code  UserDetailsDTO} 用户信息
     */
    UserDetailsDTO saveUserDetails(SocialTokenDTO socialToken, String ipAddress, String ipSource);

    /**
     * 微博登录
     *
     * @param weiboLoginVO 微博登录信息
     * @return {@code UserInfoDTO}用户登录信息
     */
    UserInfoDTO weiboLogin(WeiboLoginVO weiboLoginVO);

    /**
     * 修改密码
     *
     * @param userVO 用户信息
     */
    void updatePassword(UserVO userVO);

}
