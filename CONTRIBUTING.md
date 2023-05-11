# Shapeshifter Library contributing guide

Thank you for investing your time in contributing to our project!

## Table of contents
1. [Reporting bugs](#reporting-bugs)
2. [Requesting features](#requesting-features)
3. [Contributing code](#contributing-code)
4. [Releasing new versions](#releasing-new-versions)
5. [Code of conduct](#code-of-conduct)

## Reporting bugs

To be described.

## Requesting features

To be described.

## Contributing code

To be described.

## Releasing new versions

We use [semantic versioning](https://semver.org).

Given a version number MAJOR.MINOR.PATCH, increment the:
 - MAJOR version when you make incompatible API changes
 - MINOR version when you add functionality in a backward compatible manner
 - PATCH version when you make backward compatible bug/security fixes

### Major or minor release
Whatever the version in the `pom.xml` is, that is the version that will be released.

For example if the version in the pom.xml is `1.0.0-SNAPSHOT`, then the released version will be `1.0.0`:
1. Trigger the `release` workflow on the `main` branch.
2. This will build and upload version `1.0.0` to the Maven repository and will set the next development version on the branch to `1.1.0-SNAPSHOT`.

By default, the minor version will be incremented. If you want to release or start work on a new major version, you will have to update the `pom.xml` to the new major version (appending `-SNAPSHOT`) before triggering the workflow.

### Security / bug fix release
Let's assume the last release was `1.0.0` and you want to make a security or bug fix on it.

1. Create a new branch from the tag `1.0.0`, e.g. `release-1.0` (name can be anything).
2. Update the version in the pom.xml to `1.0.1-SNAPSHOT`.
3. Make your changes and commit them.
4. Trigger the `release` workflow on the `release-1.0` branch.
5. This will build and upload version `1.0.1` to the Maven repository and will set the next development version on the branch to `1.0.2-SNAPSHOT`.
6. Merge the branch `release-1.0` into `main`.

You can keep the branch open to make more fixes on the `1.0.x` line, or you can delete the branch when no longer needed.

This can be done for any previous release (including patch versions).

## Code of conduct

To be described.