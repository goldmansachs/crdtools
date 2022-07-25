# crdtools

> **NOTE: This project is currently in early stages of development.** 
> **Things will change without warning and might not work.**

crdtools is a java library that provides functionality related to [Kubernetes Custom Resource 
Definitions](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/).
The first planned piece of functionality is to generate source code to java classes that 
corresponds to the types defined in a CustomResourceDefinition objects. Instances of the generated
classes can then be used to create objects which can then be serialised to their yaml 
representation and applied to a kubernetes cluster.

## How to build and develop

This software is built using [bazel](https://bazel.build). The recommended way is to download
and install [bazelisk](https://github.com/bazelbuild/bazelisk) which will download and install
the correct version behind as needed. Once this is installed, the current entrypoint can be 
built and executed by issuing `bazel run //src/main/java/com/gs/crdtools:SourceGenerator` on 
the command line.

The development tooling we use is Intellij Idea Community Edition with the bazel plugin. Please
note that to be able to run the latest intellij, the beta version of the bazel plugin will need
to be installed. This is done by navigating to Preferences -> Plugins then click the cogwheel and
choose "Manage Plugin Repositories". Add https://plugins.jetbrains.com/plugins/beta/list to 
the list of repositories. After this is done, the bazel plugin compatible with recent IntelliJ
versions should be available by clicking "Marketplace" in the Plugins and searching for Bazel.
This has been tested with IntelliJ Idea Community 2022.1.2.

## Credits
The code that this project is based on has been written by Jonathan Perry at
[Goldman Sachs](https://www.gs.com/).
