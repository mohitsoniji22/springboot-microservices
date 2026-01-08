package com.example.ns.api.util;

import org.slf4j.*;

public class CorrelationIdUtil {

    public static void set(String correlationId) {
        if (correlationId != null) {
            MDC.put("correlationId", correlationId);
        }
    }
}
