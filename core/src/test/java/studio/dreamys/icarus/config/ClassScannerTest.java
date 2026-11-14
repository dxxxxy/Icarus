package studio.dreamys.icarus.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studio.dreamys.icarus.annotation.type.IPage;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassScannerTest {
    @IPage('a')
    public static class FirstPage {
    }

    @IPage('b')
    public static class SecondPage {
    }

    /** Mentions IPage in its constant pool without being annotated with it. */
    public static class NotAPage {
        public static final Class<?> REFERENCE = IPage.class;
    }

    private static Path testClassesDir() throws Exception {
        return Paths.get(ClassScannerTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }

    private static List<String> names(List<Class<?>> classes) {
        //only this test's own fixtures (other tests declare pages too)
        return classes.stream().filter(c -> c.getEnclosingClass() == ClassScannerTest.class).map(Class::getSimpleName).sorted().collect(Collectors.toList());
    }

    @Test
    void findsAnnotatedClassesInDirectory() throws Exception {
        List<Class<?>> found = ClassScanner.findAnnotated(Collections.singletonList(testClassesDir()), getClass().getClassLoader(), IPage.class);
        assertEquals(java.util.Arrays.asList("FirstPage", "SecondPage"), names(found));
    }

    @Test
    void findsAnnotatedClassesInJar(@TempDir Path tmp) throws Exception {
        Path root = testClassesDir();
        Path jar = tmp.resolve("mod.jar");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar)); Stream<Path> files = Files.walk(root)) {
            for (Path file : (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                out.putNextEntry(new JarEntry(root.relativize(file).toString().replace('\\', '/')));
                Files.copy(file, (OutputStream) out);
                out.closeEntry();
            }
        }

        List<Class<?>> found = ClassScanner.findAnnotated(Collections.singletonList(jar), getClass().getClassLoader(), IPage.class);
        assertEquals(java.util.Arrays.asList("FirstPage", "SecondPage"), names(found));
    }
}
