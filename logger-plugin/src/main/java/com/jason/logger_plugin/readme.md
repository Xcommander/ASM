
# 一、删除日志

思路：

（1）根据ASM Core api可知，要修改字节码，必须能够访问到，对于方法来说，是在MethodVisitor进行访问的。
所以我们必须自定义MethodVisitor，然后在visitMethodInsn，进行拦截，然后修改。
这个是AOP思路

而根据AOP思路：
- JoinPoint：连接点，我们关注点是日志
- PointCut：切入点，负责拦截这些日志
- Advice：通知，负责处理拦截到的连接点

所以asm core api提供了AdviceAdapter，它是一个MethodVisitor的子类，我们需要重写visitMethodInsn方法即可。

（2）MethodVisitor是ClassVisitor,所以我们必须在CLassVisitor替换掉MethodVisitor。
故而我们需要自定义ClassVisitor，然后替换掉MethodVisitor即可。


上述完成后，需要Transform进行操作class字节码文件。那么需要写
Transform的操作规则

通过ASM ByteCode Viewer将插入的Java/kotlin代码，转换成ASM的代码逻辑。转换后的代码，就是我们需要的代码。




ASM总的架构思路：

input输入class字节码流 -> CLassVisitor进行visit()对字节码进行访问。以方法为例，会调用visitMethod来访问方法 
-> 这时候MethodVisitor就会访问方法，对于操作码来说执行visitMethodInsn(),在visitMethodInsn进行拦截和处理字节码。

所以这个就是我们为什么需要自定义MethodVisitor和CLassVisitor。


插件作用范围是编译期，不会打包到apk中。所以打印日志都是在编译期，不会出现在运行期。

                          