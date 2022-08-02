filegroup(
    name = "all",
    srcs = glob(["*.txt"]) + ["minimal-openapi.yaml"],
    visibility = ["//visibility:public"],
)

filegroup(
    name = "test_crds",
    srcs = [
        "minimal-crd.yaml",
        "managedcertificates-crd.yaml",
    ],
    visibility = ["//visibility:public"],
)
