package pers.project.blog.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import pers.project.blog.constant.enumeration.ResultStatusEnum;


/**
 * 通用的前后端接口返回结果
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    /**
     * 返回状态
     */
    private Boolean flag;

    /**
     * 返回状态码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public static <T, E extends Throwable> Result<T> of(ResultStatusEnum statusEnum, T data, E exception) {
        return new Result<>(ResultStatusEnum.SUCCESS.equals(statusEnum) ? Boolean.TRUE : Boolean.FALSE,
                statusEnum.getCode(), exception == null ? statusEnum.getDescription() : exception.getMessage(), data);
    }

    public static <E extends Throwable> Result<?> of(ResultStatusEnum statusEnum, E exception) {
        return of(statusEnum, null, exception);
    }

    public static <T> Result<T> of(ResultStatusEnum statusEnum, T data) {
        return of(statusEnum, data, null);
    }

    public static Result<?> of(ResultStatusEnum statusEnum) {
        return of(statusEnum, null, null);
    }

    public static <T> Result<T> ok(T data) {
        return of(ResultStatusEnum.SUCCESS, data, null);
    }

    public static Result<?> ok() {
        return of(ResultStatusEnum.SUCCESS, null, null);
    }

    public static <E extends Throwable> Result<?> error(E exception) {
        return of(ResultStatusEnum.FAILURE, null, exception);
    }

}
