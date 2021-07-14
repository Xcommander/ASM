package com.jason.logger_plugin.visitor.cv

import com.jason.logger_plugin.utils.AccessCodeUtils
import com.jason.logger_plugin.utils.LogUtil
import org.objectweb.asm.ClassVisitor

open class BaseClassInterceptor(api: Int, classVisitor: ClassVisitor?) :
    ClassVisitor(api, classVisitor) {

    var className: String? = ""
    var signature: String? = ""
    var superName: String? = ""
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        LogUtil.info(
            "开始访问【类】，name = $name, superName = $superName, version = $version, access = ${
                AccessCodeUtils.accessCode2String(
                    access
                )
            }"
        )
        this.className = name
        this.signature = signature
        this.superName = superName
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitEnd() {
        super.visitEnd()
        LogUtil.info("访问类结束")
    }
}