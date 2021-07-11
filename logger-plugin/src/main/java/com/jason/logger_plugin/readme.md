[toc]

# 一、删除日志

思路：
根据AOP思路：
- JoinPoint：连接点，我们关注点是日志
- PointCut：切入点，负责拦截这些日志
- Advice：通知，负责处理拦截到的连接点

AdviceAdapter：拦截相关操作码（操作已经输入的字节码文件），然后进行操作

上述完成后，需要Transform进行操作class字节码文件。那么需要写
Transform的操作规则

通过ASM ByteCode Viewer将插入的Java/kotlin代码，转换成ASM的代码逻辑。转换后的代码，就是我们需要的代码。