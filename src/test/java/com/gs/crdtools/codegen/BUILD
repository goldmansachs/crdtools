load("@rules_junit5_external//:junit5.bzl", "java_junit5_test")

java_junit5_test(
    name = "tests",
    srcs = glob([
        "*Test.java",
    ]),
    deps = [
        "//src/main/java/com/gs/crdtools/codegen:swagger-codegen-extensions",
        "//src/main/java/com/gs/crdtools:openapi-src-genner",
        "//src/main/java/com/gs/crdtools:gen-source-from-spec",
        "@bazel_tools//tools/java/runfiles",
        "@maven//:io_vavr_vavr",
    ],
    test_package = "com.gs.crdtools.codegen",
    size = "small",
    data = [
        "//src/test/resources:minimal-openapi",
    ],
)
