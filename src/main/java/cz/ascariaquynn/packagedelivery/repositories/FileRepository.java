package cz.ascariaquynn.packagedelivery.repositories;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Repository
public class FileRepository {

    /**
     * Load data stream from file.
     * @param packagesFilePath
     * @return
     * @throws IOException
     */
    public Stream<String> loadLines(String packagesFilePath) throws IOException {
        return Files.lines(Paths.get(packagesFilePath));
    }
}
