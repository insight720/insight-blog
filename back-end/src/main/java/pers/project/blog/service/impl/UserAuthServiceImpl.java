package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import pers.project.blog.constant.RedisConstant;
import pers.project.blog.constant.enumeration.UserAreaTypeEnum;
import pers.project.blog.dto.AdminUserDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.UserAreaDTO;
import pers.project.blog.entity.UserAuthEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.PasswordVO;

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

}




