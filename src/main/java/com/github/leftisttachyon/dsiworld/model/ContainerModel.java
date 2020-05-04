package com.github.leftisttachyon.dsiworld.model;

import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.storage.blob.models.BlobItem;
import com.microsoft.azure.storage.blob.models.ContainerListBlobFlatSegmentResponse;
import com.microsoft.rest.v2.RestException;
import io.reactivex.Single;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A class that represents a storage container in Azure's Blob Storage.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@Slf4j
public class ContainerModel implements Serializable {

    private static final long serialVersionUID = -111L;

    private final ContainerURL containerURL;

    /**
     * Creates a ContainerModel and connects it to the cloud. By default, this
     * constructor checks for existence of the container and creates it if it
     * doesn't exist.
     *
     * @param containerName the name of the container to create. This String
     *                      must abide by Azure's rules, or a {@link MalformedURLException} will be
     *                      thrown.
     * @see ContainerModel#ContainerModel(String, String, String, boolean)
     */
    ContainerModel(String accountName, String accountKey, String containerName) {
        this(accountName, accountKey, containerName, true);
    }

    /**
     * Creates a ContainerModel and connects it to the cloud.
     *
     * @param accountName      the name of the storage account to connect to
     * @param accountKey       the key to
     * @param containerName    the name of the container to create. This String
     *                         must abide by Azure's rules, or a {@link MalformedURLException} will be
     *                         thrown.
     * @param checkForCreation whether this constructor should create the
     *                         container if it doesn't exist
     */
    ContainerModel(String accountName, String accountKey, String containerName, boolean checkForCreation) {
        SharedKeyCredentials creds = null;
        try {
            creds = new SharedKeyCredentials(accountName, accountKey);
        } catch (InvalidKeyException e) {
            log.error("Invalid storage account name/key provided");
        }

        if (creds == null) {
            throw new IllegalStateException("You cannot create a blob without credentials");
        }

        log.debug("Found credentials for {}", accountName);

        ServiceURL serviceURL = null;
        try {
            serviceURL = new ServiceURL(new URL("https://" + accountName + ".blob.core.windows.net"),
                    StorageURL.createPipeline(creds, new PipelineOptions()));
        } catch (MalformedURLException e) {
            log.error("Cannot connect to Azure Blob service", e);
        }

        assert serviceURL != null;
        containerURL = serviceURL.createContainerURL(containerName);

        if (checkForCreation) {
            try {
                /* ContainerCreateResponse response = */
                containerURL.create(null, null, null).blockingGet();
                // System.out.println("Container Create Response was " + response.statusCode());
            } catch (RestException e) {
                if (e.response().statusCode() != 409) {
                    throw e;
                }/* else {
                    System.out.println(containerName + " container already exists, resuming...");
                }*/
            }
        }
    }

    private static Single<ContainerListBlobFlatSegmentResponse> listAllBlobs(ContainerURL url,
                                                                             ContainerListBlobFlatSegmentResponse response) {
        // Process the blobs returned in this result segment (if the segment is empty,
        // blobs() will be null.
        if (response.body().segment() != null) {
            for (BlobItem b : response.body().segment().blobItems()) {
                if (b.snapshot() == null) {
                    log.info("Blob name: {}", b.name());
                } else {
                    log.info("Blob name: {}, Snapshot: {}", b.name(), b.snapshot());
                }
            }
        } else {
            log.info("There are no more blobs to list off.");
        }

        // If there is not another segment, return this response as the final response.
        if (response.body().nextMarker() == null) {
            return Single.just(response);
        } else {
            /*
             * IMPORTANT: ListBlobsFlatSegment returns the start of the next segment; you
             * MUST use this to get the next segment (after processing the current result
             * segment
             */

            String nextMarker = response.body().nextMarker();

            /*
             * The presence of the marker indicates that there are more blobs to list, so we
             * make another call to listBlobsFlatSegment and pass the result through this
             * helper function.
             */
            return url.listBlobsFlatSegment(nextMarker, new ListBlobsOptions().withMaxResults(10), null).flatMap(
                    containersListBlobFlatSegmentResponse -> listAllBlobs(url, containersListBlobFlatSegmentResponse));
        }
    }

    private static Single<ContainerListBlobFlatSegmentResponse> listAllBlobs(ContainerURL url,
                                                                             ContainerListBlobFlatSegmentResponse response, List<BlobItem> toAdd) {
        // Process the blobs returned in this result segment (if the segment is empty,
        // blobs() will be null.
        if (response.body().segment() != null) {
            toAdd.addAll(response.body().segment().blobItems());
        }
        // If there is not another segment, return this response as the final response.
        if (response.body().nextMarker() == null) {
            return Single.just(response);
        } else {
            /*
             * IMPORTANT: ListBlobsFlatSegment returns the start of the next segment; you
             * MUST use this to get the next segment (after processing the current result
             * segment
             */

            String nextMarker = response.body().nextMarker();

            /*
             * The presence of the marker indicates that there are more blobs to list, so we
             * make another call to listBlobsFlatSegment and pass the result through this
             * helper function.
             */
            return url.listBlobsFlatSegment(nextMarker, new ListBlobsOptions().withMaxResults(10), null).flatMap(
                    containersListBlobFlatSegmentResponse -> listAllBlobs(url, containersListBlobFlatSegmentResponse));
        }
    }

    @NonNull
    static List<BlobItem> blobList(ContainerURL containerURL) throws InterruptedException {
        List<BlobItem> list = new LinkedList<>();
        // Each ContainerURL.listBlobsFlatSegment call return up to maxResults
        // (maxResults=10 passed into ListBlobOptions below).
        // To list all Blobs, we are creating a helper static method called
        // listAllBlobs,
        // and calling it after the initial listBlobsFlatSegment call
        ListBlobsOptions options = new ListBlobsOptions();
        options.withMaxResults(10);

        final Object LOCK = new Object();

        containerURL.listBlobsFlatSegment(null, options, null)
                .flatMap(containerListBlobFlatSegmentResponse -> listAllBlobs(containerURL,
                        containerListBlobFlatSegmentResponse, list))
                .subscribe(response -> {
                    // System.out.println("Completed request to create list of blobs.");
                    // System.out.println(response.statusCode());
                    synchronized (LOCK) {
                        LOCK.notify();
                    }
                });

        synchronized (LOCK) {
            LOCK.wait();
        }
        return list;
    }

    /**
     * Creates a BlobModel connected to the Blob in Azure and creates a Blob if
     * it doesn't exist.
     *
     * @param blobName the name of the blob to create. This name must abide by
     *                 Azure's naming rules, or a {@link MalformedURLException} will be thrown.
     * @return the newly created BlobModel
     */
    public BlobModel createBlob(String blobName) {
        return new BlobModel(containerURL, blobName);
    }

    /**
     * Creates a BlobModel connected to the Blob in Azure and creates a Blob if
     * it doesn't exist.
     *
     * @param item the data to be used to create a BlobModel from. The name must
     *             abide by Azure's naming rules, or a {@link MalformedURLException} will be
     *             thrown.
     * @return the newly created BlobModel
     */
    public BlobModel createBlob(BlobItem item) {
        return new BlobModel(containerURL, item.name());
    }

    /**
     * Returns the list of BlobItems stored in this Azure Storage Container.
     *
     * @return the list of BlobItems stored in this Azure Storage Container.
     */
    public List<BlobItem> blobList() {
        try {
            return blobList(containerURL);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Deletes this Azure Storage Container.
     */
    public void deleteContainer() {
        try {
            final Object LOCK = new Object();
            containerURL.delete().subscribe(response -> {
                // System.out.println(">> Container URL deleted: " + containerURL);
                synchronized (LOCK) {
                    LOCK.notify();
                }
            }, error -> {
                // System.out.println(">> An error encountered during deleteContainer: " +
                // containerURL);
                synchronized (LOCK) {
                    LOCK.notify();
                }
            });
            synchronized (LOCK) {
                LOCK.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "[ContainerModel URL=\"" + containerURL.toString() + "\"]";
    }
}
