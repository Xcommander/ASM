package com.jason.logger_plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class LoggerPlugin : Plugin<Project> {
    override fun apply(p0: Project) {

        //注册Transform
        val appExtension = p0.extensions.findByType(AppExtension::class.java)
        val logFlags = p0.extensions.create("LogFlags", LogFlags::class.java)
        appExtension?.registerTransform(LoggerExtTransform(logFlags))
    }
}