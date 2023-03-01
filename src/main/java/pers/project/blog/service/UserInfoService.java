package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.userinfo.OnlineUserDTO;
import pers.project.blog.entity.UserInfo;
import pers.project.blog.vo.userinfo.EmailVO;
import pers.project.blog.vo.userinfo.UserDisableVO;
import pers.project.blog.vo.userinfo.UserInfoVO;
import pers.project.blog.vo.userinfo.UserRoleVO;

/**
 * 针对表【tb_user_info】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-25
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 修改用户禁用状态
     */
    void updateUserDisable(UserDisableVO userDisableVO);

    /**
     * 修改用户昵称或角色
     */
    void updateNicknameOrRole(UserRoleVO userRoleVO);

    /**
     * 获取分页的在线用户数据
     */
    PageDTO<OnlineUserDTO> listOnlineUsers(String keywords);

    /**
     * 下线用户
     */
    void makeUserOffline(Integer userInfoId);

    /**
     * 上传用户头像
     */
    String uploadUserAvatar(MultipartFile multipartFile);

    /**
     * 修改用户信息
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 绑定用户邮箱
     */
    void saveUserEmail(EmailVO emailVO);

}
