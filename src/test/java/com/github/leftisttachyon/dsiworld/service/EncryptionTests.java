package com.github.leftisttachyon.dsiworld.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A class that tests encryption.
 */
@SpringBootTest
public class EncryptionTests {

    /**
     * the {@link EncryptionFileService} to test with
     */
    @Autowired
    private EncryptionFileService efs;

    /**
     * A test with an {@link EncryptionFileService}: encrypting and decrypting
     *
     * @throws IOException                        if something goes wrong
     * @throws InvalidAlgorithmParameterException if something goes wrong
     * @throws InvalidKeyException                if something goes wrong
     */
    @Test
    public void encryptDecryptTest() throws IOException, InvalidAlgorithmParameterException, InvalidKeyException {
        String info = "This is a\nmulti-line\nnightmare";

        File temp = Files.createTempFile("temp", "txt").toFile();

        System.out.print("Encrypting text... ");
        try (PrintWriter out = efs.getEncryptedPrintWriter(temp)) {
            out.println(info);
        }
        System.out.println("Complete");

        System.out.println("Encrypted stuff:");
        try (BufferedReader in = new BufferedReader(new FileReader(temp))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }

        System.out.println("Reading text:");
        StringBuilder lines = new StringBuilder();
        try (BufferedReader in = efs.getEncryptedBufferedReader(temp)) {
            String line = in.readLine();
            while (line != null) {
                System.out.println(line);
                lines.append(line);

                line = in.readLine();
                if (line != null) {
                    lines.append('\n');
                }
            }
        }

        assertEquals(lines.toString(), info);
    }

    /**
     * Tests encryption across instances of {@link EncryptionFileService}
     *
     * @throws IOException                        if something goes wrong
     * @throws InvalidAlgorithmParameterException if something goes wrong
     * @throws InvalidKeyException                if something goes wrong
     */
    @Test
    public void crossInstanceEncryptionTest() throws IOException, InvalidAlgorithmParameterException,
            InvalidKeyException {
        final long seed = 125L;
        EncryptionFileService one = new EncryptionFileServiceImpl(seed);
        String info = "This is a\nmulti-line\nnightmare";

        File temp = Files.createTempFile("temp", "txt").toFile();

        System.out.print("Encrypting text... ");
        try (PrintWriter out = one.getEncryptedPrintWriter(temp)) {
            out.println(info);
        }
        System.out.println("Complete");

        System.out.println("Encrypted stuff:");
        try (BufferedReader in = new BufferedReader(new FileReader(temp))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }

        EncryptionFileService two = new EncryptionFileServiceImpl(seed);

        System.out.println("Reading text:");
        StringBuilder lines = new StringBuilder();
        try (BufferedReader in = two.getEncryptedBufferedReader(temp)) {
            String line = in.readLine();
            while (line != null) {
                System.out.println(line);
                lines.append(line);

                line = in.readLine();
                if (line != null) {
                    lines.append('\n');
                }
            }
        }

        assertEquals(lines.toString(), info);
    }

    /**
     * Literally nothing
     */
    public void generationTest() {
        SecureRandom r = new SecureRandom();
        byte[] bb = new byte[16];
        r.nextBytes(bb);

        System.out.print("new byte[]{");
        for (int i = 0; i < bb.length; i++) {
            System.out.print(bb[i]);
            if (i == bb.length - 1) {
                System.out.println("}");
            } else {
                System.out.print(", ");
            }
        }
    }

    @Value("${encryption.seed}")
    private long seed;

    /**
     * Tests whether the property injection from Spring will works like I remember it.
     */
    @Test
    public void propertyInjectionTest() {
        System.out.println(seed);
        String s = System.getenv("SEED");
        if (s == null || s.isEmpty() || !s.matches("\\d+")) {
            assertEquals(125, seed);
        } else {
            assertEquals(s, String.valueOf(seed));
        }
    }
}
