package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.constant.RedisConst;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.userinfo.OnlineUserDTO;
import pers.project.blog.entity.UserAuth;
import pers.project.blog.entity.UserInfo;
import pers.project.blog.entity.UserRole;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.mapper.UserInfoMapper;
import pers.project.blog.security.BlogUserDetails;
import pers.project.blog.service.UserInfoService;
import pers.project.blog.service.UserRoleService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.util.PageUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.userinfo.EmailVO;
import pers.project.blog.vo.userinfo.UserDisableVO;
import pers.project.blog.vo.userinfo.UserInfoVO;
import pers.project.blog.vo.userinfo.UserRoleVO;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pers.project.blog.constant.FilePathConst.AVATAR_DIR;
import static pers.project.blog.constant.RedisConst.CODE_PREFIX;

/**
 * 针对表【tb_user_info】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-25
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private UserAuthMapper userAuthMapper;

    @Override
    public void updateUserDisable(UserDisableVO userDisableVO) {
        lambdaUpdate()
                .eq(UserInfo::getId, userDisableVO.getId())
                .set(UserInfo::getIsDisable, userDisableVO.getIsDisable())
                .update();
    }

    @Override
    public PageDTO<OnlineUserDTO> listOnlineUsers(String keywords) {
        Set<String> userNameSet = SecurityUtils.getOnlineUsernames();
        IPage<OnlineUserDTO> page = baseMapper.listOnlineUsers
                (keywords, userNameSet, PageUtils.getPage());
        return PageUtils.build(page);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateNicknameOrRole(UserRoleVO userRoleVO) {
        // 更新用户昵称
        Integer userInfoId = userRoleVO.getUserInfoId();
        lambdaUpdate().set(UserInfo::getNickname, userRoleVO.getNickname())
                .eq(UserInfo::getId, userInfoId).update();
        // 重新建立用户与角色的映射
        userRoleService.lambdaUpdate()
                .eq(UserRole::getUserId, userInfoId)
                .remove();
        List<UserRole> userRoleList = userRoleVO.getRoleIdList()
                .stream()
                .map(roleId -> UserRole.builder()
                        .userId(userInfoId)
                        .roleId(roleId)
                        .build())
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoleList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void makeUserOffline(Integer userInfoId) {
        // 获取用户名信息
        LambdaQueryWrapper<UserAuth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAuth::getUserInfoId, userInfoId);
        String username = userAuthMapper.selectOne(queryWrapper).getUsername();
        BlogUserDetails userDetails = new BlogUserDetails();
        userDetails.setUsername(username);
        // 下线该用户的所有会话
        SecurityUtils.getNonExpiredSessions(userDetails)
                .forEach(SessionInformation::expireNow);
        // 删除下线的用户信息，用于会话控制
        RedisUtils.sRem(RedisConst.ONLINE_USERNAME, username);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String uploadUserAvatar(MultipartFile multipartFile) {
        String avatarUrl = UploadContext.executeStrategy
                (multipartFile, AVATAR_DIR);
        lambdaUpdate()
                .eq(UserInfo::getId, SecurityUtils.getUserInfoId())
                .set(UserInfo::getAvatar, avatarUrl)
                .update();
        return avatarUrl;
    }

    @Override
    public void updateUserInfo(UserInfoVO userInfoVO) {
        Integer userInfoId = SecurityUtils.getUserInfoId();
        UserInfo userInfo = UserInfo.builder()
                .id(userInfoId)
                .nickname(userInfoVO.getNickname())
                .intro(userInfoVO.getIntro())
                .webSite(userInfoVO.getWebSite())
                .build();
        updateById(userInfo);
    }

    @Override
    public void saveUserEmail(EmailVO emailVO) {
        Object code = RedisUtils.get(CODE_PREFIX + emailVO.getEmail());
        if (!emailVO.getCode().equals(code)) {
            throw new ServiceException("验证码错误");
        }
        UserInfo userInfo = UserInfo.builder()
                .id(SecurityUtils.getUserInfoId())
                .email(emailVO.getEmail())
                .build();
        updateById(userInfo);
    }

}




