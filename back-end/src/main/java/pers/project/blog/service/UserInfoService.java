package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.UserOnlineDTO;
import pers.project.blog.entity.UserInfoEntity;
import pers.project.blog.vo.*;

/**
 * 针对表【tb_user_info】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-25
 */
public interface UserInfoService extends IService<UserInfoEntity> {

    /**
     * 修改用户禁用状态
     *
     * @param userDisableVO 用户禁用信息
     */
    void updateUserDisable(UserDisableVO userDisableVO);

    /**
     * 修改用户权限角色
     *
     * @param userRoleVO 用户权限角色信息
     */
    void updateUserRole(UserRoleVO userRoleVO);

    /**
     * 查看在线用户列表
     *
     * @param conditionVO 查询条件
     * @return 分页的在线用户列表
     */
    PageDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO);

    /**
     * 下线用户
     *
     * @param userInfoId 用户信息 ID
     */
    void removeOnlineUser(Integer userInfoId);

    /**
     * 更新用户头像
     *
     * @param multipartFile 头像图片
     * @return 头像 URL
     */
    String updateUserAvatar(MultipartFile multipartFile);

    /**
     * 修改用户信息
     *
     * @param userInfoVO 用户信息
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 绑定用户邮箱
     *
     * @param emailVO 邮箱
     */
    void saveUserEmail(EmailVO emailVO);

}
