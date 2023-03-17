package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.friendlink.AdminFriendLinkDTO;
import pers.project.blog.dto.friendlink.FriendLinkDTO;
import pers.project.blog.entity.FriendLink;
import pers.project.blog.mapper.FriendLinkMapper;
import pers.project.blog.service.FriendLinkService;
import pers.project.blog.util.BeanCopierUtils;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.PageUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.vo.friendlink.FriendLinkVO;

import java.util.List;

import static pers.project.blog.constant.CacheConst.FRIEND_LINK;

/**
 * 针对表【tb_friend_link】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-06
 */
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {

    @Override
    @CacheEvict(cacheNames = FRIEND_LINK, allEntries = true)
    public void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO) {
        saveOrUpdate(BeanCopierUtils.copy(friendLinkVO, FriendLink.class));
    }

    @Override
    @CacheEvict(cacheNames = FRIEND_LINK, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void removeFriendLinks(List<Integer> friendLinkIdList) {
        removeBatchByIds(friendLinkIdList);
    }

    @Override
    @Cacheable(cacheNames = FRIEND_LINK, key = "#root.methodName", sync = true)
    public List<FriendLinkDTO> listFriendLinks() {
        return ConvertUtils.convertList(list(), FriendLinkDTO.class);
    }

    @Override
    public PageDTO<AdminFriendLinkDTO> listAdminFriendLinks(String keywords) {
        IPage<FriendLink> page = lambdaQuery()
                .like(StrRegexUtils.isNotBlank(keywords),
                        FriendLink::getLinkName, keywords)
                .page(PageUtils.getPage());
        return PageUtils.build(ConvertUtils.convertList
                (page.getRecords(), AdminFriendLinkDTO.class), page.getTotal());
    }

}




