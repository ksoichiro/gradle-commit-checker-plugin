package com.github.ksoichiro.commit.checker

import org.gradle.api.Plugin
import org.gradle.api.Project

class CommitCheckerPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.extensions.create(CommitCheckerExtension.NAME, CommitCheckerExtension)
        target.tasks.create(CheckCommitTask.NAME, CheckCommitTask)
    }
}
