package com.github.ksoichiro.com.github.ksoichiro.commit.checker

import com.github.ksoichiro.commit.checker.CheckCommitTask
import com.github.ksoichiro.commit.checker.CommitCheckerExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

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

        execute "git init ."
        new File(rootDir, "a.txt").text = """\
            |1
            |2
            |3""".stripMargin().stripIndent()
        execute "git add ."
        execute "git commit -m ''"
        execute "git checkout -b branch1"

        new File(rootDir, "b.txt").text = """\
            |1
            |2
            |3""".stripMargin().stripIndent()
        execute "git add ."
        execute "git commit -m ''"
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
        project.evaluate()
        project.tasks."${CheckCommitTask.NAME}".execute()
    }

    @Test
    void checkCommitOnMainBranch() {
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.extensions."${CommitCheckerExtension.NAME}".with {
            workDir = rootDir
            changedLinesThreshold = 1
        }
        project.evaluate()
        execute "git checkout master"
        project.tasks."${CheckCommitTask.NAME}".execute()
    }

    @Test(expected = GradleException)
    void checkCommitTreatAsBuildError() {
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.extensions."${CommitCheckerExtension.NAME}".with {
            workDir = rootDir
            changedLinesThreshold = 1
            failOnChangesExceedsThreshold = true
        }
        project.evaluate()
        project.tasks."${CheckCommitTask.NAME}".execute()
    }

    def execute(String command) {
        command.execute(null as List, rootDir)
    }
}
