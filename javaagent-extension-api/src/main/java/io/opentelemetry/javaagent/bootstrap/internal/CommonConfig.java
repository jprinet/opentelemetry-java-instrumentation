/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.bootstrap.internal;

import static java.util.Collections.emptyMap;

import java.util.List;
import java.util.Map;

/**
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
public final class CommonConfig {

  private static final CommonConfig instance = new CommonConfig(InstrumentationConfig.get());

  public static CommonConfig get() {
    return instance;
  }

  private final Map<String, String> peerServiceMapping;
  private final List<String> clientRequestHeaders;
  private final List<String> clientResponseHeaders;
  private final List<String> serverRequestHeaders;
  private final List<String> serverResponseHeaders;
  private final boolean statementSanitizationEnabled;

  CommonConfig(InstrumentationConfig config) {
    peerServiceMapping =
        config.getMap("otel.instrumentation.common.peer-service-mapping", emptyMap());

    // TODO (mateusz): remove the old config names in 2.0
    clientRequestHeaders =
        DeprecatedConfigProperties.getList(
            config,
            "otel.instrumentation.http.capture-headers.client.request",
            "otel.instrumentation.http.client.capture-request-headers");
    clientResponseHeaders =
        DeprecatedConfigProperties.getList(
            config,
            "otel.instrumentation.http.capture-headers.client.response",
            "otel.instrumentation.http.client.capture-response-headers");
    serverRequestHeaders =
        DeprecatedConfigProperties.getList(
            config,
            "otel.instrumentation.http.capture-headers.server.request",
            "otel.instrumentation.http.server.capture-request-headers");
    serverResponseHeaders =
        DeprecatedConfigProperties.getList(
            config,
            "otel.instrumentation.http.capture-headers.server.response",
            "otel.instrumentation.http.server.capture-response-headers");

    statementSanitizationEnabled =
        config.getBoolean("otel.instrumentation.common.db-statement-sanitizer.enabled", true);
  }

  public Map<String, String> getPeerServiceMapping() {
    return peerServiceMapping;
  }

  public List<String> getClientRequestHeaders() {
    return clientRequestHeaders;
  }

  public List<String> getClientResponseHeaders() {
    return clientResponseHeaders;
  }

  public List<String> getServerRequestHeaders() {
    return serverRequestHeaders;
  }

  public List<String> getServerResponseHeaders() {
    return serverResponseHeaders;
  }

  public boolean isStatementSanitizationEnabled() {
    return statementSanitizationEnabled;
  }
}
