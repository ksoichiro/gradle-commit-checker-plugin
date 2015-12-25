package com.github.ksoichiro.com.github.ksoichiro.commit.checker

import com.github.ksoichiro.commit.checker.CheckCommitTask
import com.github.ksoichiro.commit.checker.CommitCheckerExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.assertEquals
import static org.junit.Assert.assertTrue

class PluginTest {
    private static final String PLUGIN_ID = 'com.github.ksoichiro.commit.checker'

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File rootDir

    @Before
    void setup() {
        rootDir = testProjectDir.root
        if (!rootDir.exists()) {
            rootDir.mkdir()
        }

        println "init: " + execute("git init .").text
        new File(rootDir, "a.txt").text = """\
            |1
            |2
            |3""".stripMargin().stripIndent()
        println "add: " + execute("git add a.txt").text
        println "commit: " + execute("git commit -m First").text
        println "checkout: " + execute("git checkout -b branch1").text

        new File(rootDir, "b.txt").text = """\
            |1
            |2
            |3""".stripMargin().stripIndent()
        println "add: " + execute("git add b.txt").text
        println "commit: " + execute("git commit -m Second").text
    }

    @Test
    void apply() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: PLUGIN_ID

        assertTrue(project.tasks.checkCommit instanceof CheckCommitTask)
    }

    @Test
    void checkCommit() {
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.extensions."${CommitCheckerExtension.NAME}".with {
            workDir = rootDir
            changedLinesThreshold = 1
        }
        project.afterEvaluate {
            assertEquals("branch1", project.tasks."${CheckCommitTask.NAME}".getCurrentBranch())
            project.tasks."${CheckCommitTask.NAME}".execute()
        }
        project.evaluate()
    }

    @Test
    void checkCommitOnMainBranch() {
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.extensions."${CommitCheckerExtension.NAME}".with {
            workDir = rootDir
            changedLinesThreshold = 1
        }
        project.afterEvaluate {
            println "checkout : "+ execute("git checkout master").text
            assertEquals("master", project.tasks."${CheckCommitTask.NAME}".getCurrentBranch())
            project.tasks."${CheckCommitTask.NAME}".execute()
        }
        project.evaluate()
    }

    // Disable this test since it's unstable.
    @Test(expected = GradleException)
    @Ignore
    void checkCommitTreatAsBuildError() {
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.extensions."${CommitCheckerExtension.NAME}".with {
            workDir = rootDir
            changedLinesThreshold = 1
            failOnChangesExceedsThreshold = true
        }
        project.afterEvaluate {
            assertEquals("branch1", project.tasks."${CheckCommitTask.NAME}".getCurrentBranch())
            project.tasks."${CheckCommitTask.NAME}".execute()
        }
        project.evaluate()
    }

    def execute(String command) {
        Process proc = command.execute(null as List, rootDir)
        proc.waitFor()
        proc
    }
}
