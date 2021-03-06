package com.github.leftisttachyon.dsiworld;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A collection of tests that experiment with zipping and unzipping files.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ZipTests {
    /**
     * The file that contains the zipped info
     */
    private static File zipped = null;

    /**
     * A test to figure out zipping a directory.<br>
     * Result: success!
     *
     * @throws IOException if something goes wrong while manipulating files
     */
    @Test
    @Order(1)
    public void zipDirectoryTest() throws IOException {
        System.out.println("=== ZIPPING ===");
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

            try (PrintWriter out = new PrintWriter(f)) {
                out.println("This is file #" + i);
            }
        }

        zipped = File.createTempFile("compressed", ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipped);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            zipFile(dir, dir.getName(), zipOut);
        }

        System.out.println("You can find the zipped file @ " + zipped.getCanonicalPath());
    }

    /**
     * A test to figure out unzipping a directory
     *
     * @throws IOException whether something went wrong
     */
    @Test
    @Order(2)
    public void unzipDirectoryTest() throws IOException {
        System.out.println("=== UNZIPPING ===");
        Assertions.assertNotNull(zipped);

        File destDir = Files.createTempDirectory("").toFile();
        System.out.println("dir: " + destDir.getCanonicalPath());
        if (!destDir.exists() && !destDir.mkdir()) {
            Assertions.fail("Unable to create parent directory");
        }

        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipped))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                System.out.println(newFile.getParent() + " || " + newFile.getName());
                File parentFile = newFile.getParentFile();
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    Assertions.fail("Could not make parent directory for child file");
                }

                if (!zipEntry.isDirectory()) {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }

        System.out.println("You can find the unzipped contents @ " + destDir);
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

    /**
     * Creates a file as dictated by the given {@link ZipEntry}
     *
     * @param destinationDir the location to extract the file to
     * @param zipEntry       the {@link ZipEntry} that dictates where to put the file
     * @return the newly created file
     * @throws IOException if something goes wrong while manipulating files
     */
    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
