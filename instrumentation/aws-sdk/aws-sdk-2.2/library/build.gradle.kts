plugins {
  id("otel.library-instrumentation")
}

dependencies {
  implementation("io.opentelemetry.contrib:opentelemetry-aws-xray-propagator")

  library("software.amazon.awssdk:aws-core:2.2.0")
  library("software.amazon.awssdk:sqs:2.2.0")
  library("software.amazon.awssdk:aws-json-protocol:2.2.0")
  compileOnly(project(":muzzle")) // For @NoMuzzle

  testImplementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:testing"))

  testLibrary("software.amazon.awssdk:dynamodb:2.2.0")
  testLibrary("software.amazon.awssdk:ec2:2.2.0")
  testLibrary("software.amazon.awssdk:kinesis:2.2.0")
  testLibrary("software.amazon.awssdk:rds:2.2.0")
  testLibrary("software.amazon.awssdk:s3:2.2.0")
}

testing {
  suites {
    val testCoreOnly by registering(JvmTestSuite::class) {
      sources {
        groovy {
          setSrcDirs(listOf("src/testCoreOnly/groovy"))
        }
      }

      dependencies {
        implementation(project())
        implementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:testing"))
        implementation("software.amazon.awssdk:aws-core:2.2.0")
        implementation("software.amazon.awssdk:aws-json-protocol:2.2.0")
        implementation("software.amazon.awssdk:dynamodb:2.2.0")
      }
    }
  }
}

tasks {
  withType<Test> {
    systemProperty("testLatestDeps", findProperty("testLatestDeps") as Boolean)
    systemProperty("otel.instrumentation.aws-sdk.experimental-span-attributes", true)
    systemProperty("otel.instrumentation.aws-sdk.experimental-use-propagator-for-messaging", true)
  }
}
