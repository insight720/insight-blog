package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.constant.DirectoryUriConstant;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.UserDetailsDTO;
import pers.project.blog.dto.UserOnlineDTO;
import pers.project.blog.entity.UserInfoEntity;
import pers.project.blog.entity.UserRoleEntity;
import pers.project.blog.mapper.UserInfoMapper;
import pers.project.blog.service.UserInfoService;
import pers.project.blog.service.UserRoleService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.UserDisableVO;
import pers.project.blog.vo.UserInfoVO;
import pers.project.blog.vo.UserRoleVO;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对表【tb_user_info】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-25
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoEntity> implements UserInfoService {


    private final UserRoleService userRoleService;

    private final SessionRegistry sessionRegistry;

    public UserInfoServiceImpl(UserRoleService userRoleService,
                               SessionRegistry sessionRegistry) {
        this.userRoleService = userRoleService;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void updateUserDisable(UserDisableVO userDisableVO) {
        lambdaUpdate()
                .eq(UserInfoEntity::getId, userDisableVO.getId())
                .set(UserInfoEntity::getIsDisable, userDisableVO.getIsDisable())
                .update();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateUserRole(UserRoleVO userRoleVO) {
        // 更新用户昵称
        Integer userInfoId = userRoleVO.getUserInfoId();
        lambdaUpdate().eq(UserInfoEntity::getId, userInfoId)
                .set(UserInfoEntity::getNickname, userRoleVO.getNickname()).update();

        // 重新建立用户与角色的映射
        userRoleService.lambdaUpdate()
                .eq(UserRoleEntity::getUserId, userInfoId)
                .remove();
        List<UserRoleEntity> userRoleEntityList = userRoleVO.getRoleIdList()
                .stream()
                .map(roleId -> UserRoleEntity.builder()
                        .userId(userInfoId)
                        .roleId(roleId)
                        .build())
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoleEntityList);
    }

    @Override
    public PageDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO) {
        // 在线并且符合查询条件
        List<UserOnlineDTO> userOnlineDTOList = sessionRegistry.getAllPrincipals()
                .stream()
                .filter(principal -> sessionRegistry.getAllSessions
                        (principal, false).size() > 0)
                .map(principal -> ConversionUtils.convertObject(principal, UserOnlineDTO.class))
                .filter(userOnlineDTO -> StringUtils.isBlank(conditionVO.getKeywords()) ||
                        userOnlineDTO.getNickname().contains(conditionVO.getKeywords()))
                .sorted(Comparator.comparing(UserOnlineDTO::getLastLoginTime).reversed())
                .collect(Collectors.toList());

        // 处理分页数据
        IPage<?> page = PaginationUtils.getPage();
        int fromIndex = (int) page.offset();
        int pageSize = (int) page.getSize();
        int total = userOnlineDTOList.size();
        int toIndex = Math.min(total, (fromIndex + 1) + pageSize);
        return PageDTO.of(userOnlineDTOList.subList(fromIndex, toIndex), total);
    }

    @Override
    public void removeOnlineUser(Integer userInfoId) {
        // 获取用户的所有会话并注销
        sessionRegistry.getAllPrincipals()
                .stream()
                .filter(principal -> {
                    UserDetailsDTO userDetailsDTO = (UserDetailsDTO) principal;
                    return userDetailsDTO.getUserInfoId().equals(userInfoId);
                })
                .map(principal -> sessionRegistry.getAllSessions(principal, false))
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .forEach(SessionInformation::expireNow);
    }

    @Override
    public String updateUserAvatar(MultipartFile multipartFile) {
        String avatarUrl = UploadContext.executeStrategy
                (multipartFile, DirectoryUriConstant.AVATAR);
        lambdaUpdate()
                .eq(UserInfoEntity::getId, SecurityUtils.getUserDetails().getUserInfoId())
                .set(UserInfoEntity::getAvatar, avatarUrl)
                .update();
        return avatarUrl;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateUserInfo(UserInfoVO userInfoVO) {
        Integer userInfoId = SecurityUtils.getUserDetails().getUserInfoId();
        UserInfoEntity userInfo = UserInfoEntity.builder()
                .id(userInfoId)
                .nickname(userInfoVO.getNickname())
                .intro(userInfoVO.getIntro())
                .webSite(userInfoVO.getWebSite())
                .build();
        updateById(userInfo);
    }

}




