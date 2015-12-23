# git-commit-checker-plugin

[![Build Status](https://travis-ci.org/ksoichiro/git-commit-checker-plugin.svg?branch=master)](https://travis-ci.org/ksoichiro/git-commit-checker-plugin)
[![Build status](https://ci.appveyor.com/api/projects/status/rom2rsf2j56f01bs?svg=true)](https://ci.appveyor.com/project/ksoichiro/git-commit-checker-plugin)

Gradle plugin to check commits in a branch to avoid large diff in a pull requests.

```
$ ./gradlew checkCommit
:checkCommit FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':plugin:checkCommit'.
> Your branch includes too much changes. Please check if those changes are not mistake but intentional. If your branch includes multiple features, consider separate them into multiple branches / pull requests.

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.

BUILD FAILED

Total time: 11.791 secs
```

## Usage

Apply plugin:

```gradle
plugins {
    id 'com.github.ksoichiro.commit.checker' version '0.1.0'
}
```

Execute check:

```
./gradlew checkCommit
```

> Note that this plugin uses git command.  
Please install git wherever you execute this task.

If you want to run it on each 'check's, set it to dependent task:

```gradle
check.dependsOn 'checkCommit'
```

## Configuration

```gradle
commitChecker {
    // Change this property to the main branch of your project.
    // Default is 'master'.
    mainBranch 'develop'

    // Change this property to the number that is max size you can allow for a pull request size.
    // Default is 1000.
    changedLinesThreshold 100

    // Change this property to true if you want to make the violations to build error.
    // Default is false. (Just showing a message)
    failOnChangesExceedsThreshold true

    // Change this property to whatever you want to show if there are any violations.
    messageForLargeChanges "Too large changes!"
}
```

## License

    Copyright 2015 Soichiro Kashima

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
