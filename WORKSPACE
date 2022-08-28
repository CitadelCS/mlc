workspace(name="com_citadel_mlc")

# Mostly up-to-date gtest library
http_archive(
    name = "com_google_googletest",
    urls = ["https://github.com/google/googletest/archive/16f637fbf4ffc3f7a01fa4eceb7906634565242f.zip"],
    strip_prefix = "googletest-16f637fbf4ffc3f7a01fa4eceb7906634565242f",
    sha256 = "002d540f947e5981f54ddaab476d87b113d2a14822f21a34dca30f24c9492a24",
    )

# Mostly up-to-date protobuf library
http_archive(
    name = "com_google_protobuf",
    sha256 = "3bd7828aa5af4b13b99c191e8b1e884ebfa9ad371b0ce264605d347f135d2568",
    urls = ["https://github.com/protocolbuffers/protobuf/archive/v3.19.4.tar.gz"],
    strip_prefix = "protobuf-3.19.4",
    )

load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")
protobuf_deps()

