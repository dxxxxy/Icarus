package studio.dreamys.icarus.config;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Finds classes carrying a runtime annotation inside a mod's own directories/jars.
 * <p>
 * Replaces the Reflections library, whose classpath scan sees nothing on Fabric or modern (Neo)Forge
 * class loaders. Only classes whose bytes mention the annotation's descriptor are loaded, so unrelated
 * classes (mixins in particular) are never touched.
 */
public final class ClassScanner {
    private ClassScanner() {
    }

    public static List<Class<?>> findAnnotated(Collection<Path> roots, ClassLoader loader, Class<? extends Annotation> annotation) {
        byte[] descriptor = ("L" + annotation.getName().replace('.', '/') + ";").getBytes(StandardCharsets.UTF_8);
        List<Class<?>> found = new ArrayList<>();

        for (Path root : roots) {
            try {
                if (Files.isDirectory(root)) {
                    scan(root, descriptor, loader, annotation, found);
                } else if (Files.isRegularFile(root)) {
                    try (FileSystem fs = FileSystems.newFileSystem(root, (ClassLoader) null)) {
                        scan(fs.getPath("/"), descriptor, loader, annotation, found);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to scan " + root + " for @" + annotation.getSimpleName(), e);
            }
        }

        return found;
    }

    private static void scan(Path root, byte[] descriptor, ClassLoader loader, Class<? extends Annotation> annotation, List<Class<?>> found) throws IOException {
        List<Path> classFiles = new ArrayList<>();
        try (Stream<Path> files = Files.walk(root)) {
            files.filter(p -> p.toString().endsWith(".class") && Files.isRegularFile(p)).forEach(classFiles::add);
        }

        for (Path file : classFiles) {
            String relative = root.relativize(file).toString().replace('\\', '/');
            if (relative.startsWith("META-INF/") || relative.endsWith("module-info.class") || relative.endsWith("package-info.class")) continue;
            if (!contains(Files.readAllBytes(file), descriptor)) continue;

            String name = relative.substring(0, relative.length() - ".class".length()).replace('/', '.');
            try {
                Class<?> type = Class.forName(name, false, loader);
                if (type.isAnnotationPresent(annotation)) found.add(type);
            } catch (ClassNotFoundException | LinkageError e) {
                throw new RuntimeException("Failed to load " + name + " while scanning for @" + annotation.getSimpleName(), e);
            }
        }
    }

    private static boolean contains(byte[] haystack, byte[] needle) {
        outer:
        for (int i = 0; i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) continue outer;
            }
            return true;
        }
        return false;
    }
}
