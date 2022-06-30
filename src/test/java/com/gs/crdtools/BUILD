load("@rules_junit5_external//:junit5.bzl", "java_junit5_test")

java_junit5_test(
    name = "tests",
    size = "small",
    srcs = glob([
        "*Test.java",
    ]),
    test_package = "com.gs.crdtools",
    deps = [
        "//src/main/java/com/gs/crdtools:source-generator",
    ],
)
