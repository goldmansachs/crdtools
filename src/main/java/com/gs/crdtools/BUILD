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
        "@maven//:io_vavr_vavr",
        "@maven//:org_yaml_snakeyaml",
    ],
)

java_library(
    name = "openapi-src-genner",
    visibility = ["//visibility:public"],
    srcs = ["SourceGeneratorHelper.java"],
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
        "//src/main/java/com/gs/crdtools:openapi-src-genner",
        "//src/main/java/com/gs/crdtools:spec-extractor",
        "//src/main/java/io/swagger/codegen/v3/cli:swagger-codegen-entrypoint",
        "//src/main/java/com/gs/crdtools:vavr-helpers",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_vavr_vavr",
        "@maven//:org_yaml_snakeyaml",
    ],
)

genrule(
    name = "spec",
    outs = ["all-specs-only.yaml"],
    visibility = ["//visibility:public"],
    cmd = "$(location gen-source-from-spec) \"$@\"",
    tools = ["gen-source-from-spec"],
)

genrule(
    name = "kcc_java_genned",
    outs = [
        "all-specs.yaml",
        "genned.srcjar",
    ],
    visibility = ["//visibility:public"],
    cmd = "$(location gen-source-from-spec) $(location all-specs.yaml) $(location genned.srcjar)",
    tools = [
        "gen-source-from-spec",
    ],
)
