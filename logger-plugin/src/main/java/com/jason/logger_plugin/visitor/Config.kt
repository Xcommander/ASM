package com.jason.logger_plugin.visitor

import org.objectweb.asm.Opcodes

object Config {

    fun isTraceMethod(
        access: Int?,
        name: String?,
        descriptor: String?,
        signature: String?
    ): Boolean {
        if (Opcodes.ACC_PROTECTED.and(access!!) != 0 && name == "onCreate" && descriptor == "(Landroid/os/Bundle;)V") {
            return true
        }
        return false

    }
}