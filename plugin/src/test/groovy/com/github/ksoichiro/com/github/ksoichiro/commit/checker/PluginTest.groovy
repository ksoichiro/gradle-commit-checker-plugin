package com.github.ksoichiro.com.github.ksoichiro.commit.checker

import com.github.ksoichiro.commit.checker.CheckCommitTask
import com.github.ksoichiro.commit.checker.CommitCheckerExtension
import org.ajoberstar.grgit.Grgit
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

        execute("git init .")
        new File(rootDir, "a.txt").text = """\
            |1
            |2
            |3""".stripMargin().stripIndent()
        def grgit = Grgit.init(dir: rootDir.path)
        grgit.add(patterns: ['a.txt'])
        grgit.commit(message: 'Initial commit.')

        execute("git checkout -b branch1")

        new File(rootDir, "b.txt").text = """\
            |1
            |2
            |3""".stripMargin().stripIndent()
        grgit.add(patterns: ['b.txt'])
        grgit.commit(message: 'Second commit.')
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
            execute("git checkout master")
            assertEquals("master", project.tasks."${CheckCommitTask.NAME}".getCurrentBranch())
            project.tasks."${CheckCommitTask.NAME}".execute()
        }
        project.evaluate()
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
