# The maven setup is copied from https://github.com/bazelbuild/rules_jvm_external/blob/master/README.md

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# Use the latest release from https://github.com/bazelbuild/rules_jvm_external
RULES_JVM_EXTERNAL_TAG = "4.2"
RULES_JVM_EXTERNAL_SHA = "cd1a77b7b02e8e008439ca76fd34f5b07aecb8c752961f9640dea15e9e5ba1ca"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
       "io.vavr:vavr:0.10.3",
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
    fetch_sources = True,
)

# end maven setup

# this tracks the latest commit at the top of https://github.com/junit-team/junit5-samples/
JUNIT5_RULES_EXTERNAL_COMMMIT = "7c6536901a4446edf7d1f3e42a413ce61c676fc2"
JUNIT5_RULES_EXTERNAL_SHA = "1ed55e03d241d7b449bce089aad0cf12c9f208061baa09de254e685a86dd74c6"

http_archive(
    name = "rules_junit5_external",
    strip_prefix = "junit5-samples-%s/junit5-jupiter-starter-bazel" % JUNIT5_RULES_EXTERNAL_COMMMIT,
    url = "https://github.com/junit-team/junit5-samples/archive/%s.tar.gz" % JUNIT5_RULES_EXTERNAL_COMMMIT,
    sha256 = JUNIT5_RULES_EXTERNAL_SHA
)

load("@rules_junit5_external//:junit5.bzl", "junit_jupiter_java_repositories", "junit_platform_java_repositories")

junit_jupiter_java_repositories()
junit_platform_java_repositories()
