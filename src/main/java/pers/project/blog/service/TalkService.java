package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.talk.AdminTalkDTO;
import pers.project.blog.dto.talk.TalkDTO;
import pers.project.blog.entity.Talk;
import pers.project.blog.vo.talk.TalkVO;

import java.util.List;

/**
 * 针对表【tb_talk】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-11
 */
public interface TalkService extends IService<Talk> {

    /**
     * 保存说说图片
     *
     * @return 图片 URL
     */
    String saveTalkImages(MultipartFile multipartFile);

    /**
     * 保存或修改说说
     */
    void saveOrUpdateTalk(TalkVO talkVO);

    /**
     * 删除说说
     */
    void removeTalks(List<Integer> talkIdList);

    /**
     * 获取分页的后台说说列表数据
     *
     * @param status 说说状态
     */
    PageDTO<AdminTalkDTO> listAdminTalks(Integer status);

    /**
     * 根据 ID 获取后台说说数据
     */
    AdminTalkDTO getAdminTalk(Integer talkId);

    /**
     * 获取首页说说数据列表
     */
    List<String> listHomePageTalks();

    /**
     * 获取首页查看全部说说的数据列表
     */
    PageDTO<TalkDTO> listTalks();

    /**
     * 根据 ID 获取前台说说数据
     */
    TalkDTO getTalk(Integer talkId);

    /**
     * 点赞说说
     */
    void likeTalk(Integer talkId);

}
