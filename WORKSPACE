# The maven setup is copied from https://github.com/bazelbuild/rules_jvm_external/blob/master/README.md

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

##
load("//:external_deps.bzl", "external_deps")
external_deps()
##

# Set up the buildifier settings in accordance with https://github.com/bazelbuild/buildtools/tree/master/buildifier#readme
#http_archive(
#    name = "io_bazel_rules_go",
#    sha256 = "685052b498b6ddfe562ca7a97736741d87916fe536623afb7da2824c0211c369",
#    urls = [
#        "https://mirror.bazel.build/github.com/bazelbuild/rules_go/releases/download/v0.33.0/rules_go-v0.33.0.zip",
#        "https://github.com/bazelbuild/rules_go/releases/download/v0.33.0/rules_go-v0.33.0.zip",
#    ],
#)
#
#load("@io_bazel_rules_go//go:deps.bzl", "go_register_toolchains", "go_rules_dependencies")
#
#go_rules_dependencies()
#
#go_register_toolchains(version = "1.18.3")
#
#http_archive(
#    name = "bazel_gazelle",
#    sha256 = "de69a09dc70417580aabf20a28619bb3ef60d038470c7cf8442fafcf627c21cb",
#    urls = [
#        "https://mirror.bazel.build/github.com/bazelbuild/bazel-gazelle/releases/download/v0.24.0/bazel-gazelle-v0.24.0.tar.gz",
#        "https://github.com/bazelbuild/bazel-gazelle/releases/download/v0.24.0/bazel-gazelle-v0.24.0.tar.gz",
#    ],
#)
#
#load("@bazel_gazelle//:deps.bzl", "gazelle_dependencies")
#
## If you use WORKSPACE.bazel, use the following line instead of the bare gazelle_dependencies():
## gazelle_dependencies(go_repository_default_config = "@//:WORKSPACE.bazel")
#gazelle_dependencies()
#
#http_archive(
#    name = "com_google_protobuf",
#    sha256 = "3bd7828aa5af4b13b99c191e8b1e884ebfa9ad371b0ce264605d347f135d2568",
#    strip_prefix = "protobuf-3.19.4",
#    urls = [
#        "https://github.com/protocolbuffers/protobuf/archive/v3.19.4.tar.gz",
#    ],
#)
#
#load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")
#
#protobuf_deps()
#
#http_archive(
#    name = "com_github_bazelbuild_buildtools",
#    sha256 = "ae34c344514e08c23e90da0e2d6cb700fcd28e80c02e23e4d5715dddcb42f7b3",
#    strip_prefix = "buildtools-4.2.2",
#    urls = [
#        "https://github.com/bazelbuild/buildtools/archive/refs/tags/4.2.2.tar.gz",
#    ],
#)

###########################
## External dependencies ##
###########################
load("//:expanded_external_deps.bzl", expanded_external_deps = "all")
#go_register_toolchains(go_version = external_dependencies["go"]["version"])
#load("//:expanded_external_deps.bzl", expanded_external_deps = "all")

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
    ],
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
