package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.project.blog.entity.Page;
import pers.project.blog.mapper.PageMapper;
import pers.project.blog.service.PageService;
import pers.project.blog.util.BeanCopierUtils;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.vo.page.PageVO;

import java.util.List;

import static pers.project.blog.constant.CacheConst.PAGE;

/**
 * 针对表【tb_page（页面）】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-06
 */
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, Page> implements PageService {

    @Override
    @Cacheable(cacheNames = PAGE, key = "#root.methodName", sync = true)
    public List<PageVO> listPages() {
        return ConvertUtils.convertList(list(), PageVO.class);
    }

    @Override
    @CacheEvict(cacheNames = PAGE, allEntries = true)
    public void saveOrUpdatePage(PageVO pageVO) {
        saveOrUpdate(BeanCopierUtils.copy(pageVO, Page.class));
    }

    @Override
    @CacheEvict(cacheNames = PAGE, allEntries = true)
    public void removePage(Integer pageId) {
        removeById(pageId);
    }

}




