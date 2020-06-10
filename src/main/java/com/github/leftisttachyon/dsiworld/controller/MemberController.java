package com.github.leftisttachyon.dsiworld.controller;

import com.github.leftisttachyon.dsiworld.data.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * A class that acts as a controller for member-controlled pages.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Slf4j
@Controller
public class MemberController {
    /**
     * Directs the user towards the member home.
     *
     * @param user  the {@link User} object associated with this user
     * @param model the attributes to show the view
     * @return the view to show the user
     */
    @GetMapping("/member")
    public String memberHome(@SessionAttribute("user") User user, Model model) {
        model.addAttribute("username", user.getUsername());
        return "member/index";
    }

}
