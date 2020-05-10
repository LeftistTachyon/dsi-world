package com.github.leftisttachyon.dsiworld.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

/**
 * A class that acts as a controller for converting images into Base 64.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Slf4j
@CrossOrigin(origins = "*")
@Controller
public class Base64Controller {
    /**
     * A page that converts an image to Base 64 and returns that.
     *
     * @param image the image to convert
     * @return the Base 64 representation of the given image.
     * @throws IOException if something goes wrong while converting the image into a byte array
     */
    @ResponseBody
    @PostMapping("/base64")
    public String toBase64(@RequestParam("image") MultipartFile image) throws IOException {
        byte[] bytes = image.getBytes();
        return "data:" + image.getContentType() + ";base64," +
                Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * A dummy page to test CORS.
     *
     * @return a String that reads "Hello from Dsi World!"
     */
    @ResponseBody
    @GetMapping("/base64")
    public String corsTest() {
        return "Hello from DSi World!";
    }

    /**
     * A page that converts an URL that links to an image to Base 64 and returns that.
     *
     * @param url_ the URL that points to the image to be converted
     * @return the Base 64 representation of the given image
     * @throws IOException if something goes wrong while converting the image to Base 64
     */
    @ResponseBody
    @GetMapping("/base64url")
    public String toBase64(@RequestParam("url") String url_) throws IOException {
        log.info("Encoding image from url {}", url_);
        URL url = new URL(url_);
        File temp = File.createTempFile("temp", ".dat");
        FileUtils.copyURLToFile(url, temp);

        byte[] bytes = FileUtils.readFileToByteArray(temp);
        String type = url.openConnection().getContentType();
        return "data:" + type + ";base64," +
                Base64.getEncoder().encodeToString(bytes);
    }
}
