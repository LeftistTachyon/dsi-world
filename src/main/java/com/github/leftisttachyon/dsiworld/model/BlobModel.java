package com.github.leftisttachyon.dsiworld.model;

import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.azure.storage.blob.CommonRestResponse;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.TransferManager;
import io.reactivex.Single;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.StandardOpenOption;

/**
 * A model class that represents a Blob in Azure
 *
 * @author Jed Wang
 * @since 1.0.0
 */
public class BlobModel implements Closeable, Serializable {

    private static final long serialVersionUID = -11L;
    private final BlockBlobURL blobURL;
    private File download = null;

    // Ctrl + c, Ctrl + v from Microsoft's example
    // Why reinvent the wheel?
    BlobModel(ContainerURL containerURL, String blobName) {
        // this.serviceURL = serviceURL;
        blobURL = containerURL.createBlockBlobURL(blobName);
    }

    // The M in MVC
    static void uploadFile(BlockBlobURL blob, File sourceFile) throws IOException, InterruptedException {
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(sourceFile.toPath());

        // Uploading a file to the blobURL using the high-level methods available in TransferManager class
        // Alternatively call the PutBlob/PutBlock low-level methods from BlockBlobURL type
        final Object LOCK = new Object();
        TransferManager.uploadFileToBlockBlob(fileChannel, blob, 8 * 1024 * 1024, null, null).subscribe(response -> {
            // System.out.println("Completed upload request.");
            // System.out.println(response.response().statusCode());
            synchronized (LOCK) {
                LOCK.notify();
            }
        });

        synchronized (LOCK) {
            LOCK.wait();
        }
    }

    @Deprecated
    static Single<CommonRestResponse> asyncUploadFile(BlockBlobURL blob, File sourceFile) throws IOException {
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(sourceFile.toPath());

        // Uploading a file to the blobURL using the high-level methods available in TransferManager class
        // Alternatively call the PutBlob/PutBlock low-level methods from BlockBlobURL type
        return TransferManager.uploadFileToBlockBlob(fileChannel, blob, 8 * 1024 * 1024, null, null);
    }

    static void deleteBlob(BlockBlobURL blobURL) throws InterruptedException {
        // Delete the blob
        final Object LOCK = new Object();
        blobURL.delete(null, null, null).subscribe(response -> {
            // System.out.println(">> Blob deleted: " + blobURL);
            synchronized (LOCK) {
                LOCK.notify();
            }
        }, error -> {
            // System.out.println(">> An error encountered during deleteBlob: " +
            // error.getMessage());
            synchronized (LOCK) {
                LOCK.notify();
            }
        });

        synchronized (LOCK) {
            LOCK.wait();
        }
    }

    static void getBlob(BlockBlobURL blobURL, File sourceFile) throws IOException, InterruptedException {
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(sourceFile.toPath(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        final Object LOCK = new Object();

        TransferManager.downloadBlobToFile(fileChannel, blobURL, null, null).subscribe(response -> {
            // System.out.println("Completed download request.");
            // System.out.println("The blob was downloaded to " +
            // sourceFile.getAbsolutePath());
            synchronized (LOCK) {
                LOCK.notify();
            }
        });

        synchronized (LOCK) {
            LOCK.wait();
        }
    }

    /**
     * Uploads the given file to the blob. The contents of the given file will
     * overwrite what is on the cloud.<br>This operation is synchronous.
     *
     * @param toUpload the file to upload
     * @throws IOException the standard IOException reasons and also causes
     *                     within the Microsoft Azure Java API
     */
    public void uploadFile(File toUpload) throws IOException {
        try {
            uploadFile(blobURL, toUpload);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uploads the given file to the blob. The contents of the given file will
     * overwrite what is on the cloud.<br>This operation is asynchronous.
     *
     * @param toUpload the file to upload
     * @return a listenable object that notifies the listener that the upload is complete
     * @throws IOException the standard IOException reasons and also causes
     *                     within the Microsoft Azure Java API
     * @deprecated since its behavior is not as intended, so it is advised to use uploadFile only
     */
    @Deprecated
    public Single<CommonRestResponse> asyncUploadFile(File toUpload) throws IOException {
        return asyncUploadFile(blobURL, toUpload);
    }

    /**
     * Deletes this blob from Azure.
     */
    public void deleteBlob() {
        try {
            deleteBlob(blobURL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Downloads the file stored on the blob onto a file on the machine.
     *
     * @return the downloaded file
     * @throws IOException the standard IOException reasons
     */
    public File getBlob() throws IOException {
        download = File.createTempFile("savestate", ".schproj");
        try {
            getBlob(blobURL, download);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return download;
    }

    /**
     * This method clears all resources used by this
     * object, which includes deleting any temporary files stored on the
     * machine.
     */
    @Override
    public void close() {
        if (download != null) {
            download.delete();
        }
    }
}
