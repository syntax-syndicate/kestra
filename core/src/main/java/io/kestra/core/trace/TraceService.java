package io.kestra.core.trace;

import io.kestra.core.runners.RunContext;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.concurrent.Callable;

@Singleton
public class TraceService {
    @Inject
    private OpenTelemetry openTelemetry;

    @Inject
    private Tracer tracer;

    public <V> V inTraceContext(RunContext runContext, String spanName, Attributes additonalAttributes, Callable<V> callable) throws Exception {
        // extract the traceparent from the run context to allow trace propagation
        var propagator = openTelemetry.getPropagators().getTextMapPropagator();
        Context extractedContext = propagator.extract(Context.current(), runContext, RunContextTextMapGetter.INSTANCE);

        try (Scope ignored = extractedContext.makeCurrent()) {
            var span = tracer.spanBuilder(spanName)
                .setAllAttributes(TraceUtils.attributesFrom(runContext.flowInfo()))
                .setAllAttributes(additonalAttributes)
                .startSpan();
            try {
                return callable.call();
            } catch(Exception e) {
                span.setStatus(StatusCode.ERROR, e.getMessage());
                throw e;
            } finally {
                span.end();
            }
        }
    }
}
