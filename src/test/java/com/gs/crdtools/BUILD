load("@rules_junit5_external//:junit5.bzl", "java_junit5_test")

java_junit5_test(
    name = "tests",
    srcs = glob([
        "*Test.java",
    ]),
    deps = [
        "//src/main/java/com/gs/crdtools:source-generator",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_vavr_vavr",
        "@maven//:org_apache_commons_commons_compress",
        "@maven//:org_yaml_snakeyaml",
    ],
    test_package = "com.gs.crdtools",
    size = "small",
)
