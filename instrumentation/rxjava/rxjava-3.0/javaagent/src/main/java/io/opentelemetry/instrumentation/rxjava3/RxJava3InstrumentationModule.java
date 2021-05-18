/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rxjava3;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

@AutoService(InstrumentationModule.class)
public class RxJava3InstrumentationModule extends InstrumentationModule {

  public RxJava3InstrumentationModule() {
    super("rxjava3");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return Collections.singletonList(new PluginInstrumentation());
  }

  public static class PluginInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
      return named("io.reactivex.rxjava3.plugins.RxJavaPlugins");
    }

    @Override
    public void transform(TypeTransformer transformer) {
      transformer.applyAdviceToMethod(
          isMethod(), RxJava3InstrumentationModule.class.getName() + "$RxJavaPluginsAdvice");
    }
  }

  public static class RxJavaPluginsAdvice {

    // TODO(anuraaga): Replace with adding a type initializer to RxJavaPlugins
    // https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/2685
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void activateOncePerClassloader() {
      TracingAssemblyActivation.activate(RxJavaPlugins.class);
    }
  }
}
