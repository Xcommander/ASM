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

    fun isActivityMethod(className: String?): Boolean {
        className?.run {
            if (this.endsWith("Activity")) {
                return true
            }
        }
        return false
    }

    fun isTryCatchMethod(className: String?, name: String?): Boolean {
        className?.run {
            if (this.endsWith("Activity") && "testString" == (name)) {
                return true
            }
        }
        return false
    }

}