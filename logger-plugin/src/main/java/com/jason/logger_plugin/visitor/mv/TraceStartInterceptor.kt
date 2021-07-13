package com.jason.logger_plugin.visitor.mv

import com.jason.logger_plugin.PluginConstant
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class TraceStartInterceptor(
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(PluginConstant.ASM_VERSION, methodVisitor, access, name, descriptor) {
    //方法开头
    override fun onMethodEnter() {
        super.onMethodEnter()
        //开头插入代码
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
        mv.visitVarInsn(LSTORE, 2)
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        //结尾插入代码
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
        mv.visitVarInsn(LSTORE, 4)
        mv.visitLdcInsn("xulinchao")
        mv.visitLdcInsn("\u542f\u52a8\u65f6\u95f4\u4e3a\uff1a")
        mv.visitVarInsn(LLOAD, 4)
        mv.visitVarInsn(LLOAD, 2)
        mv.visitInsn(LSUB)
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false)
        mv.visitMethodInsn(INVOKESTATIC, "kotlin/jvm/internal/Intrinsics", "stringPlus", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;", false)
        mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false)
        mv.visitInsn(POP)
    }
}