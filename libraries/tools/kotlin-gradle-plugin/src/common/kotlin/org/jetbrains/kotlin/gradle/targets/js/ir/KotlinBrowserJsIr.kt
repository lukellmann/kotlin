/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.js.ir

import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.cli.common.arguments.K2JsArgumentConstants.ES_2015
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.KOTLIN_JS_DCE_TOOL_DEPRECATION_MESSAGE
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.isMain
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDceDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBrowserDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin.Companion.kotlinNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.Companion.kotlinNodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.npmProject
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.js.testing.karma.KotlinKarma
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool
import org.jetbrains.kotlin.gradle.tasks.dependsOn
import org.jetbrains.kotlin.gradle.tasks.locateTask
import org.jetbrains.kotlin.gradle.utils.archivesName
import org.jetbrains.kotlin.gradle.utils.doNotTrackStateCompat
import org.jetbrains.kotlin.gradle.utils.domainObjectSet
import org.jetbrains.kotlin.gradle.utils.relativeOrAbsolute
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
import javax.inject.Inject

abstract class KotlinBrowserJsIr @Inject constructor(target: KotlinJsIrTarget) :
    KotlinJsIrSubTarget(target, "browser"),
    KotlinJsBrowserDsl {

    private val nodeJsRoot = project.rootProject.kotlinNodeJsRootExtension
    private val nodeJs = project.kotlinNodeJsEnvSpec

    private val webpackTaskConfigurations = project.objects.domainObjectSet<Action<KotlinWebpack>>()
    private val runTaskConfigurations = project.objects.domainObjectSet<Action<KotlinWebpack>>()

    override val testTaskDescription: String
        get() = "Run all ${target.name} tests inside browser using karma and webpack"

    override fun configureTestDependencies(test: KotlinJsTest) {
        test.dependsOn(
            nodeJsRoot.npmInstallTaskProvider,
        )
        with(nodeJs) {
            test.dependsOn(project.nodeJsSetupTaskProvider)
        }
        test.dependsOn(nodeJsRoot.packageManagerExtension.map { it.postInstallTasks })
    }

    override fun configureDefaultTestFramework(test: KotlinJsTest) {
        if (test.testFramework == null) {
            test.useKarma {
                useChromeHeadless()
            }
        }

        if (test.enabled) {
            nodeJsRoot.taskRequirements.addTaskRequirements(test)
        }
    }

    override fun commonWebpackConfig(body: Action<KotlinWebpackConfig>) {
        webpackTaskConfigurations.add {
            it.webpackConfigApplier(body)
        }
        runTaskConfigurations.add {
            it.webpackConfigApplier(body)
        }
        testTask {
            it.onTestFrameworkSet {
                if (it is KotlinKarma) {
                    body.execute(it.webpackConfig)
                }
            }
        }
    }

    override fun runTask(body: Action<KotlinWebpack>) {
        runTaskConfigurations.add(body)
    }

    override fun webpackTask(body: Action<KotlinWebpack>) {
        webpackTaskConfigurations.add(body)
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(KOTLIN_JS_DCE_TOOL_DEPRECATION_MESSAGE, level = DeprecationLevel.ERROR)
    @ExperimentalDceDsl
    override fun dceTask(body: Action<@Suppress("DEPRECATION_ERROR") org.jetbrains.kotlin.gradle.dsl.KotlinJsDce>) {
        project.logger.warn(KOTLIN_JS_DCE_TOOL_DEPRECATION_MESSAGE)
    }

    override fun configureRun(
        compilation: KotlinJsIrCompilation,
    ) {
        val commonRunTaskName = disambiguateCamelCased(RUN_TASK_NAME)
        val commonRunTask = project.locateTask<Task>(commonRunTaskName) ?: registerSubTargetTask<Task>(commonRunTaskName) {}

        compilation.binaries
            .matching { it is Executable }
            .all { binary ->
                binary as Executable

                val mode = binary.mode
                val archivesName = project.archivesName

                val runTask = registerSubTargetTask<KotlinWebpack>(
                    disambiguateCamelCased(
                        binary.executeTaskBaseName,
                        RUN_TASK_NAME
                    ),
                    listOf(compilation)
                ) { task ->
                    task.dependsOn(binary.linkSyncTask)

                    task.args.add(0, "serve")
                    task.description = "start ${mode.name.toLowerCaseAsciiOnly()} webpack dev server"

                    val npmProject = compilation.npmProject
                    val resourcesDir = compilation.output.resourcesDir
                    task.devServerProperty.convention(
                        npmProject.dist.zip(npmProject.dir) { distDirectory, dir ->
                            KotlinWebpackConfig.DevServer(
                                open = true,
                                static = mutableListOf(
                                    distDirectory.asFile.normalize().relativeOrAbsolute(dir.asFile),
                                    resourcesDir.relativeOrAbsolute(dir.asFile),
                                ),
                                client = KotlinWebpackConfig.DevServer.Client(
                                    KotlinWebpackConfig.DevServer.Client.Overlay(
                                        errors = true,
                                        warnings = false
                                    )
                                )
                            )
                        }
                    )

                    task.watchOptions = KotlinWebpackConfig.WatchOptions(
                        ignored = arrayOf("*.kt")
                    )


                    task.doNotTrackStateCompat("Tracked by external webpack tool")

                    task.dependsOn(binary.linkSyncTask)

                    task.commonConfigure(
                        binary = binary,
                        mode = mode,
                        inputFilesDirectory = task.project.objects.directoryProperty().fileProvider(
                            task.project.provider { binary.linkSyncTask.get().destinationDirectory.get() },
                        ),
                        entryModuleName = binary.linkTask.flatMap { it.compilerOptions.moduleName },
                        configurationActions = runTaskConfigurations,
                        nodeJs = nodeJsRoot,
                        defaultArchivesName = archivesName,
                    )
                }

                if (mode == KotlinJsBinaryMode.DEVELOPMENT && compilation.isMain()) {
                    target.runTask.dependsOn(runTask)
                    commonRunTask.dependsOn(runTask)
                }
            }
    }

    override fun configureBuild(
        compilation: KotlinJsIrCompilation,
    ) {
        val project = compilation.target.project

        val processResourcesTask = target.project.tasks.named(compilation.processResourcesTaskName)

        val assembleTaskProvider = project.tasks.named(LifecycleBasePlugin.ASSEMBLE_TASK_NAME)

        compilation.binaries
            .matching { it is Executable }
            .all { binary ->
                binary as Executable

                val mode = binary.mode
                val archivesName = project.archivesName

                val webpackTask = registerSubTargetTask<KotlinWebpack>(
                    disambiguateCamelCased(
                        binary.executeTaskBaseName,
                        WEBPACK_TASK_NAME
                    ),
                    listOf(compilation)
                ) { task ->
                    task.description = "build webpack ${mode.name.toLowerCaseAsciiOnly()} bundle"
                    val buildDirectory = project.layout.buildDirectory
                    val targetName = target.name
                    task.outputDirectory.convention(
                        binary.distribution.distributionName.flatMap {
                            buildDirectory.dir("kotlin-webpack/$targetName/$it")
                        }
                    ).finalizeValueOnRead()

                    task.dependsOn(binary.linkSyncTask)

                    task.commonConfigure(
                        binary = binary,
                        mode = mode,
                        inputFilesDirectory = task.project.objects.directoryProperty().fileProvider(
                            task.project.provider { binary.linkSyncTask.get().destinationDirectory.get() },
                        ),
                        entryModuleName = binary.linkTask.flatMap { it.compilerOptions.moduleName },
                        configurationActions = webpackTaskConfigurations,
                        nodeJs = nodeJsRoot,
                        defaultArchivesName = archivesName,
                    )
                }

                val distributionTask = registerSubTargetTask<Copy>(
                    disambiguateCamelCased(
                        if (
                            binary.mode == KotlinJsBinaryMode.PRODUCTION &&
                            binary.compilation.isMain()
                        ) "" else binary.name,
                        DISTRIBUTION_TASK_NAME
                    )
                ) { copy ->
                    copy.from(processResourcesTask)
                    copy.from(webpackTask.flatMap { it.outputDirectory })

                    copy.into(binary.distribution.outputDirectory)
                }

                if (mode == KotlinJsBinaryMode.PRODUCTION && compilation.isMain()) {
                    assembleTaskProvider.dependsOn(distributionTask)
                    registerSubTargetTask<Task>(disambiguateCamelCased(WEBPACK_TASK_NAME)) {
                        it.dependsOn(webpackTask)
                    }
                }
            }
    }

    private fun KotlinWebpack.commonConfigure(
        binary: JsIrBinary,
        mode: KotlinJsBinaryMode,
        inputFilesDirectory: Provider<Directory>,
        entryModuleName: Provider<String>,
        configurationActions: DomainObjectSet<Action<KotlinWebpack>>,
        nodeJs: NodeJsRootExtension,
        defaultArchivesName: Property<String>,
    ) {
        dependsOn(
            nodeJs.npmInstallTaskProvider,
            target.project.tasks.named(compilation.processResourcesTaskName)
        )

        dependsOn(nodeJs.packageManagerExtension.map { it.postInstallTasks })

        configureOptimization(mode)

        this.inputFilesDirectory.set(inputFilesDirectory)

        val platformType = binary.compilation.platformType
        val moduleKind = binary.linkTask.flatMap { task ->
            task.compilerOptions.moduleKind.orElse(task.compilerOptions.target.map {
                if (it == ES_2015) JsModuleKind.MODULE_ES else JsModuleKind.MODULE_UMD
            })
        }

        this.entryModuleName.set(entryModuleName)
        this.esModules.convention(
            project.provider {
                platformType == KotlinPlatformType.wasm || moduleKind.get() == JsModuleKind.MODULE_ES
            }
        ).finalizeValueOnRead()

        mainOutputFileName.convention(defaultArchivesName.orElse("main").map { "$it.js" }).finalizeValueOnRead()

        configurationActions.all { configure ->
            configure.execute(this)
        }
    }

    private fun KotlinWebpack.configureOptimization(mode: KotlinJsBinaryMode) {
        this.mode = getByKind(
            kind = mode,
            releaseValue = Mode.PRODUCTION,
            debugValue = Mode.DEVELOPMENT
        )

        devtool = getByKind(
            kind = mode,
            releaseValue = WebpackDevtool.SOURCE_MAP,
            debugValue = WebpackDevtool.EVAL_SOURCE_MAP
        )
    }

    private fun <T> getByKind(
        kind: KotlinJsBinaryMode,
        releaseValue: T,
        debugValue: T,
    ): T = when (kind) {
        KotlinJsBinaryMode.PRODUCTION -> releaseValue
        KotlinJsBinaryMode.DEVELOPMENT -> debugValue
    }

    companion object {
        private const val WEBPACK_TASK_NAME = "webpack"
    }
}