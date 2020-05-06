package com.github.leftisttachyon.dsiworld.controller;

import com.github.leftisttachyon.dsiworld.data.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * A class that acts as a controller for menial tasks.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Controller
@Slf4j
public class SiteController {
    /**
     * Displays to the user the home page.
     *
     * @param user  the {@link User} object associated with the session, if any exists
     * @param model the attributes to show the view page
     * @return the view to show the user
     */
    @GetMapping("/")
    public String index(@SessionAttribute(value = "user", required = false) User user, Model model) {
        if (user != null) {
            model.addAttribute("welcome_text", "Hello there, " + user.getUsername() + "!");
        }

        return "index";
    }

    /**
     * Displays to the user the login age.
     *
     * @param user the {@link User} object associated with the session, if any exists
     * @return the view to show the user
     */
    @GetMapping("/login")
    public String login(@SessionAttribute(value = "user", required = false) User user) {
        return user == null ? "login" : "redirect:/member";
    }

    /**
     * Displays to the user the register page.
     *
     * @param user the {@link User} object associated with the session, if any exists
     * @return the view to show the user
     */
    @GetMapping("/register")
    public String register(@SessionAttribute(value = "user", required = false) User user) {
        return user == null ? "register" : "redirect:/member";
    }
}
