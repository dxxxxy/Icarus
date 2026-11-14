package xyz.wagyourtail.unimined.minecraft.localrepo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

/**
 * Shadows unimined 2.0.2's {@code LocalMavenPublisher} (a Kotlin {@code object}, hence {@link #INSTANCE}).
 * <p>
 * The original re-copies every artifact into {@code .gradle/unimined/local-repo} on every configuration with an
 * atomic move over the existing file. On Windows that fails with AccessDeniedException as soon as a Gradle daemon
 * still has the previous copy open, i.e. on practically every build after the first.
 * This copy skips byte-identical files and only replaces changed ones.
 * <p>
 * It wins because buildSrc's classes precede the plugin jars on the shared class path.
 * Delete once unimined ships a fix.
 */
@SuppressWarnings("unused")
public final class LocalMavenPublisher {
    public static final LocalMavenPublisher INSTANCE = new LocalMavenPublisher();

    private LocalMavenPublisher() {
    }

    public Path moduleDir(Path repoDir, String group, String artifactId, String version) {
        return repoDir.resolve(group.replace('.', '/')).resolve(artifactId).resolve(version);
    }

    public Path publish(Path repoDir, String group, String artifactId, String version, Map<String, Path> files) {
        try {
            Path moduleDir = Files.createDirectories(moduleDir(repoDir, group, artifactId, version));

            String pom = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">\n" +
                    "    <modelVersion>4.0.0</modelVersion>\n" +
                    "    <groupId>" + group + "</groupId>\n" +
                    "    <artifactId>" + artifactId + "</artifactId>\n" +
                    "    <version>" + version + "</version>\n" +
                    "    <packaging>jar</packaging>\n" +
                    "</project>";
            Path pomFile = moduleDir.resolve(artifactId + "-" + version + ".pom");
            if (!Files.exists(pomFile) || !new String(Files.readAllBytes(pomFile), StandardCharsets.UTF_8).equals(pom)) {
                Files.write(pomFile, pom.getBytes(StandardCharsets.UTF_8));
            }

            for (Map.Entry<String, Path> e : files.entrySet()) {
                String name = artifactId + "-" + version + (e.getKey().isEmpty() ? "" : "-" + e.getKey()) + ".jar";
                copyIfChanged(e.getValue(), moduleDir.resolve(name));
            }
            return moduleDir;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void copyIfChanged(Path source, Path target) throws IOException {
        if (Files.exists(target) && Files.size(target) == Files.size(source) && Files.mismatch(source, target) == -1) {
            return;
        }

        Path tmp = target.resolveSibling("." + target.getFileName() + "." + System.nanoTime() + ".tmp");
        Files.copy(source, tmp, StandardCopyOption.REPLACE_EXISTING);
        try {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AccessDeniedException | java.nio.file.AtomicMoveNotSupportedException ex) {
            try {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (FileSystemException locked) {
                Files.deleteIfExists(tmp);
                throw new IOException("Cannot replace " + target + " (still open by another Gradle daemon? run ./gradlew --stop)", locked);
            }
        }
    }
}
