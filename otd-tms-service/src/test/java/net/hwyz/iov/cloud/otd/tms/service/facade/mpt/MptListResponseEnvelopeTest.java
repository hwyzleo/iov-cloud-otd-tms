package net.hwyz.iov.cloud.otd.tms.service.facade.mpt;

import com.github.pagehelper.PageHelper;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import net.hwyz.iov.cloud.framework.common.bean.PageResult;
import net.hwyz.iov.cloud.otd.tms.service.application.service.LogisticsNodeTypeAppService;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodeTypePo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理后台列表响应契约测试（OTD-TMS-DSN-CR-002）
 * <p>
 * MPT 前端统一按 {@code response.data.items} / {@code response.data.total} 取值
 * （与 VSO、WMS 等服务的 MPT 接口一致），因此列表接口必须返回
 * {@code ApiResponse<PageResult<X>>} 信封；直接返回裸 {@code PageResult} 会导致
 * 前端取不到 {@code data.items}，页面空白（据点类型等 7 个主数据页面曾全部如此）。
 *
 * @author TMS Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MPT 列表响应契约")
class MptListResponseEnvelopeTest {

    /**
     * TMS 管理后台全部分页列表 Controller
     */
    private static final List<Class<?>> MPT_CONTROLLERS = List.of(
            CarrierMptController.class,
            DriverMptController.class,
            LoadingVehicleMptController.class,
            LogisticsNodeMptController.class,
            LogisticsNodeTypeMptController.class,
            TransportOrderMptController.class,
            TransportRouteMptController.class,
            TransportTypeMptController.class);

    @Mock
    private LogisticsNodeTypeAppService logisticsNodeTypeAppService;

    @Test
    @DisplayName("8 个 MPT 列表接口统一返回 ApiResponse<PageResult<X>> 信封")
    void allMptListApisShouldReturnApiResponseEnvelope() {
        for (Class<?> controller : MPT_CONTROLLERS) {
            Method list = findListMethod(controller);
            assertThat(list.getGenericReturnType())
                    .as(controller.getSimpleName() + "#list 必须声明为 ApiResponse<PageResult<...>>")
                    .isInstanceOf(ParameterizedType.class);

            ParameterizedType returnType = (ParameterizedType) list.getGenericReturnType();
            assertThat(returnType.getRawType()).isEqualTo(ApiResponse.class);

            ParameterizedType dataType = (ParameterizedType) returnType.getActualTypeArguments()[0];
            assertThat(dataType.getRawType())
                    .as(controller.getSimpleName() + "#list 的 data 必须是 PageResult")
                    .isEqualTo(PageResult.class);
        }
    }

    @Test
    @DisplayName("列表响应体包含 data.items 与 data.total，前端才能渲染表格")
    void listShouldExposeItemsUnderDataNode() throws Exception {
        Date createTime = new Date(1763441727412L);
        when(logisticsNodeTypeAppService.search(any(), any(), any(), any()))
                .thenReturn(List.of(
                        LogisticsNodeTypePo.builder().id(1L).code("PDC").name("前置库")
                                .enable(true).sort(99).createTime(createTime).build(),
                        LogisticsNodeTypePo.builder().id(2L).code("DDC").name("经销商库/交付中心")
                                .enable(true).sort(99).createTime(createTime).build()));

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new LogisticsNodeTypeMptController(logisticsNodeTypeAppService))
                .build();
        try {
            mockMvc.perform(get("/mpt/logisticsNodeType/list")
                            .param("pageNum", "1")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("000000"))
                    .andExpect(jsonPath("$.data.items.length()").value(2))
                    .andExpect(jsonPath("$.data.items[0].code").value("PDC"))
                    .andExpect(jsonPath("$.data.total").value(2));
        } finally {
            PageHelper.clearPage();
        }
    }

    private Method findListMethod(Class<?> controller) {
        for (Method method : controller.getDeclaredMethods()) {
            if ("list".equals(method.getName())) {
                return method;
            }
        }
        throw new IllegalStateException(controller.getSimpleName() + " 未声明 list 方法");
    }
}
