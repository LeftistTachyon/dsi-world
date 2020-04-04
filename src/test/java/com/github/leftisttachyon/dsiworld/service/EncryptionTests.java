package com.github.leftisttachyon.dsiworld.service;

import com.github.leftisttachyon.dsiworld.data.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A class that tests encryption.
 */
@SpringBootTest
public class EncryptionTests {

    /**
     * the {@link EncryptionService} to test with
     */
    @Autowired
    private EncryptionService efs;
    @Value("${encryption.seed}")
    private long seed;

    /**
     * A test with an {@link EncryptionService}: encrypting and decrypting
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
     * Tests encryption across instances of {@link EncryptionService}
     *
     * @throws IOException                        if something goes wrong
     * @throws InvalidAlgorithmParameterException if something goes wrong
     * @throws InvalidKeyException                if something goes wrong
     */
    @Test
    public void crossInstanceEncryptionTest() throws IOException, InvalidAlgorithmParameterException,
            InvalidKeyException {
        final long seed = 125L;
        EncryptionService one = new EncryptionServiceImpl(seed);
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

        EncryptionService two = new EncryptionServiceImpl(seed);

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
     * Tests {@link EncryptionService#getEncryptedObjectInputStream(File)} and
     * {@link EncryptionService#getEncryptedObjectOutputStream(File)}.
     *
     * @throws IOException                        if something goes wrong with the file
     * @throws InvalidKeyException                if something goes wrong
     * @throws InvalidAlgorithmParameterException if something goes wrong
     * @throws ClassNotFoundException             if something goes wrong with reading the object
     */
    @Test
    public void objectEncryptionTest() throws IOException, InvalidKeyException, InvalidAlgorithmParameterException,
            ClassNotFoundException {
        File temp = Files.createTempFile("temp", "txt").toFile();

        User u = new User();
        u.setUsername("Papyrus");

        System.out.print("Serializing object... ");
        try (ObjectOutputStream oos = efs.getEncryptedObjectOutputStream(temp)) {
            oos.writeObject(u);
        }
        System.out.println("Done!");

        System.out.println("File contents:");
        try (BufferedReader in = new BufferedReader(new FileReader(temp))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }

        System.out.print("Reading object... ");
        User read = null;
        try (ObjectInputStream ois = efs.getEncryptedObjectInputStream(temp)) {
            read = (User) ois.readObject();
        }
        System.out.println("Done!");

        System.out.println("Read object:\n" + read);

        assertEquals(u, read, "Users not equal");
    }

    /**
     * A test that tests the sealed object functionality
     *
     * @throws IOException               if something goes wrong
     * @throws IllegalBlockSizeException if something goes wrong
     * @throws ClassNotFoundException    if something goes wrong
     * @throws InvalidKeyException       if something goes wrong
     * @throws NoSuchAlgorithmException  if something goes wrong
     */
    @Test
    public void sealedObjectTest() throws IOException, IllegalBlockSizeException, ClassNotFoundException,
            InvalidKeyException, NoSuchAlgorithmException {
        File temp = Files.createTempFile("temp", "txt").toFile();

        User u = new User();
        u.setUsername("Papyrus");

        System.out.print("Serializing object... ");
        try (FileOutputStream fos = new FileOutputStream(temp);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(efs.sealObject(u));
        }
        System.out.println("Done!");

        System.out.println("File contents:");
        try (BufferedReader in = new BufferedReader(new FileReader(temp))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }

        System.out.print("Reading object... ");
        User read = null;
        try (FileInputStream fis = new FileInputStream(temp);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            SealedObject so = (SealedObject) ois.readObject();
            read = (User) efs.unsealObject(so);
        }
        System.out.println("Done!");

        System.out.println("Read object:\n" + read);

        assertEquals(u, read, "Users not equal");
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
