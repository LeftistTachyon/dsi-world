package com.github.leftisttachyon.dsiworld;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A collection of tests that experiment with zipping and unzipping files.
 */
public class ZipTests {
    /**
     * A test to figure out zipping a directory.<br>
     * Result: success!
     *
     * @throws IOException if something goes wrong while manipulating files
     */
    @Test
    public void zipDirectoryTest() throws IOException {
        File dir = Files.createTempDirectory("").toFile();
        System.out.println("dir: " + dir.getCanonicalPath());
        if (!dir.exists() && !dir.mkdir()) {
            Assertions.fail("Unable to create parent directory");
        }

        for (int i = 1; i <= 5; i++) {
            File f = new File(dir, "file" + i);
            System.out.println("file #" + i + ": " + f.getCanonicalPath());
            if (!f.exists() && !f.createNewFile()) {
                Assertions.fail("Unable to create file #" + i);
            }
        }

        File zipped = File.createTempFile("compressed", ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipped);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            zipFile(dir, dir.getName(), zipOut);
        }

        System.out.println("You can find the zipped file @ " + zipped.getCanonicalPath());
    }

    /**
     * Feeds the given file to the given {@link ZipOutputStream}
     *
     * @param fileToZip the file to zip
     * @param fileName  the current "canonical" path fo the file being zipped
     * @param zipOut    the {@link ZipOutputStream} being used
     * @throws IOException if something goes wrong while zipping the file
     */
    private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }

            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }

            return;
        }

        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }
}
