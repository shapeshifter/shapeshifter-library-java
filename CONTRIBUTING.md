# Shapeshifter Library contributing guide

Thank you for investing your time in contributing to our project!

## Table of contents
1. [Reporting bugs](#reporting-bugs)
2. [Requesting features](#requesting-features)
3. [Contributing code](#contributing-code)
4. [Releasing new versions](#releasing-new-versions)

## Reporting bugs

Please [create a new issue on GitHub](https://github.com/shapeshifter/shapeshifter-library/issues), pick type 'Bug report' and describe the bug in detail.
If possible, please provide a minimal (code) example that reproduces the bug.

Bug reports will be triaged and assigned a priority by the maintainers.

## Requesting features

Please [create a new issue on GitHub](https://github.com/shapeshifter/shapeshifter-library/issues), pick type 'Feature request' and describe the feature in detail.

Feature requests will be considered and assigned a priority by the maintainers.

## Contributing code

### Prerequisites
- JDK >=17
- Maven >=3.8

### Creating a fork
To contribute code, you will need to create a fork of the repository on GitHub.

1. Create a fork of the repository on GitHub.
2. Clone your fork to your local machine.
3. Create a new branch for your changes.
4. Make and verify your changes.
5. Commit and push your changes to your fork on GitHub.
   - When committing you must sign-off your commit. If you are unfamiliar on how to do this, Google is your friend.

### Building the source code
To build the source code, run the following command in the root directory of the project:
```shell
mvn install
```
This will build the source code and run all tests.

### Opening a pull request
When you are done making your changes, you can open a pull request on GitHub.

If your pull request fixes an issue, please reference the issue in the pull request description.

Pull requests will be reviewed by the maintainers.

## Releasing new versions

We use [semantic versioning](https://semver.org).

Given a version number MAJOR.MINOR.PATCH, increment the:
 - MAJOR version when you make incompatible API changes
 - MINOR version when you add functionality in a backward compatible manner
 - PATCH version when you make backward compatible bug/security fixes

We use GitHub Actions to automate the release process. The release process is as follows:
1. The release workflow will build the project, run all tests and then create the artifacts.
2. The release workflow will sign the artifacts and upload them to the Maven central repository.
3. The release workflow will wait until staging checks are complete.
2. The release workflow will update the `pom.xml` with the next development version and push the changes to the branch.
3. The release workflow will tag the release in the repository.

### Releasing a Major or minor release
Whatever the version in the `pom.xml` is, that is the version that will be released.
For example if the version in the pom.xml is `1.0.0-SNAPSHOT`, then the released version will be `1.0.0`.

1. Click `Run workflow` for the [Release workflow](https://github.com/shapeshifter/shapeshifter-library/actions/workflows/release.yml). Select the `main` branch.
2. This will build and upload version `x.x.0` to the Maven repository and will set the next development version on the branch to `x.x.0-SNAPSHOT`.

By default, the minor version will be incremented. 

If you want to release or start work on a new major version, you can do the following :
1. Select the `Actions` tab in the `shapeshifter-library-java` project.
2. Click the `Release` workflow in the left hand menu.
3. Click on the `Run workflow` button
4. This will open a small popup window; first select the branch from which you want to create a release; usually this will be the `main` branch.
5. In the popup enter the release version, and the next development version
6. Click the `Run workflow` button; this will create the artefacts, update the `pom.xml` with the new development version, and upload it all to Maven central.

Alternatively, you can manually update the `pom.xml` to a new major version with the following command:
```shell
mvn versions:set -DnewVersion=x.0.0-SHAPSHOT
```

### Security / bug fix release
Let's assume the last release was `1.0.0` and you want to make a security or bug fix on it.

1. Create a new branch from the tag `v1.0.0`, name it `v1.0-maintenance`.
2. Manually update the version in the `pom.xml`'s to `1.0.1-SNAPSHOT` using this command: `mvn versions:set -DnewVersion=1.0.1-SNAPSHOT`
3. Commit the modified `pom.xml`'s.
4. Make your security or bug fix changes on your own (forked) branch.
5. Create a pull request for your own branch into the `v1.0-maintenance` branch.
6. After the pull request is merged, click `Run workflow` for the [Release workflow](https://github.com/shapeshifter/shapeshifter-library/actions/workflows/release.yml). Select the `v1.0-maintenance` branch. Set "Development Version" to `1.0.2-SNAPSHOT`.
7. This will build and upload version `1.0.1` to the Maven repository and will set the next development version on the branch to `1.0.2-SNAPSHOT`.
8. Make sure to cherry pick your fixes to the `main` branch as well.

You can keep the `v1.0-maintenance` branch open to make more bug/security fixes, or you can delete the branch when no longer needed.

This can be done for any previous release (including patch versions).