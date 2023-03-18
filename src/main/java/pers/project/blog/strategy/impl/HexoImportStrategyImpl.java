package pers.project.blog.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.enums.ArticelStateEnum;
import pers.project.blog.enums.ArticleTypeEnum;
import pers.project.blog.service.ArticleService;
import pers.project.blog.strategy.AbstractImportStrategy;
import pers.project.blog.util.FileIoUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.util.TimeUtils;
import pers.project.blog.vo.article.ArticleVO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pers.project.blog.constant.GenericConst.EMPTY_STR;
import static pers.project.blog.constant.GenericConst.ZERO;
import static pers.project.blog.constant.HexoConst.*;
import static pers.project.blog.constant.ImportConst.HEXO;

/**
 * Hexo 文章导入策略
 *
 * @author Luo Fei
 * @version 2023/1/8
 */
@Component(HEXO)
public class HexoImportStrategyImpl extends AbstractImportStrategy {

    @Autowired
    public HexoImportStrategyImpl(ArticleService articleService) {
        super(articleService);
    }

    @Override
    public ArticleVO getArticleVO(MultipartFile multipartFile) throws IOException {
        List<String> lineList = FileIoUtils.readLines(multipartFile.getInputStream());
        return parseLines(lineList);
    }

    /**
     * 解析 Hexo 文章行内容
     * <p>
     * <b>
     * 注意：行内容必须具有正确的结构，否则结果未知
     * </b>
     * <p>
     * Hexo 文章结构<br>
     * --- （第一个分隔符）<br>
     * title: 标题内容<br>
     * date: 日期时间（yyyy-MM-dd HH:mm:ss 格式）<br>
     * categories:<br>
     * -目录名<br>
     * tags:<br>
     * -标签名<br>
     * -标签名<br>
     * -标签名<br>
     * --- （第二个分隔符）<br>
     * 正文<br>
     *
     * @param lineList 行数据列表
     * @return 解析后的文章数据
     */
    private ArticleVO parseLines(List<String> lineList) {
        String articleTitle = EMPTY_STR, categoryName = EMPTY_STR;
        LocalDateTime createTime = TimeUtils.now();
        List<String> tagNameList = new ArrayList<>();
        StringBuilder articleContent = new StringBuilder();
        Integer delimiterCount = ZERO;
        Integer nameTypeFlag = NORMAL_FLAG;
        // 按行拼接文章内容
        for (String line : lineList) {
            // 计数分隔符，分隔符结束就是正文
            if (delimiterCount.equals(MAX_DELIMITER_COUNT)) {
                articleContent.append(line).append(LINE_FEED);
                continue;
            } else if (line.equals(DELIMITER)) {
                delimiterCount++;
                continue;
            }
            // 读取标题内容
            if (line.startsWith(TITLE_PREFIX)) {
                articleTitle = line.replace(TITLE_PREFIX, EMPTY_STR).trim();
                continue;
            }
            // 读取创建时间
            if (line.startsWith(DATE_PREFIX)) {
                createTime = TimeUtils.parse(line.replace(DATE_PREFIX, EMPTY_STR).trim());
                continue;
            }
            // 目录或标签内容的开始标记
            if (line.startsWith(CATEGORIES_PREFIX)) {
                nameTypeFlag = CATEGORY_FLAG;
                continue;
            } else if (line.startsWith(TAGS_PREFIX)) {
                nameTypeFlag = TAG_FLAG;
                continue;
            }
            // 读取目录或标签内容
            if (line.startsWith(CATEGORY_OR_TAG_NAME_PREFIX)) {
                if (nameTypeFlag.equals(CATEGORY_FLAG)) {
                    categoryName = line.replace(CATEGORY_OR_TAG_NAME_PREFIX, EMPTY_STR).trim();
                } else if (nameTypeFlag.equals(TAG_FLAG)) {
                    tagNameList.add(line.replace(CATEGORY_OR_TAG_NAME_PREFIX, EMPTY_STR).trim());
                }
            }
        }
        // 默认公开文章，但如果分类或标签为空，则设为草稿（草稿不保存分类）
        Integer articleStatus = ArticelStateEnum.PUBLIC.getStatus();
        if (StrRegexUtils.isBlank(categoryName) || CollectionUtils.isEmpty(tagNameList)) {
            articleStatus = ArticelStateEnum.DRAFT.getStatus();
        }
        // 默认为原创文章
        Integer articleType = ArticleTypeEnum.ORIGINAL.getType();
        return ArticleVO.builder()
                .articleTitle(articleTitle)
                .articleContent(articleContent.toString())
                .categoryName(categoryName)
                .tagNameList(tagNameList)
                .type(articleType)
                .status(articleStatus)
                .createTime(createTime).
                build();
    }

}
