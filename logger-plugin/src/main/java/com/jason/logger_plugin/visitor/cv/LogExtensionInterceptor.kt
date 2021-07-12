package com.jason.logger_plugin.visitor.cv

import com.jason.logger_plugin.LogFlags
import com.jason.logger_plugin.utils.LogUtil
import com.jason.logger_plugin.visitor.mv.DeleteLogInterceptor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class LogExtensionInterceptor(api: Int, classVisitor: ClassVisitor?, private val flags: LogFlags?) :
    BaseClassInterceptor(api, classVisitor) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        var methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)

        flags?.run {
            //根据不同flag，替换不同的MethodVisitor
            if (deleteLog) {
                LogUtil.error("DeleteLogInterceptor")
                methodVisitor = DeleteLogInterceptor(methodVisitor, access, name, descriptor)
            }
        }
        return methodVisitor
    }
}