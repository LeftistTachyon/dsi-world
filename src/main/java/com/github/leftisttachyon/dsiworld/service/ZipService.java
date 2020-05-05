package com.github.leftisttachyon.dsiworld.service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A pseudo-service that zips and unzips files.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Slf4j
public final class ZipService {
    /**
     * The one and only
     */
    private static final ZipService INSTANCE = new ZipService();

    /**
     * No instantiation
     */
    private ZipService() {
    }

    /**
     * Returns an instance of a ZipService
     *
     * @return an instance of a ZipService
     */
    public static ZipService getInstance() {
        return INSTANCE;
    }

    /**
     * Zips the given source folder into the destination.<br>
     * It should be noted that this zips the contents of the folder, not the folder itself.
     *
     * @param source      the source folder to compress
     * @param destination the destination archive file to write data to
     * @return whether the operation was successful
     */
    public boolean zipFolder(File source, File destination) {
        if (!source.isDirectory()) {
            return false;
        }

        try (FileOutputStream fos = new FileOutputStream(destination);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            for (File f : Objects.requireNonNull(source.listFiles())) {
                try {
                    zipFile(f, f.getName(), zipOut);
                } catch (IOException e) {
                    log.warn("An IOException occurred while zipping file "
                            + f.getName(), e);
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            log.warn("An IOException occurred while zipping files", e);

            return false;
        }
    }

    /**
     * Unzips the given source file into the destination.
     *
     * @param source      the archive file to extract from
     * @param destination the destination directory to place the extracted files into
     * @return whether the operation was successful
     */
    public boolean unzipFile(File source, File destination) {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destination, zipEntry);
                System.out.println(newFile.getParent() + " || " + newFile.getName());
                File parentFile = newFile.getParentFile();
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    log.warn("Could not make parent directory for child file");

                    return false;
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

            return true;
        } catch (IOException e) {
            log.warn("An IOException occurred while extracting the files", e);

            return false;
        }
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
