package com.github.ksoichiro.commit.checker

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class CheckCommitTask extends DefaultTask {
    public static String NAME = 'checkCommit'
    CommitCheckerExtension extension

    CheckCommitTask() {
        project.afterEvaluate {
            extension = project.extensions."${CommitCheckerExtension.NAME}"
        }
    }

    @TaskAction
    void exec() {
        def mainBranch = extension.mainBranch
        def currentBranch = getCurrentBranch()
        if (currentBranch == mainBranch) {
            // Already in the main branch, nothing to do with it.
            return
        }
        int added = 0
        int deleted = 0
        executeCommand("git diff ${mainBranch} ${currentBranch} --numstat").text.eachLine { line ->
            def columns = line.split("\\t")
            added += columns[0].toInteger()
            deleted += columns[1].toInteger()
        }

        def changes = added + deleted
        if (extension.changedLinesThreshold <= changes) {
            def message = extension.messageForLargeChanges
            if (extension.failOnChangesExceedsThreshold) {
                throw new GradleException(message)
            } else {
                println message
            }
        }
    }

    Process executeCommand(String command) {
        Process process = command.execute(null as List, extension.workDir)
        process.waitFor()
        process
    }

    String getCurrentBranch() {
        executeCommand("git status -b --porcelain")
            .text
            .readLines()
            .find { it.startsWith('##') }
            .trim()
            .substring(3)
            .split('\\.\\.\\.')[0]
    }
}
