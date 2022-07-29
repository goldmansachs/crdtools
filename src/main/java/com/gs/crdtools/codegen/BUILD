java_library(
    name = "swagger-codegen-extensions",
    srcs = [
        "CrdtoolsCodegen.java",
        "CustomOverrideTemplateEngine.java",
        "OverrideTemplateLoader.java",
    ],
    visibility = ["//visibility:public"],
    resources = ["//src/main/resources:all"],
    deps = [
        "//src/main/java/com/gs/crdtools:base-object",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_swagger_codegen_v3_swagger_codegen",
        "@maven//:io_swagger_codegen_v3_swagger_codegen_generators",
        "@maven//:io_swagger_core_v3_swagger_models",
        "@maven//:io_swagger_parser_v3_swagger_parser",
        "@maven//:io_vavr_vavr",
        "@maven//:com_github_jknack_handlebars",
    ],
)
