plugins {
  id("com.github.johnrengelman.shadow")

  id("otel.java-conventions")
}

group = "io.opentelemetry.javaagent"

val shadowInclude by configurations.creating {
  isCanBeResolved = true
  isCanBeConsumed = false
}

val caffeine2Version: String by project

dependencies {
  compileOnly("com.github.ben-manes.caffeine:caffeine:$caffeine2Version")
  shadowInclude("com.github.ben-manes.caffeine:caffeine:$caffeine2Version") {
    exclude("com.google.errorprone", "error_prone_annotations")
    exclude("org.checkerframework", "checker-qual")
  }
}

// patch inner class from Caffeine to avoid ForkJoinTask from being loaded too early in the javaagent
val patch by sourceSets.creating {
  java {}
}

tasks {
  shadowJar {
    configurations = listOf(shadowInclude)

    relocate("com.github.benmanes.caffeine", "io.opentelemetry.instrumentation.api.internal.shaded.caffeine2")

    minimize()
  }

  javadoc {
    enabled = false
  }

  val extractShadowJar by registering(Copy::class) {
    dependsOn(shadowJar)

    // replace caffeine class with our patched version
    from(zipTree(shadowJar.map { it.archiveFile })) {
    //from(zipTree(shadowJar.get().archiveFile)) {
      exclude("io/opentelemetry/instrumentation/api/internal/shaded/caffeine2/cache/BoundedLocalCache\$PerformCleanupTask.class")
      exclude("META-INF/**")
    }
    from(patch.output) {
      include("io/opentelemetry/instrumentation/api/internal/shaded/caffeine2/cache/BoundedLocalCache\$PerformCleanupTask.class")
    }

    into("build/extracted/shadow")
    // prevents empty com/github/benmanes/caffeine/cache path from ending up in instrumentation-api
    includeEmptyDirs = false
  }
}
