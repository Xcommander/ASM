package com.jason.logger_plugin.visitor.mv

import com.jason.logger_plugin.PluginConstant
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

//如果要修改方法的字节码，必须是一个MethodVisitor，这样才能访问方法的字节码。其次是一个AOP中的切入点和通知
class DeleteLogInterceptor(
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(PluginConstant.ASM_VERSION, methodVisitor, access, name, descriptor) {
    //我们需求是删除方法中的Log的调用，也就是我们访问的是字节码中的方法。所以是visitMethodInsn()

    /**
     * 访问方法中的字节码，然后修改。
     * @param opcodeAndSource:操作码修饰符
     * @param owner:操作码的类名
     * @param name:操作码的方法名
     * @param descriptor:方法的描述
     * @param isInterface:是否是接口
     */
    override fun visitMethodInsn(
        opcodeAndSource: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        /**
         * 扫描方法中的每一行操作码，日志的调用形式为Log.v(String tag,String message);
         * 从上可知：
         * 1、因为是调用的方法是static方法，所以opcodeAndSource="static"
         * 2、调用的类为Log类，所以owner="android/util/Log"
         * 3、调用的方法名："v"，"d"，"e"，"w"，"i"
         * 4、方法的描述，例如(String tag,String message)：(Ljava/lang/String;Ljava/lang/String;)I
         * 5、是否是接口，就不是我们关心的
         */
        if (Opcodes.ACC_STATIC.and(opcodeAndSource) != 0 && owner == "android/util/Log" && (
                    name == "v" || name == "d" || name == "i" || name == "e" || name == "w"
                    ) && descriptor == "(Ljava/lang/String;Ljava/lang/String;)I"
        ) {//条件均符合
            //直接return，这样就相当于删除了这样操作码，就不会被保存到methodVisitor中，无法被输出
            return
        }

        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
    }

}