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
        ":compiles-example-java",
        ":result-helper",
        "//src/main/java/com/gs/crdtools:gen-source-from-spec",
        "//src/main/java/com/gs/crdtools:generator",
        "//src/main/java/com/gs/crdtools:spec-extractor",
        "//src/main/java/com/gs/crdtools:yamlifier",
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

genrule(
    name = "generate-example-crds",
    srcs = [
        "//src/test/resources:just_one_crd",
    ],
    outs = [
        "generated.srcjar",
    ],
    cmd = """$(location //src/main/java/com/gs/crdtools:generator) \
        $(location generated.srcjar) \
        $(locations //src/test/resources:just_one_crd)""",
    tools = ["//src/main/java/com/gs/crdtools:generator"],
    visibility = ["//visibility:public"],
)

java_library(
    name = "compiles-example-java",
    srcs = [":generate-example-crds"],
    deps = [
        "//src/main/java/com/gs/crdtools:api-annotation",
        "//src/main/java/com/gs/crdtools:base-object",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_swagger_core_v3_swagger_annotations",
        "@maven//:javax_validation_validation_api",
    ],
)
