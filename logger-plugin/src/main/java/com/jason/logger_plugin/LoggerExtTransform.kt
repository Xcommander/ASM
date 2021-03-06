package com.jason.logger_plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.jason.logger_plugin.utils.LogUtil
import com.jason.logger_plugin.utils.TransformHelper


class LoggerExtTransform(private val logFlags: LogFlags) : Transform() {
    override fun getName(): String {
        //自定义transform名字，也就是编译时候，task的名字
        return PluginConstant.PLUGIN_NAME
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        //输入文件的类型
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        //输入文件的作用范围
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        //是否支持增量编译
        return true
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        beforeTransform()
        transformInvocation?.run {
            //清理输出
            outputProvider.deleteAll()
            //循环处理输入
            for (input in inputs) {
                //输入分为两种，一种是jar输入
                for (jarInput in input.jarInputs) {
                    TransformHelper.transformJars(jarInput, outputProvider, isIncremental)
                }

                //一种是目录输入
                for (directory in input.directoryInputs) {
                    TransformHelper.transformDirectory(directory, outputProvider, isIncremental)

                }
            }
        }

        afterTransform()
    }


    private fun beforeTransform() {
        LogUtil.info("start")
        TransformHelper.logFlags = logFlags
    }

    private fun afterTransform() {}

}