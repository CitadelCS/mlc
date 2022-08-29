# Overview

This repository comprises the research done at the Citadel for the Minimum Length Corridor problem. At the time of the writing, it has largely been abandoned since 2019. The renewed efforts for this repository are to accomplish the following objectives:

* Create a working application for the initial research and algorithms
* Document the research efforts in an organized manner for the possibility of others interest in the MLC problem
* Create a development environment similar to industry
* Provide a place for cadets to learn useful libraries, build systems, and a new language (C++) beyond academia and course projects
* Stretch goal of a multi-language environment for cadets to develop and write code that interacts with multiple languages
* Ultimately, contribute to the space in a meaningful manner

## Development Environment

As a general note, the modification of an individual's development environment should be minimum. At most, the only alterations should be making available a C++ compiler on Windows via Visual Studios (msvc). Ubuntu should have a gcc compiler with the base installation. If not, the following command should work: `sudo apt install gcc`.

The initial efforts to create working code of the problem were in Java. Unfortunately, the details of the environment and build system are not documented, so this repository will move to a new language of choice, C++, with Bazel as the build system. Using Bazel will allow cadets to choose from a multitude of languages supported by Bazel as it is designed to handle multiple languages.

Third-party libraries may be used to handle the representation of nodes, but every effort will be to provide as much as source code without reinventing the wheel or introducing libraries with steep learning curves. Thus, the primary objective will be to keep the learning curve minimal for the libraries and build system. The majority of the effort can be freed for the research, algorithms, data structures, and software architecture. 

The following will get you started:

Linux Build: `./bazelisk-linux-amd64 build //...`
Linux Run: `./bazelisk-linux-amd64 run :main`
Linux Test: `./bazelisk-linux-amd64 test //...`

To learn more about Bazel, refer to the `BUILD.bazel` files for examples or `https://bazel.build`.

## Additional Notes

The code will be tested using a popular library for C++: `gtest`. All of the dependencies are handled via Bazel. Any test code will reside in folders marked as `Unit_Tests`. 
