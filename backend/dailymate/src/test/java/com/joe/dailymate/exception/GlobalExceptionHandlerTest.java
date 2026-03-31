package com.joe.dailymate.exception;

import com.joe.dailymate.dto.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GlobalExceptionHandler 单元测试
 * 直接实例化处理器，验证各异常类型映射到正确的 code
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("401：未登录异常返回 code=401")
    void handleBusinessException_unauthorized() {
        BusinessException ex = BusinessException.unauthorized("未登录");

        Result<Void> result = handler.handleBusinessException(ex);

        assertThat(result.getCode()).isEqualTo(401);
        assertThat(result.getMsg()).isEqualTo("未登录");
    }

    @Test
    @DisplayName("403：越权异常返回 code=403")
    void handleBusinessException_forbidden() {
        BusinessException ex = BusinessException.forbidden("无权访问该账单");

        Result<Void> result = handler.handleBusinessException(ex);

        assertThat(result.getCode()).isEqualTo(403);
        assertThat(result.getMsg()).isEqualTo("无权访问该账单");
    }

    @Test
    @DisplayName("404：资源不存在返回 code=404")
    void handleBusinessException_notFound() {
        BusinessException ex = BusinessException.notFound("账单不存在");

        Result<Void> result = handler.handleBusinessException(ex);

        assertThat(result.getCode()).isEqualTo(404);
        assertThat(result.getMsg()).isEqualTo("账单不存在");
    }

    @Test
    @DisplayName("500：未预期异常返回 code=500，不暴露内部信息")
    void handleException_shouldReturn500AndHideDetail() {
        Exception ex = new RuntimeException("数据库连接超时，密码是 xiang123");

        Result<Void> result = handler.handleException(ex);

        assertThat(result.getCode()).isEqualTo(500);
        // 内部细节不能泄露到响应里
        assertThat(result.getMsg()).doesNotContain("xiang123");
        assertThat(result.getMsg()).doesNotContain("数据库");
    }

    @Test
    @DisplayName("BusinessException 的 data 字段始终为 null")
    void handleBusinessException_dataShouldBeNull() {
        BusinessException ex = BusinessException.notFound("不存在");
        Result<Void> result = handler.handleBusinessException(ex);
        assertThat(result.getData()).isNull();
    }
}
