package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminUserDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.UserAreaDTO;
import pers.project.blog.entity.UserAuthEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.PasswordVO;

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

}
