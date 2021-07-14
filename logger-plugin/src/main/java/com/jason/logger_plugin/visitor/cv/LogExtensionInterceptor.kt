package com.jason.logger_plugin.visitor.cv

import com.jason.logger_plugin.LogFlags
import com.jason.logger_plugin.utils.LogUtil
import com.jason.logger_plugin.visitor.Config
import com.jason.logger_plugin.visitor.mv.DeleteLogInterceptor
import com.jason.logger_plugin.visitor.mv.PrintLogInterceptor
import com.jason.logger_plugin.visitor.mv.TraceStartInterceptor
import com.jason.logger_plugin.visitor.mv.TryCatchInterceptor
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
                //删除日志
                LogUtil.error("DeleteLogInterceptor")
                methodVisitor = DeleteLogInterceptor(methodVisitor, access, name, descriptor)
            }
            if (methodTrace && Config.isTraceMethod(access, name, descriptor, signature)) {
                //统计耗时
                LogUtil.error("TraceStartInterceptor")
                methodVisitor = TraceStartInterceptor(methodVisitor, access, name, descriptor)
            }

            if (printLog && Config.isActivityMethod(className)) {
                //打印方法名
                LogUtil.error("PrintLogInterceptor")
                methodVisitor = PrintLogInterceptor(methodVisitor, access, name, descriptor)
            }
            if (tryCatch && Config.isTryCatchMethod(className, name)) {
                //插入try-catch块
                LogUtil.error("PrintLogInterceptor")
                methodVisitor = TryCatchInterceptor(methodVisitor, access, name, descriptor)
            }
        }
        return methodVisitor
    }
}