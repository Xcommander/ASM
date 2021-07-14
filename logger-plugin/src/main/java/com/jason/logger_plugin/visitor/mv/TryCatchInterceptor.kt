package com.jason.logger_plugin.visitor.mv

import com.jason.logger_plugin.PluginConstant
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class TryCatchInterceptor(
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    var descriptor: String?
) : AdviceAdapter(PluginConstant.ASM_VERSION, methodVisitor, access, name, descriptor) {
    //开始位置
    private val labelStart = Label()

    //结束位置
    private val labelEnd = Label()

    //捕获Exception开始位置
    private val labelTarget = Label()
    override fun onMethodEnter() {
        // 定义开始位置
        mv.visitLabel(labelStart)
        //在方法进入的时候，开始插入try-catch块（使用visitTryCatchBlock方法生成）
        mv.visitTryCatchBlock(labelStart, labelEnd, labelTarget, "java/lang/Exception")
    }

    //访问方法的操作数栈和局部变量表最大值，在这个里面，我们去添加catch代码块里面的内容
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        //定位到正常代码结束位置
        mv.visitLabel(labelEnd)
        //定位到catch开始的位置
        mv.visitLabel(labelTarget)
        //下面添加catch Exception具体逻辑
        val local1 = newLocal(Type.getType("Ljava/lang/Exception"))
        mv.visitVarInsn(Opcodes.ASTORE, local1)
        mv.visitVarInsn(Opcodes.ALOAD, local1)
        // 输出 ex.printStackTrace
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Exception",
            "printStackTrace",
            "()V",
            false
        )
        //判断方法的返回类型
        mv.visitInsn(getReturnCode(descriptor = descriptor))
        super.visitMaxs(maxStack, maxLocals)
    }
    /**
     * 获取对应的返回值
     */
    private fun getReturnCode(descriptor: String?): Int {
        return when (descriptor!!.subSequence(descriptor.indexOf(")") + 1, descriptor.length)) {
            "V" -> Opcodes.RETURN
            "I", "Z", "B", "C", "S" -> {
                mv.visitInsn(Opcodes.ICONST_0)
                Opcodes.IRETURN
            }
            "D" -> {
                mv.visitInsn(Opcodes.DCONST_0)
                Opcodes.DRETURN
            }
            "J" -> {
                mv.visitInsn(Opcodes.LCONST_0)
                Opcodes.LRETURN
            }
            "F" -> {
                mv.visitInsn(Opcodes.FCONST_0)
                Opcodes.FRETURN
            }
            else -> {
                mv.visitInsn(Opcodes.ACONST_NULL)
                Opcodes.ARETURN
            }
        }
    }

}