package xyz.wagyourtail.unimined.transform.forge.internal.mcpconfig;

import xyz.wagyourtail.unimined.transform.step.subpipeline.PipelineFunction;
import xyz.wagyourtail.unimined.transform.step.subpipeline.SubStep;
import xyz.wagyourtail.unimined.transform.step.subpipeline.SubpipelineContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shadows unimined 2.0.2's {@code private object NoOpFunction} (McpConfigSubpipeline.kt), which runs NeoForm's
 * {@code downloadManifest} / {@code downloadJson} steps and returns no output. NeoForm 26.x's config passes
 * {@code {downloadManifestOutput}} / {@code {downloadJsonOutput}} to the following steps, and interpolating those
 * fails with "No output from step 'downloadManifest'". Unimined downloads the game itself, so the referenced files
 * are never read; an empty placeholder output is enough.
 * <p>
 * Delete this once unimined handles it upstream.
 */
@SuppressWarnings("unused")
final class NoOpFunction implements PipelineFunction {
    public static final NoOpFunction INSTANCE = new NoOpFunction();

    private NoOpFunction() {
    }

    @Override
    public String getName() {
        return "mcpconfig:noop";
    }

    @Override
    public Path run(SubStep step, SubpipelineContext ctx) {
        Path placeholder = ctx.getWorkDir().resolve(step.getName() + "Output.jar");
        try {
            if (!Files.exists(placeholder)) Files.createFile(placeholder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return placeholder;
    }
}
