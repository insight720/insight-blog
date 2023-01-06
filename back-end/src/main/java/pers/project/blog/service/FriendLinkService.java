package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminFriendLinkDTO;
import pers.project.blog.dto.FriendLinkDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.FriendLinkEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.FriendLinkVO;

import java.util.List;

/**
 * 针对表【tb_friend_link】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-06
 */
public interface FriendLinkService extends IService<FriendLinkEntity> {

    /**
     * 查看友链列表
     *
     * @return 友链列表
     */
    List<FriendLinkDTO> listFriendLinks();


    /**
     * 查看分页的后台友链列表
     *
     * @param conditionVO 查询条件
     * @return 分页的后台友链列表
     */
    PageDTO<AdminFriendLinkDTO> listAdminFriendLinks(ConditionVO conditionVO);

    /**
     * 保存或更新友链
     *
     * @param friendLinkVO 友链信息
     */
    void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO);

}
