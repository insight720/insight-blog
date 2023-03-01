package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.dto.comment.EmailDTO;
import pers.project.blog.dto.userauth.*;
import pers.project.blog.entity.UserAuth;
import pers.project.blog.entity.UserInfo;
import pers.project.blog.entity.UserRole;
import pers.project.blog.enums.LoginTypeEnum;
import pers.project.blog.enums.RoleEnum;
import pers.project.blog.enums.UserTypeEnum;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.mapper.UserRoleMapper;
import pers.project.blog.schedule.AreaCountSchedule;
import pers.project.blog.security.BlogUserDetails;
import pers.project.blog.security.BlogUserDetailsService;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.service.UserInfoService;
import pers.project.blog.strategy.context.LoginContext;
import pers.project.blog.util.*;
import pers.project.blog.vo.userauth.*;

import javax.annotation.Resource;
import java.util.List;

import static java.util.concurrent.TimeUnit.MINUTES;
import static pers.project.blog.constant.GenericConst.ZERO_L;
import static pers.project.blog.constant.RedisConst.CODE_EXPIRE_TIME;
import static pers.project.blog.constant.RedisConst.CODE_PREFIX;
import static pers.project.blog.enums.UserTypeEnum.USER;
import static pers.project.blog.enums.UserTypeEnum.VISITOR;

/**
 * 针对表【tb_user_auth】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-23
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements UserAuthService {

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private BlogUserDetailsService userDetailsService;

    @Override
    public List<AreaCountDTO> listUserAreas(Integer userType) {
        UserTypeEnum userTypeEnum = UserTypeEnum.get(userType);
        if (USER.equals(userTypeEnum)) {
            // 查询用户地域分布
            return AreaCountSchedule.getUserAreaCount();
        } else if (VISITOR.equals(userTypeEnum)) {
            // 查询游客地域分布
            return AreaCountSchedule.getVisitorAreaCount();
        } else {
            throw new RuntimeException("用户类型不存在");
        }
    }

    @Override
    public PageDTO<UserDTO> listUsers(UserSearchVO userSearchVO) {
        // 查询后台用户总数
        Long count = baseMapper.countUsers(userSearchVO);
        if (count.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 查询分页的用户列表数据
        List<UserDTO> userDTOList = baseMapper.listUsers
                (PageUtils.offset(), PageUtils.size(), userSearchVO);
        return PageUtils.build(userDTOList, count);
    }

    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        // 验证旧密码是否正确
        Integer userAuthId = SecurityUtils.getInfo(BlogUserDetails::getId);
        UserAuth userAuth = getById(userAuthId);
        if (userAuth == null) {
            throw new ServiceException("用户不存在");
        }
        if (!SecurityUtils.matches
                (passwordVO.getOldPassword(), userAuth.getPassword())) {
            throw new ServiceException("旧密码不正确");
        }
        // 更新密码
        LambdaUpdateWrapper<UserAuth> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAuth::getPassword,
                SecurityUtils.encode(passwordVO.getNewPassword()));
        updateWrapper.eq(UserAuth::getId, userAuthId);
        update(updateWrapper);
        // 下线当前用户
        userInfoService.makeUserOffline(SecurityUtils.getUserInfoId());
    }

    @Override
    public void sendVerificationCode(String email) {
        // 验证邮箱是否合法
        if (!StrRegexUtils.checkEmail(email)) {
            throw new ServiceException("请输入正确的邮箱");
        }
        // 生成随机验证码发送
        String randomCode = SecurityUtils.getRandomCode();
        // 发送验证码
        EmailDTO emailDTO = EmailDTO.builder()
                .email(email)
                .subject("博客验证码")
                .content("您的验证码为 " + randomCode + "，有效期 15 分钟。")
                .build();
        RabbitUtils.sendEmail(emailDTO);
        // 将验证码存入 Redis，设置过期时间为 15 分钟
        String codeKey = CODE_PREFIX + email;
        RedisUtils.setEx(codeKey, randomCode, CODE_EXPIRE_TIME, MINUTES);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void register(UserAuthVO userAuthVO) {
        // 检查用户认证数据
        if (checkUserAuthVO(userAuthVO)) {
            throw new ServiceException("邮箱已被注册");
        }
        // 新增用户信息
        UserInfo userInfo = UserInfo.builder()
                .email(userAuthVO.getUsername())
                .nickname(SecurityUtils.getUniqueName())
                .avatar(ConfigUtils.getCache(WebsiteConfig::getUserAvatar))
                .build();
        userInfoService.save(userInfo);
        // 绑定用户角色
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRole);
        // 新增用户账号
        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                .username(userAuthVO.getUsername())
                .password(SecurityUtils.encode(userAuthVO.getPassword()))
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        save(userAuth);
    }

    @Override
    public void resetPassword(UserAuthVO userAuthVO) {
        // 检查用户认证数据
        if (!checkUserAuthVO(userAuthVO)) {
            throw new ServiceException("邮箱尚未注册");
        }
        // 根据用户名修改密码
        lambdaUpdate()
                .set(UserAuth::getPassword,
                        SecurityUtils.encode(userAuthVO.getPassword()))
                .eq(UserAuth::getUsername, userAuthVO.getUsername())
                .update();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public UserDetailsDTO qqLogin(QQLoginVO qqLoginVO) {
        return LoginContext.executeStrategy
                (ConvertUtils.getJson(qqLoginVO), LoginTypeEnum.QQ);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public UserDetailsDTO weiboLogin(WeiboLoginVO weiboLoginVO) {
        return LoginContext.executeStrategy
                (ConvertUtils.getJson(weiboLoginVO), LoginTypeEnum.WEIBO);
    }

    @Override
    public UserAuth getSocialUserAuth(SocialTokenDTO socialToken) {
        return lambdaQuery()
                // openId 是社交登录账号，社交登录不会直接使用账号
                .eq(UserAuth::getUsername, socialToken.getOpenId())
                .eq(UserAuth::getLoginType, socialToken.getLoginType())
                .one();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public BlogUserDetails getSocialUserDetails(UserAuth userAuth) {
        // 加载 Spring Security 用户信息
        BlogUserDetails userDetails = userDetailsService.loadUserDetails(userAuth);
        // 更新用户认证信息
        lambdaUpdate()
                .set(UserAuth::getIpAddress, userDetails.getIpAddress())
                .set(UserAuth::getIpSource, userDetails.getIpAddress())
                .set(UserAuth::getBrowser, userDetails.getBrowser())
                .set(UserAuth::getOs, userDetails.getOs())
                .set(UserAuth::getLastLoginTime, userDetails.getLastLoginTime())
                .eq(UserAuth::getId, userAuth.getId())
                .update();
        return userDetails;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public BlogUserDetails createSocialUserDetails(SocialTokenDTO socialToken) {
        // 保存第三方用户信息
        SocialUserInfoDTO socialUserInfo = socialToken.getSocialUserInfo();
        UserInfo userInfo = UserInfo.builder()
                .nickname(socialUserInfo.getNickname())
                .avatar(socialUserInfo.getAvatar())
                .build();
        userInfoService.save(userInfo);
        // 绑定用户权限角色
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRole);
        // 保存账号信息
        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                // openId 是社交登录账号，accessToken 是社交登录账号的密码
                // 用户社交登录不会直接使用账号密码
                .username(socialToken.getOpenId())
                .password(socialToken.getAccessToken())
                .loginType(socialToken.getLoginType())
                .build();
        save(userAuth);
        // 同类方法调用，getSocialUserDetails 方法上的 @Transactional 注解会失效
        // 但整个社交登录流程在 Login 方法的事务中，所以事务生效
        return getSocialUserDetails(userAuth);
    }

    /**
     * 检查用户认证数据
     *
     * @param userAuthVO 用户认证数据
     * @return 如果验证码不正确，抛出服务异常；
     * 如果验证码正确，用户名存在返回 true，用户名不存在则返回 false。
     */
    private boolean checkUserAuthVO(UserAuthVO userAuthVO) {
        // 检查验证码
        String username = userAuthVO.getUsername();
        Object code = RedisUtils.get(CODE_PREFIX + username);
        if (!userAuthVO.getCode().equals(code)) {
            throw new ServiceException("验证码错误");
        }
        // 查询用户名是否存在
        return lambdaQuery()
                .select(UserAuth::getUsername)
                .eq(UserAuth::getUsername, username)
                .exists();
    }

}




