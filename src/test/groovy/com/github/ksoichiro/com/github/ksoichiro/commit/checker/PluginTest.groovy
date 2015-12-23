package com.github.ksoichiro.com.github.ksoichiro.commit.checker

import com.github.ksoichiro.commit.checker.CheckCommitTask
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
    }

    @Test
    void apply() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: PLUGIN_ID

        assertTrue(project.tasks.checkCommit instanceof CheckCommitTask)
    }
}
