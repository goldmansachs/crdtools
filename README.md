# crdtools

> **NOTE: This project is currently in early stages of development.** 
> **Things will change without warning and might not work.**

crdtools is a java library that provides functionality related to [Kubernetes Custom Resource 
Definitions](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/).
The first planned piece of functionality is to generate source code to java classes that 
corresponds to the types defined in a CustomResourceDefinition objects _(available now)_. Instances of the generated
classes can then be used to create objects which can then be serialised to their yaml 
representation and applied to a kubernetes cluster _(work in progress)_.

## How to build and develop

This software is built using [bazel](https://bazel.build). The recommended way is to download
and install [bazelisk](https://github.com/bazelbuild/bazelisk) which will download and install
the correct version behind as needed.

The development tooling we use is Intellij Idea Community Edition with the bazel plugin. Please
note that to be able to run the latest intellij, the beta version of the bazel plugin will need
to be installed. This is done by navigating to Preferences -> Plugins then click the cogwheel and
choose "Manage Plugin Repositories". Add https://plugins.jetbrains.com/plugins/beta/list to 
the list of repositories. After this is done, the bazel plugin compatible with recent IntelliJ
versions should be available by clicking "Marketplace" in the Plugins and searching for Bazel.
This has been tested with IntelliJ Idea Community 2022.1.2.

## How to use the tool

As the software has been built using bazel, the recommended way to integrate this library into
an existing project is by using the above as a build tool and following these instructions:

### Updating the Workspace file

The following lines need to be added to the WORKSPACE file and are necessary to download all
necessary dependencies needed to use crdtools.

```
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository", "new_git_repository")

git_repository(
    name = "crdtools",
    commit = "4e0e449c30896429ca2ea10c2a621a15028f8328",
    remote = "https://github.com/goldmansachs/crdtools",
)

git_repository(
    name = "nryaml",
    commit = "9dfe6b071aeccb652280564da0077940a5c1fc5f",
    remote = "https://github.com/nresare/nryaml",
    shallow_since = "",
)

maven_install(
    artifacts = [
        "io.vavr:vavr:0.10.3",
        "org.apache.commons:commons-compress:1.21",
        "org.yaml:snakeyaml:1.30",
        "com.fasterxml.jackson.core:jackson-databind:2.13.3",
        "com.fasterxml.jackson.core:jackson-core:2.13.3",
        "com.fasterxml.jackson.core:jackson-annotations:2.13.3",
        "io.swagger.codegen.v3:swagger-codegen:3.0.34",
        "io.swagger.codegen.v3:swagger-codegen-generators:1.0.34",
        "io.swagger.parser.v3:swagger-parser-v3:2.1.1",
        "io.swagger.core.v3:swagger-models:2.2.1",
        "com.github.jknack:handlebars:4.3.0",
        "javax.validation:validation-api:2.0.1.Final",
        "io.kubernetes:client-java-api:16.0.0",
        "io.kubernetes:client-java:16.0.0",
        "info.picocli:picocli:4.6.3",
    ],
    fetch_sources = True,
    override_targets = {
        "com_atlassian_commonmark_commonmark": "org_commonmark_commonmark",
    },
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)
```

### Defining an input

After the necessary dependencies have been download, it is already possible to utilise the tool.
Input is defined as follows.

#### Custom Input

To generate code for a custom input, follow Bazel standard practices for defining a _filegroup_ in the
BUILD file within the input folder location. For example:

```
filegroup(
    name = "example-crd",
    srcs = ["minimal-crd.yaml"],
    visibility = ["//visibility:public"],
)
```

Then in the BUILD file within the source folder, where crdtools is being used, grab the previously
defined file as follows:

```
filegroup(
    name = "example-crd",
    srcs = ["//resources:example-crd"],
)
```

It is now possible to reference `:example-crd` within the _genrule_ for the tool (see below).

#### KCC Input

To use the available CRDs in the [GCP repository](https://github.com/GoogleCloudPlatform/k8s-config-connector.git),
add the following lines to the WORKSPACE file.

```
new_git_repository(
    name = "k8s-config-connector",
    build_file_content = "exports_files(['install-bundles/install-bundle-workload-identity/crds.yaml'])",
    commit = "1c096d9a6382fb0b6e54901e5b618f6ee9d0282b",
    remote = "https://github.com/GoogleCloudPlatform/k8s-config-connector.git",
    shallow_since = "",
)
```

It is now possible to reference _k8s-config-connector_ as source for the generator.

### Generating compile-able code

To generate compile-able java code corresponding to the input CRD(s), head to the BUILD file within the
source folder and add the following lines:

```
genrule(
    name = "generate-java-from-crds",
    srcs = [":example-crd"],
    outs = [
        "generated.srcjar",
    ],
    cmd = """ $(location @crdtools//:generator) \
        $(location generated.srcjar) \
        $(location :example-crd)""",
    tools = ["@crdtools//:generator"],
    visibility = ["//visibility:public"],
)

java_library(
    name = "kccapi",
    srcs = ["generate-java-from-crds"],
    visibility = ["//visibility:public"],
    deps = [
        "@crdtools//:runtime-dependencies",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:io_swagger_core_v3_swagger_annotations",
        "@maven//:javax_validation_validation_api",
    ],
)
```

In the genrule update the cmd option to reflect your input:
- Update _outs_ and _$(location generated.srcjar)_ to set the output name to something different from
generated.srcjar.
- Update _$(location :example-crd)_ to set the input for the generator.

Finally, add _kccapi_ to the set of dependencies in the _java_binary_ where the java generated CRDs are
invoked. An example is as follows:

```
java_binary(
    name = "example-with-code",
    srcs = ["Main.java"],
    main_class = "src.Main",
    deps = [
        "kccapi",
        "@maven//:io_kubernetes_client_java",
        "@maven//:io_kubernetes_client_java_api",
    ],
)
```

## Credits
The code that this project is based on has been written by Jonathan Perry at
[Goldman Sachs](https://www.gs.com/).
