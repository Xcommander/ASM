package com.jason.logger_plugin.visitor.mv

import com.jason.logger_plugin.PluginConstant
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class PrintLogInterceptor(
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(PluginConstant.ASM_VERSION, methodVisitor, access, name, descriptor) {


    override fun onMethodEnter() {
        super.onMethodEnter()
        mv.visitLdcInsn(name)
        mv.visitVarInsn(ASTORE, 2)
        mv.visitLdcInsn("xulinchao")
        mv.visitLdcInsn("\u65b9\u6cd5\u540d\u4e3a")
        mv.visitVarInsn(ALOAD, 2)
        mv.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "stringPlus",
            "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;",
            false
        )
        mv.visitMethodInsn(
            INVOKESTATIC,
            "android/util/Log",
            "e",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        )
        mv.visitInsn(POP)
    }

}