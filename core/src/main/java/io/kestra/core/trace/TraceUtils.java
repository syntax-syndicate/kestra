package io.kestra.core.trace;

import io.kestra.core.models.executions.Execution;
import io.kestra.core.runners.DefaultRunContext;
import io.kestra.core.runners.RunContext;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;

public final class TraceUtils {
    public static final AttributeKey<String> ATTR_UID = AttributeKey.stringKey("kestra.uid");

    private static final AttributeKey<String> ATTR_TENANT_ID = AttributeKey.stringKey("kestra.tenantId");
    private static final AttributeKey<String> ATTR_NAMESPACE = AttributeKey.stringKey("kestra.namespace");
    private static final AttributeKey<String> ATTR_FLOW_ID = AttributeKey.stringKey("kestra.flowId");
    private static final AttributeKey<String> ATTR_EXECUTION_ID = AttributeKey.stringKey("kestra.executionId");

    private TraceUtils() {}

    public static Attributes attributesFrom(Execution execution) {
        var builder = Attributes.builder()
            .put(ATTR_NAMESPACE, execution.getNamespace())
            .put(ATTR_FLOW_ID, execution.getFlowId())
            .put(ATTR_EXECUTION_ID, execution.getId());

        if (execution.getTenantId() != null) {
            builder.put(ATTR_TENANT_ID, execution.getTenantId());
        }

        return builder.build();
    }

    public static Attributes attributesFrom(RunContext.FlowInfo flowInfo) {
        var builder = Attributes.builder()
            .put(ATTR_NAMESPACE, flowInfo.namespace())
            .put(ATTR_FLOW_ID, flowInfo.id());

        if (flowInfo.tenantId() != null) {
            builder.put(ATTR_TENANT_ID, flowInfo.tenantId());
        }

        return builder.build();
    }
}
