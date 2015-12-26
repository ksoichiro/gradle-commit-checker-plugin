package com.github.ksoichiro.commit.checker

class CommitCheckerExtension {
    public static final NAME = "commitChecker"
    String mainBranch = 'master'
    int changedLinesThreshold = 1000
    boolean failOnChangesExceedsThreshold = false
    String messageForLargeChanges = "Your branch includes too much changes. Please check if those changes are not mistake but intentional. If your branch includes multiple features, consider separate them into multiple branches / pull requests."
    File workDir
}
