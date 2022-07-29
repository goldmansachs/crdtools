# The maven setup is copied from https://github.com/bazelbuild/rules_jvm_external/blob/master/README.md
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

###########################
## External dependencies ##
###########################

load("@bazel_tools//tools/build_defs/repo:git.bzl", "new_git_repository")

new_git_repository(
    name = "k8s-config-connector",
    commit = "1c096d9a6382fb0b6e54901e5b618f6ee9d0282b",
    remote = "https://github.com/GoogleCloudPlatform/k8s-config-connector.git",
    shallow_since = "",
    build_file_content = "exports_files(['install-bundles/install-bundle-workload-identity/crds.yaml'])"
)

###########################
##    Java build rules   ##
###########################

# Use the latest release from https://github.com/bazelbuild/rules_jvm_external
RULES_JVM_EXTERNAL_TAG = "4.2"

RULES_JVM_EXTERNAL_SHA = "cd1a77b7b02e8e008439ca76fd34f5b07aecb8c752961f9640dea15e9e5ba1ca"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "io.kubernetes:client-java-api:14.0.1",
        "io.vavr:vavr:0.10.3",
        "org.apache.commons:commons-compress:1.21",
        "org.yaml:snakeyaml:1.30",
        "com.fasterxml.jackson.core:jackson-databind:2.13.3",
        "com.fasterxml.jackson.core:jackson-core:2.13.3",
        "io.swagger.codegen.v3:swagger-codegen:3.0.34",
        "io.swagger.codegen.v3:swagger-codegen-generators:1.0.34",
        "io.swagger.parser.v3:swagger-parser-v3:2.1.1",
        "io.swagger.core.v3:swagger-models:2.2.1",
        "com.github.jknack:handlebars:4.3.0",
    ],
    # The rules_jvm_external, when adding the swagger dependencies, downloads
    # a version of atlassian that comes under a different name. This is a
    # workaround to be able to build the project.
    override_targets = {
        "com_atlassian_commonmark_commonmark" : "org_commonmark_commonmark",
    },
    fetch_sources = True,
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)

# end maven setup

# this tracks the latest commit at the top of https://github.com/junit-team/junit5-samples/
JUNIT5_RULES_EXTERNAL_COMMMIT = "7c6536901a4446edf7d1f3e42a413ce61c676fc2"

JUNIT5_RULES_EXTERNAL_SHA = "1ed55e03d241d7b449bce089aad0cf12c9f208061baa09de254e685a86dd74c6"

http_archive(
    name = "rules_junit5_external",
    sha256 = JUNIT5_RULES_EXTERNAL_SHA,
    strip_prefix = "junit5-samples-%s/junit5-jupiter-starter-bazel" % JUNIT5_RULES_EXTERNAL_COMMMIT,
    url = "https://github.com/junit-team/junit5-samples/archive/%s.tar.gz" % JUNIT5_RULES_EXTERNAL_COMMMIT,
)

load("@rules_junit5_external//:junit5.bzl", "junit_jupiter_java_repositories", "junit_platform_java_repositories")

junit_jupiter_java_repositories()

junit_platform_java_repositories()
