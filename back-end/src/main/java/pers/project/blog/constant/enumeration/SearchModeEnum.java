package pers.project.blog.constant.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 搜索模式枚举
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Getter
@AllArgsConstructor
public enum SearchModeEnum {

    /**
     * mysql
     */
    MYSQL("mysql", "mySqlSearchStrategyImpl"),

    /**
     * elasticsearch
     */
    ELASTICSEARCH("elasticsearch", "elasticSearchStrategyImpl");

    /**
     * 模式
     */
    private final String mode;

    /**
     * 策略
     */
    private final String strategy;

    /**
     * 获取策略
     *
     * @param mode 模式
     * @return {@link String} 搜索策略
     */
    public static String getStrategy(String mode) {
        for (SearchModeEnum value : SearchModeEnum.values()) {
            if (value.getMode().equals(mode)) {
                return value.getStrategy();
            }
        }
        return null;
    }

}