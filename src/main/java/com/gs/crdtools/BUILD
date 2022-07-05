java_library(
    name = "base-object",
    srcs = ["BaseObject.java"],
    visibility = ["//visibility:public"],
    deps = ["@maven//:io_kubernetes_client_java_api"],
)

java_binary(
    name = "spec-extractor",
    srcs = ["SpecExtractor.java"],
    main_class = "com.gs.crdtools.SpecExtractor",
    resources = ["@k8s-config-connector//:install-bundles/install-bundle-workload-identity/crds.yaml"],
    visibility = ["//visibility:public"],
    deps = [
        "//src/main/java/com/gs/crdtools:vavr-helpers",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_vavr_vavr",
        "@maven//:org_apache_commons_commons_compress",
        "@maven//:org_yaml_snakeyaml",
    ],
)

genrule(
    name = "spec",
    outs = ["all-specs.yaml"],
    visibility = ["//visibility:public"],
    cmd = "$(location spec-extractor) \"$@\"",
    tools = ["spec-extractor"],
)

java_library(
    name = "vavr-helpers",
    srcs = ["VavrHelpers.java"],
    visibility = ["//visibility:public"],
    deps = ["@maven//:io_vavr_vavr"],
)

java_binary(
    name = "openapi-src-genner",
    srcs = ["SourceGenerator.java"],
    jvm_flags = [
        "--add-opens java.base/java.util=ALL-UNNAMED",
        "-Dmodels",
        "-DmodelDocs=false",
        "-DmodelTests=false",
    ],
    visibility = ["//visibility:public"],
    main_class = "com.gs.crdtools.SourceGenerator",
    deps = [
        "//src/main/java/io/swagger/codegen/v3/cli:swagger-codegen-entrypoint",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_swagger_codegen_v3_swagger_codegen_cli",
        "@maven//:io_vavr_vavr",
        "@maven//:org_yaml_snakeyaml",
    ],
)

genrule(
    name = "kcc_java_genned",
    outs = ["genned.srcjar"],
    visibility = ["//visibility:public"],
    cmd = "$(location openapi-src-genner) $(location spec) \"$@\"",
    tools = [
        "openapi-src-genner",
        "spec",
    ],
)
