java_library(
    name = "base-object",
    srcs = ["BaseObject.java"],
    visibility = ["//visibility:public"],
    deps = ["@maven//:io_kubernetes_client_java_api"],
)

java_library(
    name = "spec-extractor",
    visibility = ["//visibility:public"],
    srcs = ["SpecExtractorHelper.java"],
    resources = ["@k8s-config-connector//:install-bundles/install-bundle-workload-identity/crds.yaml"],
    deps = [
        "//src/main/java/com/gs/crdtools:vavr-helpers",
        "@nryaml//:nryaml",
        "@maven//:io_vavr_vavr",
        "@maven//:org_yaml_snakeyaml",
    ],
)

java_library(
    name = "openapi-src-genner",
    visibility = ["//visibility:public"],
    srcs = ["SourceGeneratorHelper.java"],
    deps = [
        "@maven//:io_vavr_vavr",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
    ]
)

java_library(
    name = "vavr-helpers",
    srcs = ["VavrHelpers.java"],
    visibility = ["//visibility:public"],
    deps = ["@maven//:io_vavr_vavr"],
)

java_binary(
    name = "gen-source-from-spec",
    srcs = ["SourceGenFromSpec.java"],
    jvm_flags = [
        "--add-opens java.base/java.util=ALL-UNNAMED",
        "-Dmodels",
        "-DmodelDocs=false",
        "-DmodelTests=false",
    ],
    visibility = ["//visibility:public"],
    main_class = "com.gs.crdtools.SourceGenFromSpec",
    deps = [
        "//src/main/java/com/gs/crdtools:spec-extractor",
        "//src/main/java/com/gs/crdtools:vavr-helpers",
        "//src/main/java/com/gs/crdtools/codegen:swagger-codegen-extensions",
        "//src/main/java/com/gs/crdtools:openapi-src-genner",
        "@maven//:io_swagger_codegen_v3_swagger_codegen",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_vavr_vavr",
        "@maven//:org_yaml_snakeyaml",
        "@nryaml//:nryaml",
    ],
)

java_binary(
    name = "generator",
    srcs = ["Generator.java"],
    visibility = ["//visibility:public"],
    main_class = "com.gs.crdtools.Generator",
    deps = [
        ":spec-extractor",
        ":gen-source-from-spec",
        "@nryaml//:nryaml",
        "//src/main/java/com/gs/crdtools/codegen:swagger-codegen-extensions",
        "//src/main/java/com/gs/crdtools:openapi-src-genner",
        "@maven//:io_vavr_vavr",
        "@maven//:org_yaml_snakeyaml",
    ],
)
