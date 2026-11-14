package studio.dreamys.gradle;

import xyz.wagyourtail.unimined.transform.processor.JarProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * Drop-in replacement for unimined 2.0.2's {@code automatic-module-name} jar processor.
 * <p>
 * The builtin one rewrites the jar through a zip filesystem while its own {@link JarFile}
 * handle on that jar is still open, which fails on Windows ("The process cannot access the
 * file because it is being used by another process") for every forge environment.
 * This copy reads the manifest(s) first, closes the handles, then rewrites.
 * It is registered via META-INF/services and found before the builtin because buildSrc's
 * classes precede the plugin jars on the classpath.
 * <p>
 * Delete this (and the services file) once unimined ships a fix.
 */
public class WindowsSafeAutomaticModuleNameProcessor implements JarProcessor {
    private static final String NAME = "automatic-module-name";

    static final class Spec implements JarProcessorSpec {
        private static final long serialVersionUID = 1L;
        final String moduleName;
        final String manifestDonor;

        Spec(String moduleName, String manifestDonor) {
            this.moduleName = moduleName;
            this.manifestDonor = manifestDonor;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public JarProcessorSpec buildSpec(SpecContext context) {
        return null;
    }

    @Override
    public String serializeSpec(JarProcessorSpec spec) {
        Spec s = (Spec) spec;
        return s.manifestDonor == null ? s.moduleName : s.moduleName + " " + s.manifestDonor;
    }

    @Override
    public JarProcessorSpec deserializeSpec(String payload) {
        String[] parts = payload.trim().split(" ", 2);
        return new Spec(parts[0], parts.length > 1 ? parts[1] : null);
    }

    @Override
    public void processJar(Path jar, JarProcessorSpec spec, ProcessorContext context) {
        Spec s = (Spec) spec;
        try {
            Manifest manifest = readManifest(jar);
            if (manifest == null) manifest = new Manifest();
            if (s.moduleName.equals(manifest.getMainAttributes().getValue("Automatic-Module-Name"))) return;

            manifest.getMainAttributes().putIfAbsent(Attributes.Name.MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().putValue("Automatic-Module-Name", s.moduleName);

            if (s.manifestDonor != null) {
                Manifest donor = readManifest(Path.of(s.manifestDonor));
                if (donor != null) {
                    for (Map.Entry<String, Attributes> e : donor.getEntries().entrySet()) {
                        boolean wants = e.getValue().keySet().stream().anyMatch(k ->
                                k.toString().equals("Specification-Version") || k.toString().equals("Implementation-Version"));
                        if (!wants) continue;
                        Attributes out = new Attributes();
                        e.getValue().forEach((k, v) -> {
                            if (!k.toString().contains("Digest")) out.put(k, v);
                        });
                        manifest.getEntries().put(e.getKey(), out);
                    }
                }
            }

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            manifest.write(bytes);

            // every JarFile handle is closed by now, so the zipfs rewrite can replace the file
            try (FileSystem fs = FileSystems.newFileSystem(jar, Collections.<String, Object>emptyMap())) {
                Path p = fs.getPath("META-INF/MANIFEST.MF");
                Files.deleteIfExists(p);
                Files.createDirectories(p.getParent());
                Files.write(p, bytes.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                Files.setLastModifiedTime(p, FileTime.fromMillis(0));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Manifest readManifest(Path jar) throws IOException {
        try (JarFile jf = new JarFile(jar.toFile())) {
            ZipEntry entry = jf.getEntry("META-INF/MANIFEST.MF");
            if (entry == null) return null;
            try (InputStream in = jf.getInputStream(entry)) {
                return new Manifest(in);
            }
        }
    }
}
