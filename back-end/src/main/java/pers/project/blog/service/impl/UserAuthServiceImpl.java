package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.constant.RedisConstant;
import pers.project.blog.constant.enumeration.LoginTypeEnum;
import pers.project.blog.constant.enumeration.RoleEnum;
import pers.project.blog.constant.enumeration.UserAreaTypeEnum;
import pers.project.blog.dto.*;
import pers.project.blog.entity.UserAuthEntity;
import pers.project.blog.entity.UserInfoEntity;
import pers.project.blog.entity.UserRoleEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.mapper.UserInfoMapper;
import pers.project.blog.mapper.UserRoleMapper;
import pers.project.blog.service.BlogInfoService;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.strategy.context.SocialLoginContext;
import pers.project.blog.util.*;
import pers.project.blog.vo.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 针对表【tb_user_auth】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-23
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuthEntity> implements UserAuthService {

    private final BlogInfoService blogInfoService;
    private final UserInfoMapper userInfoMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserDetailsService userDetailsService;
    @Autowired
    HttpServletRequest httpServletRequest;

    public UserAuthServiceImpl(BlogInfoService blogInfoService, UserInfoMapper userInfoMapper, UserRoleMapper userRoleMapper, UserAuthMapper userAuthMapper, UserDetailsService userDetailsService) {
        this.blogInfoService = blogInfoService;
        this.userInfoMapper = userInfoMapper;
        this.userRoleMapper = userRoleMapper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAreaDTO> listUserAreas(ConditionVO conditionVO) {
        List<UserAreaDTO> userAreaDTOList = new ArrayList<>();
        switch (Objects.requireNonNull(UserAreaTypeEnum.get(conditionVO.getType()), "类型不存在")) {
            case USER:
                // 查询注册用户区域分布
                Object userArea = RedisUtils.get(RedisConstant.USER_AREA);

                // TODO: 2022/12/29 未检查类型转换
                if (Objects.nonNull(userArea)) {
                    userAreaDTOList = ConversionUtils.parseJson(userArea.toString(), List.class);
                }
                return userAreaDTOList;
            case VISITOR:
                // 查询游客区域分布
                userAreaDTOList = RedisUtils.hGetAll(RedisConstant.VISITOR_AREA)
                        .entrySet()
                        .stream()
                        .map(entry -> UserAreaDTO.builder()
                                .name(entry.getKey().toString())
                                .value(Long.valueOf(entry.getValue().toString()))
                                .build())
                        .collect(Collectors.toList());
                return userAreaDTOList;
            default:
                throw new IllegalArgumentException("Unexpected value: " + conditionVO.getType());
        }
    }

    @Override
    public PageDTO<AdminUserDTO> listBackgroundUserDTOs(ConditionVO conditionVO) {
        // 查询后台用户总数
        Integer count = baseMapper.countBackgroundUsers(conditionVO);
        if (count == 0) {
            return new PageDTO<>();
        }

        // 查询分页的后台用户列表
        IPage<?> page = PaginationUtils.getPage();
        List<AdminUserDTO> adminUserDTOList
                = baseMapper.listBackgroundUserDTOs(page.offset(), page.getSize(), conditionVO);
        return PageDTO.of(adminUserDTOList, count);
    }

    // TODO: 2023/1/6 或许可以从 SecurityUtils 获取密码，可能需要登出逻辑
    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        // 验证旧密码是否正确
        Integer userAuthId = SecurityUtils.getUserDetails().getId();
        String hashedPassword = Objects.requireNonNull
                (getById(userAuthId), "后台用户信息不存在").getPassword();
        if (!BCrypt.checkpw(passwordVO.getOldPassword(), hashedPassword)) {
            throw new ServiceException("旧密码不正确");
        }

        // 更新密码
        String newHashedPassword = BCrypt.hashpw
                (passwordVO.getNewPassword(), BCrypt.gensalt());
        lambdaUpdate()
                .eq(UserAuthEntity::getId, userAuthId)
                .set(UserAuthEntity::getPassword, newHashedPassword);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void register(UserVO userVO) {
        // 验证账号是否合法
        if (checkUser(userVO)) {
            throw new ServiceException("邮箱已被注册");
        }

        // TODO: 2023/1/14 用户名当邮箱 魔法值
        // 新增用户信息
        UserInfoEntity userInfoEntity = UserInfoEntity.builder()
                .email(userVO.getUsername())
                .nickname("用户" + IdWorker.getIdStr())
                .avatar(blogInfoService.getWebSiteConfig().getUserAvatar())
                .build();
        userInfoMapper.insert(userInfoEntity);

        // 绑定用户角色
        UserRoleEntity userRoleEntity = UserRoleEntity.builder()
                .userId(userInfoEntity.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRoleEntity);

        // 新增用户账号
        UserAuthEntity userAuthEntity = UserAuthEntity.builder()
                .userInfoId(userInfoEntity.getId())
                .username(userVO.getUsername())
                .password(BCrypt.hashpw(userVO.getPassword(), BCrypt.gensalt()))
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        save(userAuthEntity);
    }

    @Override
    public void sendCode(String username) {
        // 验证账号是否合法
        if (!SecurityUtils.checkEmail(username)) {
            throw new ServiceException("请输入正确的邮箱");
        }

        // 生成随机验证码发送
        String randomCode = SecurityUtils.getRandomCode();

        // 发送验证码
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("验证码")
                .content("您的验证码为 " + randomCode + "，有效期 15 分钟。")
                .build();
        RabbitUtils.sendEmail(emailDTO);

        // 将验证码存入 Redis，设置过期时间为15分钟
        RedisUtils.setEx(RedisConstant.USER_CODE_KEY, randomCode, RedisConstant.CODE_EXPIRE_TIME);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public UserInfoDTO qqLogin(QQLoginVO qqLoginVO) {
        return SocialLoginContext.executeStrategy
                (ConversionUtils.getJson(qqLoginVO), LoginTypeEnum.QQ);
    }

    @Override
    public UserAuthEntity getUserAuth(SocialTokenDTO socialToken) {
        // TODO: 2023/1/15 username ? OpenId ?
        return lambdaQuery()
                .eq(UserAuthEntity::getUsername, socialToken.getOpenId())
                .eq(UserAuthEntity::getLoginType, socialToken.getLoginType())
                .one();
    }

    @Override
    public UserDetailsDTO getUserDetails(UserAuthEntity userAuth, String ipAddress, String ipSource) {
        // 更新用户信息
        lambdaUpdate()
                .set(UserAuthEntity::getLastLoginTime, TimeUtils.now())
                .set(UserAuthEntity::getIpAddress, ipAddress)
                .set(UserAuthEntity::getIpSource, ipSource)
                .eq(UserAuthEntity::getId, userAuth.getId())
                .update();

        // TODO: 2023/1/15 冗余调用 IpUtils
        return UserDetailsServiceImpl.createUserDetails(userAuth, httpServletRequest);
    }

    @Override
    public UserDetailsDTO saveUserDetails(SocialTokenDTO socialToken, String ipAddress, String ipSource) {
        // 获取第三方用户信息
        SocialUserInfoDTO socialUserInfo = socialToken.getSocialUserInfo();

        // 保存用户信息
        UserInfoEntity userInfoEntity = UserInfoEntity.builder()
                .nickname(socialUserInfo.getNickname())
                .avatar(socialUserInfo.getAvatar())
                .build();
        userInfoMapper.insert(userInfoEntity);

        // 保存账号信息
        UserAuthEntity userAuthEntity = UserAuthEntity.builder()
                .userInfoId(userInfoEntity.getId())
                .username(socialToken.getOpenId())
                .password(socialToken.getAccessToken())
                .loginType(socialToken.getLoginType())
                .lastLoginTime(TimeUtils.now())
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .build();
        save(userAuthEntity);

        // 绑定角色
        UserRoleEntity userRoleEntity = UserRoleEntity.builder()
                .userId(userInfoEntity.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRoleEntity);

        return UserDetailsServiceImpl.createUserDetails(userAuthEntity, httpServletRequest);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public UserInfoDTO weiboLogin(WeiboLoginVO weiboLoginVO) {
        return SocialLoginContext.executeStrategy(ConversionUtils.getJson(weiboLoginVO), LoginTypeEnum.WEIBO);
    }

    @Override
    public void updatePassword(UserVO userVO) {
        // 校验账号是否合法
        if (!checkUser(userVO)) {
            throw new ServiceException("邮箱尚未注册");
        }

        // 根据用户名修改密码
        lambdaUpdate()
                .set(UserAuthEntity::getPassword,
                        BCrypt.hashpw(userVO.getPassword(), BCrypt.gensalt()))
                .eq(UserAuthEntity::getUsername, userVO.getUsername())
                .update();
    }

    /**
     * 校验用户数据是否合法
     *
     * @param userVO 用户数据
     * @return 如果合法，true
     */
    private boolean checkUser(UserVO userVO) {
        // TODO: 2023/1/14 验证码不知道哪来的
        if (!userVO.getCode().equals
                (RedisUtils.get(RedisConstant.USER_CODE_KEY + userVO.getUsername()))) {
            return false;
        }

        // 查询用户名是否存在
        return lambdaQuery()
                .select(UserAuthEntity::getUsername)
                .eq(UserAuthEntity::getUsername, userVO.getUsername())
                .exists();
    }

}




