package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.userauth.AreaCountDTO;
import pers.project.blog.dto.userauth.SocialTokenDTO;
import pers.project.blog.dto.userauth.UserDTO;
import pers.project.blog.dto.userauth.UserDetailsDTO;
import pers.project.blog.entity.UserAuth;
import pers.project.blog.security.BlogUserDetails;
import pers.project.blog.vo.userauth.*;

import java.util.List;

/**
 * 针对表【tb_user_auth】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-23
 */
public interface UserAuthService extends IService<UserAuth> {

    /**
     * 获取用户地域分布数据
     */
    List<AreaCountDTO> listUserAreas(Integer userType);

    /**
     * 获取分页的用户列表数据
     */
    PageDTO<UserDTO> listUsers(UserSearchVO userSearchVO);

    /**
     * 后台修改密码
     */
    void updateAdminPassword(PasswordVO passwordVO);

    /**
     * 用户注册
     */
    void register(UserAuthVO userAuthVO);

    /**
     * 发送验证码
     */
    void sendVerificationCode(String email);

    /**
     * 重置密码
     */
    void resetPassword(UserAuthVO userVO);

    /**
     * QQ 登录
     */
    UserDetailsDTO qqLogin(QQLoginVO qqLoginVO);

    /**
     * 微博登录
     */
    UserDetailsDTO weiboLogin(WeiboLoginVO weiboLoginVO);

    /**
     * 获取社交登录用户认证信息
     */
    UserAuth getSocialUserAuth(SocialTokenDTO socialToken);

    /**
     * 获取社交登录的 Spring Security 用户信息
     */
    BlogUserDetails getSocialUserDetails(UserAuth userAuth);

    /**
     * 创建社交登录的 Spring Security 用户信息
     */
    BlogUserDetails createSocialUserDetails(SocialTokenDTO socialToken);

}
