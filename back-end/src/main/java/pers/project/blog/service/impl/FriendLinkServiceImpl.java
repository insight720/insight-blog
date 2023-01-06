package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pers.project.blog.dto.AdminFriendLinkDTO;
import pers.project.blog.dto.FriendLinkDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.FriendLinkEntity;
import pers.project.blog.mapper.FriendLinkMapper;
import pers.project.blog.service.FriendLinkService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.FriendLinkVO;

import java.util.List;

/**
 * 针对表【tb_friend_link】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-06
 */
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLinkEntity> implements FriendLinkService {

    @Override
    public List<FriendLinkDTO> listFriendLinks() {
        return ConversionUtils.covertList(list(), FriendLinkDTO.class);
    }

    @Override
    public PageDTO<AdminFriendLinkDTO> listAdminFriendLinks(ConditionVO conditionVO) {
        String keywords = conditionVO.getKeywords();
        IPage<FriendLinkEntity> page = lambdaQuery()
                .like(StringUtils.hasText(keywords),
                        FriendLinkEntity::getLinkName, keywords)
                .page(PaginationUtils.getPage());
        return PageDTO.of(ConversionUtils.covertList
                (page.getRecords(), AdminFriendLinkDTO.class), (int) page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO) {
        saveOrUpdate(ConversionUtils.convertObject(friendLinkVO, FriendLinkEntity.class));
    }

}




