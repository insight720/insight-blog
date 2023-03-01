package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.entity.Page;
import pers.project.blog.vo.page.PageVO;

import java.util.List;

/**
 * 针对表【tb_page】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-06
 */
public interface PageService extends IService<Page> {

    /**
     * 获取后台页面管理数据
     */
    List<PageVO> listPages();

    /**
     * 保存或更新页面
     */
    void saveOrUpdatePage(PageVO pageVO);

    /**
     * 删除页面
     */
    void removePage(Integer pageId);

}
