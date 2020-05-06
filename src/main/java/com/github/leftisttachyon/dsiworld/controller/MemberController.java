package com.github.leftisttachyon.dsiworld.controller;

import com.github.leftisttachyon.dsiworld.data.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * A class that acts as a controller for member-controlled pages.
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

    /**
     * Directs the user towards the member repos home page.
     *
     * @param user  the {@link User} object associated with this user
     * @param model the attributes to show the view
     * @return the view to show the user
     */
    @GetMapping("/member/repos")
    public String memberRepos(@SessionAttribute("user") User user, Model model) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("id", user.getId());

        user.loadRepositories();
        model.addAttribute("repos", user.getRepositories());

        return "member/repos";
    }
}
