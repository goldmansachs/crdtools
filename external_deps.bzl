load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_file")
load("//:expanded_external_deps.bzl", "all")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository", "new_git_repository")
load("@bazel_tools//tools/jdk:remote_java_repository.bzl", "remote_java_repository")

def _java(expanded):
    http_file(
        name = "jre-runtime-tarball",
        downloaded_file_path = expanded["jre"]["archive_name"] + ".tar.gz",
        sha256 = expanded["jre"]["sha256"],
        urls = [expanded["jre"]["url"]],
    )

    http_file(
        name = "jdk-runtime-tarball",
        downloaded_file_path = expanded["jdk"]["archive_name"] + ".tar.gz",
        sha256 = expanded["jdk"]["sha256"],
        urls = [expanded["jdk"]["url"]],
    )

    # And a version of exactly the same thing for the build's runtime
    http_archive(
        name = "jdk-build-archive",
        build_file_content = "java_runtime(name = 'runtime', srcs =  glob(['**']), visibility = ['//visibility:public'])",
        sha256 = expanded["jdk"]["sha256"],
        strip_prefix = expanded["jdk"]["archive_name"],
        urls = [expanded["jdk"]["url"]],
    )

    # Actual runtime for build
    remote_java_repository(
        name = "javac-archive",
        version = expanded["jdk"]["major_version"],
        sha256 = expanded["jdk"]["sha256"],
        exec_compatible_with = [
            "@platforms//os:linux",
            "@platforms//cpu:x86_64",
        ],
        strip_prefix = expanded["jdk"]["archive_name"],
        urls = [expanded["jdk"]["url"]],
    )

def _gcs_http_repository(name, build_file_content, sha256, https_url):
    if len(build_file_content) > 0:
        http_archive(
            name = name,
            build_file_content = build_file_content,
            sha256 = sha256,
            urls = [https_url],
        )
    else:
        http_archive(
            name = name,
            sha256 = sha256,
            urls = [https_url],
        )

def _github_archive(name, sha256, urls, kwargs, build_file_content):
    http_archive(
        name = name,
        urls = urls,
        sha256 = sha256,
        build_file_content = None if "" == build_file_content else build_file_content,
        **kwargs
    )

def call_all(func, expanded):
    [
        func(**v["expansion"])
        for v in expanded
    ]

def external_deps():
    _java(all["java"])
    call_all(git_repository, all["git_repositories"].values())
    call_all(new_git_repository, all["new_git_repositories"].values())
    call_all(_gcs_http_repository, all["gcs_expansions"].values())
    call_all(_github_archive, all["github_archives"].values())

def all_external_deps_names_and_purls():
    return [{
        "target": ex["expansion"]["name"],
        "purl": ex["purl"],
    } for ex in all["git_repositories"].values() + all["new_git_repositories"].values() + all["container_pulls"].values()]
