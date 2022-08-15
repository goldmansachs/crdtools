load("@rules_junit5_external//:junit5.bzl", "java_junit5_test")

java_junit5_test(
    name = "tests",
    size = "small",
    srcs = glob([
        "*Test.java",
    ]),
    data = [
        "//src/test/resources:minimal-openapi",
        "//src/test/resources:test_crds",
    ],
    test_package = "com.gs.crdtools",
    deps = [
        ":result-helper",
        "//src/main/java/com/gs/crdtools:crdtools-args-parser",
        "//src/main/java/com/gs/crdtools:gen-source-from-spec",
        "//src/main/java/com/gs/crdtools:generator",
        "//src/main/java/com/gs/crdtools:spec-extractor",
        "//src/main/java/com/gs/crdtools:vavr-helpers",
        "//src/main/java/com/gs/crdtools/codegen:swagger-codegen-extensions",
        "@bazel_tools//tools/java/runfiles",
        "@maven//:io_vavr_vavr",
        "@nryaml",
    ],
)

java_library(
    name = "result-helper",
    srcs = ["Result.java"],
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:io_vavr_vavr",
        "@org_junit_jupiter_junit_jupiter_api//jar",
    ],
)
