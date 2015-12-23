package com.github.ksoichiro.commit.checker

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class CheckCommitTask extends DefaultTask {
    public static String NAME = 'checkCommit'

    @TaskAction
    void exec() {
        // TODO main branch to compare should be configurable by extension
        def mainBranch = 'master'
        def currentBranch = "git status -b --porcelain"
            .execute()
            .text
            .readLines()
            .find { it.startsWith('##') }
            .trim()
            .substring(3)
            .split('\\.\\.\\.')[0]
        if (currentBranch == mainBranch) {
            // Already in the main branch, nothing to do with it.
            return
        }
        int added = 0
        int deleted = 0
        "git diff ${mainBranch} ${currentBranch} --numstat".execute().text.eachLine { line ->
            def columns = line.split("\\t")
            added += columns[0].toInteger()
            deleted += columns[1].toInteger()
        }
        // TODO threshold should be configurable by extension
        def threshold = 10
        def changes = added + deleted
        boolean failOnChangesExceedsThreshold = true
        if (threshold <= changes) {
            def message = "Your branch includes too much changes. Please check if those changes are not mistake but intentional. If your branch includes multiple features, consider separate them into multiple branch / pull requests."
            if (failOnChangesExceedsThreshold) {
                throw new GradleException(message)
            } else {
                println message
            }
        }
    }
}
