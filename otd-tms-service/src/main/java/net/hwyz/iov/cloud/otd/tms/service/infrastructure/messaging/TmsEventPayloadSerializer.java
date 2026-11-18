package net.hwyz.iov.cloud.otd.tms.service.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Component;

/**
 * 出站事件载荷序列化器（OTD-TMS-DSN-CR-001）
 * <p>
 * 独立的静态 {@link ObjectMapper}，不注册为 Spring Bean：额外注册 ObjectMapper 会关闭 Boot 的
 * 条件化主 mapper，导致框架 Jackson 启动探针按错误 mapper 校验（FW-WEB-DSN-CR-003）。
 *
 * @author hwyz_leo
 */
@Component
public class TmsEventPayloadSerializer {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .serializationInclusion(JsonInclude.Include.ALWAYS)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    /**
     * 序列化事件载荷
     *
     * @param event 事件对象
     * @return 不可变 JSON 文本
     */
    public String serialize(Object event) {
        try {
            return MAPPER.writeValueAsString(event);
        } catch (Exception e) {
            throw new IllegalStateException("出站事件序列化失败", e);
        }
    }
}
