load("@rules_junit5_external//:junit5.bzl", "java_junit5_test")

java_junit5_test(
    name = "tests",
    size = "small",
    srcs = glob([
        "*Test.java",
    ]),
    data = [
        "//src/test/resources:minimal-openapi",
    ],
    test_package = "com.gs.crdtools.codegen",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/com/gs/crdtools:gen-source-from-spec",
        "//src/main/java/com/gs/crdtools:openapi-src-genner",
        "//src/main/java/com/gs/crdtools/codegen:swagger-codegen-extensions",
        "//src/test/java/com/gs/crdtools:result-helper",
        "@bazel_tools//tools/java/runfiles",
        "@maven//:io_vavr_vavr",
    ],
)
