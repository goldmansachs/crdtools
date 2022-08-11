java_library(
    name = "base-object",
    srcs = ["BaseObject.java"],
    visibility = ["//visibility:public"],
    deps = ["@maven//:io_kubernetes_client_java_api"],
)

java_library(
    name = "api-annotation",
    srcs = ["ApiInformation.java"],
    visibility = ["//visibility:public"],
)

java_library(
    name = "spec-extractor",
    srcs = ["SpecExtractorHelper.java"],
    resources = ["@k8s-config-connector//:install-bundles/install-bundle-workload-identity/crds.yaml"],
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/com/gs/crdtools:vavr-helpers",
        "@maven//:io_vavr_vavr",
        "@nryaml",
    ],
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
    main_class = "com.gs.crdtools.SourceGenFromSpec",
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/com/gs/crdtools:spec-extractor",
        "//src/main/java/com/gs/crdtools:vavr-helpers",
        "//src/main/java/com/gs/crdtools/codegen:swagger-codegen-extensions",
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
    main_class = "com.gs.crdtools.Generator",
    visibility = ["//visibility:public"],
    deps = [
        ":gen-source-from-spec",
        ":spec-extractor",
        "//src/main/java/com/gs/crdtools/codegen:swagger-codegen-extensions",
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
