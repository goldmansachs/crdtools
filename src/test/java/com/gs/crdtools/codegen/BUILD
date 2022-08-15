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
    deps = [
        ":compiles-generated-java",
        "//src/main/java/com/gs/crdtools:api-annotation",
        "//src/main/java/com/gs/crdtools:gen-source-from-spec",
        "//src/main/java/com/gs/crdtools/codegen:swagger-codegen-extensions",
        "//src/test/java/com/gs/crdtools:result-helper",
        "@bazel_tools//tools/java/runfiles",
        "@maven//:io_vavr_vavr",
    ],
)

genrule(
    name = "generate-java-from-crds",
    srcs = [
        "//src/test/resources:test_crds",
    ],
    outs = [
        "generated.srcjar",
    ],
    cmd = """$(location //src/main/java/com/gs/crdtools:generator) \
        -o $(location generated.srcjar) \
        -i $(locations //src/test/resources:test_crds) \
        -p com.gs.crdtools.generated""",
    tools = ["//src/main/java/com/gs/crdtools:generator"],
    visibility = ["//visibility:public"],
)

java_library(
    name = "compiles-generated-java",
    srcs = [":generate-java-from-crds"],
    deps = [
        "//src/main/java/com/gs/crdtools:api-annotation",
        "//src/main/java/com/gs/crdtools:base-object",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_swagger_core_v3_swagger_annotations",
        "@maven//:javax_validation_validation_api",
    ],
)
