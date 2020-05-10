package com.github.leftisttachyon.dsiworld.controller;

import com.github.leftisttachyon.dsiworld.data.Repository;
import com.github.leftisttachyon.dsiworld.data.User;
import com.github.leftisttachyon.dsiworld.model.BlobModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * Clones the given repository.
     *
     * @param user               the {@link User} associated with the given user.
     * @param url                the url to clone the repository from
     * @param name               the name to give the repository
     * @param username           the username, if any, to use as credentials
     * @param password           the password, if any, to use as credentials
     * @param redirectAttributes attributes to show the view
     * @return the next view to show the user
     */
    @PostMapping("/member/clone")
    public String cloneRepo(@SessionAttribute("user") User user, @RequestParam("url") String url,
                            @RequestParam("name") String name, @RequestParam("username") String username,
                            @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        if (name.equals("blob")) {
            redirectAttributes.addFlashAttribute("reject_reason",
                    "The name \"blob\" is forbidden.");
            return "redirect:/member/repos";
        }

        for (Repository r : user.getRepositories()) {
            if (r.getName().equals(name)) {
                redirectAttributes.addFlashAttribute("reject_reason",
                        "The name \"" + name + "\" has already been taken.");
                return "redirect:/member/repos";
            }
        }

        BlobModel repoBlob = user.newRepoBlob(name);
        Repository newRepo = new Repository(repoBlob, name);
        try {
            if (!username.isEmpty() && !password.isEmpty()) {
                newRepo.clone(url, username, password);
            } else {
                newRepo.clone(url);
            }
        } catch (IOException e) {
            log.warn("An IOException occurred while cloning a repo", e);
            redirectAttributes.addFlashAttribute("reject_reason",
                    "An error occurred while cloning the repository.");
            return "redirect:/member/repos";
        }

        user.addRepository(newRepo);

        return "redirect:/member/repos";
    }

    /**
     * Loads the given repository owned by the current user.
     *
     * @param user               the user whose repository should be loaded
     * @param name               the name of the repository to load
     * @param redirectAttributes a set of attributes to show the redirected view
     * @return the next view to show the user
     */
    @PostMapping("/member/load-repo")
    public String loadRepo(@SessionAttribute("user") User user, @RequestParam("repo") String name,
                           RedirectAttributes redirectAttributes) {
        Repository toOpen = user.getRepository(name);
        if (toOpen == null) {
            redirectAttributes.addFlashAttribute("reject_reason", "That repository couldn't be found.");
        } else {
            try {
                toOpen.fetch();
            } catch (IOException e) {
                log.warn("An IOException occurred while loading a repo", e);

                redirectAttributes.addFlashAttribute("reject_reason",
                        "An error occurred while loading this repository. Please try again.");
            }
        }

        return "redirect:/member/repos";
    }

    /**
     * Shows the repository home page.
     *
     * @param user     the user attempting to view the page
     * @param id       the id of the user who owns the repo
     * @param name     the name of the repo
     * @param response a {@link HttpServletResponse} object to send back error codes if necessary
     * @param model    attributes to show the view
     * @return the next view to show the user
     * @throws IOException if something goes wrong with I/O operations
     */
    @GetMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}")
    public String showRepo(@SessionAttribute("user") User user, @PathVariable String id, @PathVariable String name,
                           HttpServletResponse response, Model model) throws IOException {
        if (!user.getId().equals(id)) {
            log.debug("Blocked request of somebody else's repo");
            response.sendError(404);
            return "error";
        }

        Repository repo = user.getRepository(name);
        if (repo == null) {
            log.debug("Requested repo that doesn't exist");
            response.sendError(404);
            return "error";
        }

        if (!repo.isOpened()) {
            log.debug("Trying to read from an unopened repo?!");
            return "redirect:/member/repos";
        }

        model.addAttribute("name", repo.getName());
        return "member/repo";
    }

    /**
     * Shows a repository blob page.
     *
     * @param user     the user trying to access the repo
     * @param id       the id of the owner of the repo
     * @param name     the name of the repo
     * @param request  the request made by the user
     * @param response the response to send back to the user
     * @param model    attributes to show to the view
     * @return the next view to show to the user
     * @throws IOException if something goes wrong with I/O operations
     */
    @GetMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}/blob/**")
    public String showRepoBlob(@SessionAttribute("user") User user, @PathVariable String id, @PathVariable String name,
                               HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        if (!user.getId().equals(id)) {
            log.debug("Blocked request of somebody else's repo");
            response.sendError(404);
            return "error";
        }

        Repository repo = user.getRepository(name);
        if (repo == null) {
            log.debug("Requested repo that doesn't exist");
            response.sendError(404);
            return "error";
        }

        if (!repo.isOpened()) {
            log.debug("Trying to read from an unopened repo?!");
            return "redirect:/member/repos";
        }

        String uri = request.getRequestURI();
        int splitIdx = uri.indexOf("blob");
        String relPath = uri.substring(splitIdx + 5);
        File f = repo.getFile(relPath);
        if (f == null) {
            log.debug("Requested file '{}' that doesn't exist", relPath);
            response.sendError(400); // bad request
            return "error";
        }

        model.addAttribute("name", repo.getName());
        model.addAttribute("path", relPath.isEmpty() ? ""
                : relPath.substring(0, relPath.length() - 1).replace("/", " / "));
        model.addAttribute("username", user.getUsername());

        if (f.isDirectory()) {
            // dirs, files
            List<File> dirs = new ArrayList<>(), files = new ArrayList<>();
            for (File file : Objects.requireNonNull(f.listFiles())) {
                if (file.isHidden()) continue;
                if (file.isDirectory()) {
                    dirs.add(file);
                } else {
                    files.add(file);
                }
            }
            model.addAttribute("dirs", dirs);
            model.addAttribute("files", files);

            // base
            String base = uri.substring(splitIdx);
            model.addAttribute("base", base);

            return "member/blobFolder";
        } else {
            // chunks
            List<String> chunks = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            try (FileReader fReader = new FileReader(f);
                 BufferedReader in = new BufferedReader(fReader)) {
                String line;
                while ((line = in.readLine()) != null) {
                    if (builder.length() + line.length() > 1000) {
                        chunks.add(builder.toString());
                        builder = new StringBuilder();
                    }

                    builder.append(line);
                    builder.append('\n');
                }
            }
            chunks.add(builder.toString());
            model.addAttribute("chunks", chunks);

            // id
            model.addAttribute("id", id);

            // redirect_to
            model.addAttribute("redirect_to", uri);

            return "member/blobFile";
        }
    }

    /**
     * Writes the changes to this file, then saves this repository.
     *
     * @param user       the user attempting to save the repo
     * @param chunks     the chunks of data to save
     * @param id         the id of the user who owns the repo
     * @param name       the name of the repo
     * @param redirectTo the URL to redirect to
     * @param response   the response to send back to the user
     * @return the view to show the user
     * @throws IOException if something goes wrong with I/O operations
     */
    @PostMapping("/member/save-repo")
    public String saveRepo(@SessionAttribute("user") User user, @RequestParam("chunk") List<String> chunks,
                           @RequestParam("id") String id, @RequestParam("name") String name,
                           @RequestParam("redirect_to") String redirectTo, HttpServletResponse response) throws IOException {
        if (!user.getId().equals(id)) {
            log.debug("Blocked request of somebody else's repo");
            response.sendError(404);
            return "error";
        }

        Repository repo = user.getRepository(name);
        if (repo == null) {
            log.debug("Requested repo that doesn't exist");
            response.sendError(404);
            return "error";
        }

        if (!repo.isOpened()) {
            log.debug("Trying to read from an unopened repo?!");
            return "redirect:/member/repos";
        }

        int splitIdx = redirectTo.indexOf("blob");
        String relPath = redirectTo.substring(splitIdx + 5);
        File f = repo.getFile(relPath);

        try (PrintWriter out = new PrintWriter(f)) {
            for (String chunk : chunks) {
                out.println(chunk);
            }
        }

        repo.save();

        return "redirect:/" + redirectTo;
    }
}
