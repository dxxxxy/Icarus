package studio.dreamys.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import xyz.wagyourtail.unimined.runs.MinecraftRunTask;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Passes unimined run classpaths through a java {@code @argfile} on Windows.
 * <p>
 * A modern Minecraft classpath is longer than Windows allows on a command line, so Gradle's javaexec swaps it for
 * a "pathing jar" whose manifest lists the real entries. The JVM can load classes through that, but
 * {@code java.class.path} then only contains the pathing jar, and Fabric's Knot (which discovers the game and dev
 * mods from {@code java.class.path}) fails with "couldn't locate the game". An argfile keeps the command line short
 * and {@code java.class.path} complete.
 */
public class ArgFileClasspathPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) return;

        // unimined (re)sets the run task's classpath and jvm args in its own configuration action, so take them over
        // only once the task graph is final, right before execution
        project.getGradle().getTaskGraph().whenReady(graph -> graph.getAllTasks().stream()
                .filter(t -> t instanceof MinecraftRunTask && t.getProject() == project)
                .forEach(t -> takeOver(project, (MinecraftRunTask) t)));
    }

    private static void takeOver(Project project, MinecraftRunTask task) {
        File argFile = project.getLayout().getBuildDirectory().file("tmp/" + task.getName() + "/classpath.args").get().getAsFile();

        //hand the JVM only the argfile; an empty task classpath also keeps javaexec from adding its pathing jar
        ConfigurableFileCollection classpath = project.getObjects().fileCollection();
        classpath.from(new java.util.ArrayList<>(task.getRuntimeClasspath().getFrom())); //snapshot: getFrom() is live
        task.getRuntimeClasspath().setFrom();
        task.getJvmArgs().add("@" + argFile.getAbsolutePath());

        task.doFirst(t -> {
            String joined = classpath.getFiles().stream()
                    .map(f -> f.getAbsolutePath().replace('\\', '/'))
                    .collect(Collectors.joining(File.pathSeparator));
            try {
                Files.createDirectories(argFile.toPath().getParent());
                Files.write(argFile.toPath(), ("-cp\n\"" + joined + "\"\n").getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
