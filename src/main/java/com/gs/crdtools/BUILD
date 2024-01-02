java_library(
    name = "base-object",
    srcs = [
        "BaseObject.java",
    ],
    visibility = ["//visibility:public"],
    deps = ["@maven//:io_kubernetes_client_java_api"],
)

java_library(
    name = "api-annotation",
    srcs = [
        "ApiInformation.java", 
        "Generated.java",
    ],
    visibility = ["//visibility:public"],
)

java_library(
    name = "spec-extractor",
    srcs = ["SpecExtractorHelper.java"],
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:io_vavr_vavr",
        "@maven//:org_yaml_snakeyaml",
        "@nryaml",
    ],
)

java_library(
    name = "swagger-codegen-extensions",
    srcs = [
        "CrdtoolsCodegen.java",
        "CustomOverrideTemplateEngine.java",
        "OverrideTemplateLoader.java",
    ],
    resources = ["//src/main/resources:all"],
    visibility = ["//visibility:public"],
    deps = [
        ":api-annotation",
        ":base-object",
        ":spec-extractor",
        "@maven//:com_github_jknack_handlebars",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_swagger_codegen_v3_swagger_codegen",
        "@maven//:io_swagger_codegen_v3_swagger_codegen_generators",
        "@maven//:io_swagger_core_v3_swagger_models",
        "@maven//:io_swagger_parser_v3_swagger_parser_v3",
        "@maven//:io_vavr_vavr",
    ],
)

java_library(
    name = "gen-source-from-spec",
    srcs = ["SourceGenFromSpec.java"],
    visibility = ["//visibility:public"],
    deps = [
        ":spec-extractor",
        ":swagger-codegen-extensions",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_swagger_codegen_v3_swagger_codegen",
        "@maven//:io_vavr_vavr",
        "@maven//:org_yaml_snakeyaml",
        "@nryaml",
    ],
)

java_binary(
    name = "generator",
    srcs = ["Generator.java"],
    jvm_flags = [
        "--add-opens java.base/java.util=ALL-UNNAMED",
        "-Dmodels",
        "-DmodelDocs=false",
        "-DmodelTests=false",
    ],
    main_class = "com.gs.crdtools.Generator",
    visibility = ["//visibility:public"],
    deps = [
        ":gen-source-from-spec",
        ":spec-extractor",
        ":swagger-codegen-extensions",
        "@maven//:info_picocli_picocli",
        "@maven//:io_vavr_vavr",
        "@maven//:org_yaml_snakeyaml",
        "@nryaml",
    ],
)

java_library(
    name = "runtime-dependencies",
    srcs = [
        "ApiInformation.java",
        "BaseObject.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":api-annotation",
        ":base-object",
        "@maven//:io_kubernetes_client_java_api",
    ],
)
