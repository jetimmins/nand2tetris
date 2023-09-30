package compiler.io;

import compiler.Format;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

public class FileUtils {

    public static boolean isJackFile(Path src) {
        return src.getFileName().toString().endsWith(".jack");
    }

    public static Set<Path> jackFilepaths(Path src) {
        if (Files.isDirectory(src)) {
            File dir = src.toFile();
            Set<Path> paths = stream(requireNonNull(
                    dir.listFiles(srcFile -> srcFile.getName().endsWith(".jack"))))
                    .map(File::toPath)
                    .collect(Collectors.toSet());

            if (paths.isEmpty()) {
                throw new RuntimeException("Argument '%s' is a directory with no .jack files to compile"
                        .formatted(src));
            }

            return paths;
        }

        throw new RuntimeException("Argument '%s' is not a .jack file nor a directory"
                .formatted(src));
    }

    public static File resolveTarget(Path src, Path target, Format format) {
        String extension = switch (format) {
            case VM -> ".vm";
            case XML -> ".xml";
            case TOKENS -> "T.xml";
        };
        String targetFilename = src.getFileName().toString().replace(".jack", extension);
        return target.resolve(targetFilename).toFile();
    }

    public static Path setUpTarget(Path src, String target) {
        Path targetDir = target.isBlank() ?
                defaultTargetDir(src) :
                Path.of(target).toAbsolutePath();

        if (!Files.exists(targetDir)) {
            try {
                Files.createDirectory(targetDir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory for given target: " + target, e);
            }
        }
        return targetDir;
    }

    private static Path defaultTargetDir(Path src) {
        return Files.isDirectory(src) ?
                src.resolve("target") :
                src.getParent().resolve("target");
    }
}
