package vmtranslator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

public class FileUtils {
    public static boolean isVmFile(Path path) {
        return path.getFileName().toString().endsWith(".vm");
    }

    public static Set<Path> vmDirFilepaths(Path path) {
        if (Files.isDirectory(path)) {
            File dir = path.toFile();
            Set<Path> paths = stream(requireNonNull(
                    dir.listFiles(f -> f.getName().endsWith(".vm"))))
                    .map(File::toPath)
                    .collect(Collectors.toSet());

            if (paths.isEmpty()) {
                throw new RuntimeException("Argument '%s' is a directory with no .vm files to compile"
                        .formatted(path));
            }

            return paths;
        }

        throw new RuntimeException("Argument '%s' is not a .vm file nor a directory"
                .formatted(path));
    }

    public static Path resolveAsmPath(Path vmPath) {
        return Files.isDirectory(vmPath) ?
                vmPath.resolve(vmPath.getFileName() + ".asm") :
                Path.of(vmPath.toString().replace(".vm", ".asm"));
    }
}
