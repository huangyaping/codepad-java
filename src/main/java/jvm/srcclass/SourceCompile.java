package jvm.srcclass;

import jvm.srcclass.service.HelloService;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://github.com/OpenHFT/Java-Runtime-Compiler" />
 * @see <a href="https://github.com/apache/tomcat/blob/efe0fe8b3d6b10b7e416c32b59f60bb441831c39/java/org/apache/jasper/JspCompilationContext.java#L594" />
 */
public class SourceCompile {
    public static void main(String[] args) {
        new SourceCompile();
    }
    public SourceCompile() {
        memToMem();
    }

    private void memToMem() {
        String pkgName = "jvm.srcclass.service";
        String className = "HelloServiceImpl";
        String source = "package jvm.srcclass.service; public class HelloServiceImpl implements HelloService {@Override public String hello() {return \"hello\"; } }";
        SimpleJavaFileObject sourceJavaFileObject = new SimpleJavaFileObject(URI.create(className+".java"), JavaFileObject.Kind.SOURCE){
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
        Class klass1 = null;
        try {
             klass1 = memToMemCompile(pkgName, className, sourceJavaFileObject);
            HelloService helloService = (HelloService) klass1.newInstance();
            System.out.println(helloService.hello());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        String source2 = "package jvm.srcclass.service; public class HelloServiceImpl implements HelloService {@Override public String hello() {return \"hello2\"; } }";
        // 把源代码封装到JavaFileObject对象
        JavaFileObject sourceJavaFileObject2 = new SimpleJavaFileObject(URI.create(className+".java"), JavaFileObject.Kind.SOURCE){
            @Override
            // 编译的时候会调用这个方法获取源代码
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source2;
            }
        };
        try {
            Class klass = memToMemCompile(pkgName, className, sourceJavaFileObject2);
            HelloService helloService = (HelloService) klass.newInstance();
            System.out.println(helloService.hello());
            System.out.println(klass.equals(klass1));
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private Class memToMemCompile(String pkgName, String className, JavaFileObject sourceJavaFileObject) throws ClassNotFoundException {
        CompileClassLoader classLoader = new CompileClassLoader();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticListener<? super JavaFileObject> listener = new DiagnosticCollector<>();
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(listener, null, Charset.defaultCharset());
        JavaFileManager javaFileManager = new ForwardingJavaFileManager<JavaFileManager>(standardFileManager) {
            @Override
            // 编译完成后，拿到JavaFileObject对象ForOutput
            public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
                SimpleJavaFileObject classJavaFileObject = new SimpleJavaFileObject(URI.create(className), kind) {
                    ByteArrayOutputStream out;
                    @Override
                    public OutputStream openOutputStream() throws IOException {
                        if (out == null) {
                            out = new ByteArrayOutputStream();
                        }
                        return out;
                    }
                };
                // 把class数据缓存ClassLoader的列表中，供后续加载
                classLoader.cacheClass(className, classJavaFileObject);
                return classJavaFileObject;
            }
        };
        JavaCompiler.CompilationTask task = compiler.getTask(null, javaFileManager, listener, null, null, Arrays.asList(sourceJavaFileObject));
        Boolean result = task.call();
        System.out.println("compilation result="+result);
        return classLoader.loadClass(pkgName + "." + className);
    }

    class CompileClassLoader extends ClassLoader {
        Map<String, JavaFileObject> map = new HashMap<>();
        @Override
        // parent不负责加载的class会从这里开始加载
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if(map.containsKey(name)) {
                JavaFileObject javaFileObject = map.get(name);
                byte[] bytes = getBytes(javaFileObject);
                return defineClass(name, bytes, 0, bytes.length);
            } else {
                return super.findClass(name);
            }
        }

        private byte[] getBytes(JavaFileObject javaFileObject) {
            try {
                return ((ByteArrayOutputStream) javaFileObject.openOutputStream()).toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void cacheClass(String className, JavaFileObject javaFileObject) {
            map.put(className, javaFileObject);
        }
    }

}
