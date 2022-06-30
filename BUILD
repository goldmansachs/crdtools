# This file exists so that the intellij Bazel plugin has something to bootstrap the project from

load("@com_github_bazelbuild_buildtools//buildifier:def.bzl", "buildifier")

buildifier(
    name = "buildifier",
)

# Allows for running of bazel build //:spec with output outside of bazel-bin
genrule(
    name = "spec",
    outs = ["all-specs.yaml"],
    cmd = "$(location //src/main/java/com/gs/crdtools:spec-extractor) \"$@\"",
    tools = ["//src/main/java/com/gs/crdtools:spec-extractor"],
)
