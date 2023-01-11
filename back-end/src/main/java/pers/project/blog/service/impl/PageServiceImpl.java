package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.constant.RedisConstant;
import pers.project.blog.entity.PageEntity;
import pers.project.blog.mapper.PageMapper;
import pers.project.blog.service.PageService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.vo.PageVO;

import java.util.List;
import java.util.Optional;

/**
 * 针对表【tb_page(页面)】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-06
 */
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, PageEntity> implements PageService {

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Throwable.class)
    public List<PageVO> listPages() {
        // 从缓存获取页面信息，没有则从数据库查询并更新缓存
        return Optional
                .ofNullable(ConversionUtils.parseJson
                        ((String) RedisUtils.get(RedisConstant.PAGE_COVER), List.class))
                .orElseGet(() -> {
                    List<PageVO> pageVOList
                            = ConversionUtils.covertList
                            (baseMapper.selectList(null), PageVO.class);
                    RedisUtils.set(RedisConstant.PAGE_COVER, ConversionUtils.getJson(pageVOList));
                    return pageVOList;
                });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdatePage(PageVO pageVO) {
        saveOrUpdate(ConversionUtils.convertObject(pageVO, PageEntity.class));
        RedisUtils.del(RedisConstant.PAGE_COVER);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removePage(Integer pageId) {
        removeById(pageId);
        RedisUtils.del(RedisConstant.PAGE_COVER);
    }

}




