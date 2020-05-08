package com.github.leftisttachyon.dsiworld;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A class that runs a couple of tests that test JGit.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
public class JGitTests {
    /**
     * The test from the Vogella web page included in the README.
     *
     * @throws IOException           if something goes wrong while manipulating files
     * @throws IllegalStateException if something goes wrong unexpectedly
     * @throws GitAPIException       if Git doesn't like what's happening
     */
    @Test
    public void vogellaExample1() throws IOException, IllegalStateException, GitAPIException {
        File localPath = File.createTempFile("JGitTestRepository", "");
        // delete repository before running this
        Files.delete(localPath.toPath());

        // This code would allow to access an existing repository
//      try (Git git = Git.open(new File("/home/vogella/git/eclipse.platform.ui"))) {
//          Repository repository = git.getRepository();
//
//      }

        // Create the git repository with init
        try (Git git = Git.init().setDirectory(localPath).call()) {
            System.out.println("Created repository: " + git.getRepository().getDirectory());
            File myFile = new File(git.getRepository().getDirectory().getParent(), "testfile");
            if (!myFile.createNewFile()) {
                throw new IOException("Could not create file " + myFile);
            }

            // run the add-call
            git.add().addFilepattern("testfile").call();

            git.commit().setMessage("Initial commit").call();
            System.out.println("Committed file " + myFile + " to repository at "
                    + git.getRepository().getDirectory());
            // Create a few branches for testing
            for (int i = 0; i < 10; i++) {
                git.checkout().setCreateBranch(true).setName("new-branch" + i).call();
            }
            // List all branches
            List<Ref> call = git.branchList().call();
            for (Ref ref : call) {
                System.out.println("Branch: " + ref + " " + ref.getName() + " "
                        + ref.getObjectId().getName());
            }

            // Create a few new files
            for (int i = 0; i < 10; i++) {
                File f = new File(git.getRepository().getDirectory().getParent(),
                        "testfile" + i);
                f.createNewFile();
                if (i % 2 == 0) {
                    git.add().addFilepattern("testfile" + i).call();
                }
            }

            Status status = git.status().call();

            Set<String> added = status.getAdded();
            for (String add : added) {
                System.out.println("Added: " + add);
            }
            Set<String> uncommittedChanges = status.getUncommittedChanges();
            for (String uncommitted : uncommittedChanges) {
                System.out.println("Uncommitted: " + uncommitted);
            }

            Set<String> untracked = status.getUntracked();
            for (String untrack : untracked) {
                System.out.println("Untracked: " + untrack);
            }

            // Find the head for the repository
            ObjectId lastCommitId = git.getRepository().resolve(Constants.HEAD);
            System.out.println("Head points to the following commit :"
                    + lastCommitId.getName());
        }
    }

    /**
     * A simple test to see whether a system property exists
     */
    @Test
    public void tempDirTest() {
        String tempDir = System.getProperty("java.io.tmpdir");
        System.out.println(tempDir);

        Assertions.assertNotNull(tempDir);
    }

    /**
     * A test to figure out whether {@link Files#list(Path)} or {@link File#listFiles()} is faster.
     *
     * @throws IOException if something goes wrong with file manipulation
     */
    @Test
    public void listFilesSpeedtest() throws IOException {
        String dirStr = /* System.getProperty("java.io.tmpdir") */ "C:/Windows/";
        Assertions.assertNotNull(dirStr);

        File dir = new File(dirStr);
        Assertions.assertNotNull(dir);

        double start, total;
        StringBuilder file = new StringBuilder(),
                files = new StringBuilder();

        start = System.nanoTime();
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            System.out.println(f.getName());
            file.append(f.getName());
            file.append('\n');
        }
        total = System.nanoTime() - start;
        total /= 1_000_000;
        System.out.printf("File method : %.3f ms%n", total);

        start = System.nanoTime();
        Files.list(dir.toPath()).map(p -> p.toFile().getName()).forEach(s -> {
            System.out.println(s);
            files.append(s);
            files.append('\n');
        });
        total = System.nanoTime() - start;
        total /= 1_000_000;
        System.out.printf("Files method: %.3f ms%n", total);

        Assertions.assertEquals(file.toString(), files.toString(),
                "The two methods got different outputs");
    }

}
