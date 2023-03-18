package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.friendlink.AdminFriendLinkDTO;
import pers.project.blog.dto.friendlink.FriendLinkDTO;
import pers.project.blog.entity.FriendLink;
import pers.project.blog.vo.friendlink.FriendLinkVO;

import java.util.List;

/**
 * 针对表【tb_friend_link】的数据库操作 Service
 *
 * @author Luo Fei
 * @version 2023-01-06
 */
public interface FriendLinkService extends IService<FriendLink> {

    /**
     * 保存或更新友链
     */
    void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO);

    /**
     * 删除链接
     */
    void removeFriendLinks(List<Integer> friendLinkIdList);

    /**
     * 获取前台查看友链所需数据
     */
    List<FriendLinkDTO> listFriendLinks();

    /**
     * 查看分页的后台友链列表
     */
    PageDTO<AdminFriendLinkDTO> listAdminFriendLinks(String keywords);

}
