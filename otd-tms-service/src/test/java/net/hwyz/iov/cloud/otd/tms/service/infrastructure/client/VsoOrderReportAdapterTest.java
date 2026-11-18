package net.hwyz.iov.cloud.otd.tms.service.infrastructure.client;

import net.hwyz.iov.cloud.framework.common.bean.AjaxResult;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsBusinessException;
import net.hwyz.iov.cloud.otd.vso.api.service.VsoOrderService;
import net.hwyz.iov.cloud.otd.vso.api.vo.request.OrderStateChangeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * VSO 回写适配器测试（联调发现：平台业务失败以 200 + code 表达）
 *
 * @author hwyz_leo
 */
@DisplayName("VSO 回写适配器 - 成功与拒绝识别")
class VsoOrderReportAdapterTest {

    private final VsoOrderService vsoOrderService = mock(VsoOrderService.class);
    private final VsoOrderReportAdapter adapter = new VsoOrderReportAdapter(vsoOrderService);

    @Test
    @DisplayName("VSO 返回成功时视为回写成功")
    void shouldAcceptSuccess() {
        when(vsoOrderService.prepareTransport(any(OrderStateChangeRequest.class)))
                .thenReturn(AjaxResult.success());

        adapter.prepareTransport("SO-1");
    }

    @Test
    @DisplayName("VSO 以 200 + 业务错误码拒绝时抛出 203005")
    void shouldRejectBusinessErrorBody() {
        AjaxResult rejected = AjaxResult.error("201008", "订单当前状态不允许流转为待运输");
        when(vsoOrderService.prepareTransport(any(OrderStateChangeRequest.class))).thenReturn(rejected);

        TmsBusinessException error = org.assertj.core.api.Assertions.catchThrowableOfType(
                () -> adapter.prepareTransport("SO-1"), TmsBusinessException.class);
        assertThat(error.getCode()).isEqualTo("203005");
        assertThat(error.getMessage()).contains("201008");
    }

    @Test
    @DisplayName("Feign 调用异常时抛出 203005")
    void shouldWrapFeignFailure() {
        when(vsoOrderService.transporting(any(OrderStateChangeRequest.class)))
                .thenThrow(new IllegalStateException("connect timed out"));

        assertThatThrownBy(() -> adapter.transporting("SO-1"))
                .isInstanceOf(TmsBusinessException.class)
                .hasMessageContaining("connect timed out");
    }
}
