package com.jason.logger_plugin.utils

import com.android.build.api.transform.*
import com.android.utils.FileUtils
import com.jason.logger_plugin.LogFlags
import com.jason.logger_plugin.PluginConstant
import com.jason.logger_plugin.visitor.cv.LogExtensionInterceptor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

internal object TransformHelper {
    // 配置信息
    var logFlags: LogFlags? = null

    /**
     * 处理jar包输入
     * jarInput：jar包的输入
     * outputProvider：负责创建输出
     * isIncremental：是否支持增量编译
     */
    fun transformJars(
        jarInput: JarInput,
        outputProvider: TransformOutputProvider,
        isIncremental: Boolean
    ) {
        val jarName = jarInput.name
        val status = jarInput.status
        val destFile = outputProvider.getContentLocation(
            jarName,
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )

        LogUtil.info("TransformHelper[transformJars], jar = $jarName, status = $status, isIncremental = $isIncremental")

        if (isIncremental) {
            when (status) {
                Status.ADDED,
                Status.CHANGED -> {
                    handleJarFile(jarInput, destFile)
                }
                Status.REMOVED -> {
                    if (destFile.exists()) {
                        destFile.delete()
                    }
                }
                else -> {
                }
            }
        } else {
            handleJarFile(jarInput, destFile)
        }

    }

    fun transformDirectory(directoryInput: DirectoryInput, outputProvider: TransformOutputProvider, isIncremental: Boolean) {
        val sourceFile = directoryInput.file
        val name = sourceFile.name
        val destDir = outputProvider.getContentLocation(name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
        LogUtil.info("TransformHelper[transformDirectory], name = $name, sourceFile Path = ${sourceFile.absolutePath}, destFile Path = ${destDir.absolutePath}, isIncremental = $isIncremental")
        if (isIncremental) {
            val changeFiles = directoryInput.changedFiles
            for (changeFile in changeFiles) {
                val status = changeFile.value
                val inputFile = changeFile.key
                val destPath = inputFile.absolutePath.replace(sourceFile.absolutePath, destDir.absolutePath)
                val destFile = File(destPath)
                LogUtil.info("目录：$destPath，状态：$status")
                when (status) {
                    Status.NOTCHANGED -> {

                    }
                    Status.REMOVED -> {
                        if (destFile.exists()) {
                            destFile.delete()
                        }
                    }
                    Status.CHANGED, Status.ADDED -> {
                        handleDirectory(inputFile, destFile)
                    }
                    else -> {
                    }
                }
            }
        } else {
            // 首先全部拷贝，防止有后续处理异常导致文件的丢失
            FileUtils.copyDirectory(sourceFile, destDir)
            handleDirectory(sourceFile, destDir)
        }
    }
    //处理来自jar包文件,jar包中的文件有许多种，不能直接按照字节码文件来处理
    private fun handleJarFile(jarInput: JarInput, destFile: File) {
        if (jarInput.file == null || jarInput.file.length() == 0L) {
            LogUtil.info("handleJarfile,$${jarInput.file.absoluteFile} is null")
            return
        }
        //构建jarFile
        val modifyJar = JarFile(jarInput.file, false)
        //创建目标输出文件流
        val jarOutputStream = JarOutputStream(FileOutputStream(destFile))

        for (jarEntry in modifyJar.entries()) {//遍历处理jar包文件
            val inputStream = modifyJar.getInputStream(jarEntry)
            val entryName = jarEntry.name
            if (entryName.startsWith(".DSA") || entryName.endsWith(".SF")) {
                return
            }
            val tempEntry = JarEntry(entryName)
            jarOutputStream.putNextEntry(tempEntry)
            var modifyClassBytes: ByteArray? = null
            val destClassBytes = IOUtil.readBytes(inputStream)
            if (!jarEntry.isDirectory && entryName.endsWith(".class") && !entryName.startsWith("android")) {
                modifyClassBytes = destClassBytes?.let { modifyClass(it) }
            }

            if (modifyClassBytes != null) {
                jarOutputStream.write(modifyClassBytes)
            } else {
                jarOutputStream.write(destClassBytes!!)
            }
            jarOutputStream.flush()
            jarOutputStream.closeEntry()

        }
        jarOutputStream.close()
        modifyJar.close()
    }
    //修改字节码
    private fun modifyClass(sourceBytes: ByteArray): ByteArray? {

        try {//通过ClassVisitor来访问class字节码
            val classReader = ClassReader(sourceBytes)
            val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
            val classVisitor =
                LogExtensionInterceptor(PluginConstant.ASM_VERSION, classWriter, logFlags)
            classReader.accept(classVisitor, ClassReader.SKIP_DEBUG)
            return classWriter.toByteArray()
        } catch (e: Exception) {
            LogUtil.info("modify class exception = ${e.printStackTrace()}")
        }
        return null
    }

    private fun handleDirectory(sourceFile: File, destDir: File) {
        val files = sourceFile.listFiles { dir, name ->
            val realFile = File(dir, name)
            if (realFile.isDirectory) {
                true
            } else {
                name!!.endsWith(".class")
            }
        }

        for (file in files!!) {
            try {
                val destFile = File(destDir, file.name)
                if (file.isDirectory) {
                    handleDirectory(file, destFile)
                } else {
                    val fileInputStream = FileInputStream(file)
                    val sourceBytes = IOUtil.readBytes(fileInputStream)
                    var modifyBytes: ByteArray? = null
                    if (!file.name.contains("BuildConfig")) {
                        modifyBytes = modifyClass(sourceBytes!!)
                    }
                    if (modifyBytes != null) {
                        val destPath = destFile.absolutePath
                        destFile.delete()
                        IOUtil.byte2File(destPath, modifyBytes)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}