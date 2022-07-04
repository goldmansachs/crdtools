java_binary(
    name = "source-generator",
    srcs = ["SourceGenerator.java"],
    main_class = "com.gs.crdtools.SourceGenerator",
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:io_vavr_vavr",
    ],
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

java_library(
    name = "vavr-helpers",
    srcs = ["VavrHelpers.java"],
    visibility = ["//visibility:public"],
    deps = ["@maven//:io_vavr_vavr"],
)
