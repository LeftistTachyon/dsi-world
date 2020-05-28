package com.github.leftisttachyon.dsiworld.controller;

import com.github.leftisttachyon.dsiworld.data.Repository;
import com.github.leftisttachyon.dsiworld.data.User;
import com.github.leftisttachyon.dsiworld.model.BlobModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

        return "member/repo/repos";
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
     * @throws IOException     if something goes wrong with I/O operations
     * @throws GitAPIException if something goes wrong with Git
     */
    @GetMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}")
    public String showRepo(@SessionAttribute("user") User user, @PathVariable String id, @PathVariable String name,
                           HttpServletResponse response, Model model) throws IOException, GitAPIException {
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

        Git git = repo.getGit();
        Status status = git.status().call();
        Set<String> unstaged = status.getUncommittedChanges()
                .stream().map(s -> s.replace("/", " / ")).collect(Collectors.toSet()),
                staged = status.getAdded().stream().map(s -> s.replace("/", " / ")).collect(Collectors.toSet());

        log.trace("unstaged: {}", unstaged);
        model.addAttribute("unstaged", unstaged);
        log.trace("staged: {}", staged);
        model.addAttribute("staged", staged);

        String branch = git.getRepository().getBranch();
        model.addAttribute("branch", branch);

        model.addAttribute("name", repo.getName());
        model.addAttribute("username", user.getUsername());
        return "member/repo/repo";
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
                               HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException {
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
        model.addAttribute("id", id);
        model.addAttribute("redirect_to", uri);

        // base
        String base = uri.substring(0, splitIdx);
        model.addAttribute("base", base);

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

            return "member/repo/blobFolder";
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

            return "member/repo/blobFile";
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
    @PostMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}/save-repo")
    public String saveRepo(@SessionAttribute("user") User user, @RequestParam("chunk") List<String> chunks,
                           @PathVariable String id, @PathVariable String name,
                           @RequestParam("redirect_to") String redirectTo, HttpServletResponse response)
            throws IOException {
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

        log.info("Saving {}/{}", user.getUsername(), repo.getName());

        int splitIdx = redirectTo.indexOf("blob");
        String relPath = redirectTo.substring(splitIdx + 5);
        File f = repo.getFile(relPath);

        try (PrintWriter out = new PrintWriter(f)) {
//            log.debug("{}:", relPath);
            for (String chunk : chunks) {
                out.println(chunk);
//                log.debug(chunk);
            }
        }

        repo.save();

        return "redirect:" + redirectTo;
    }

    /**
     * Directs the server to create a new file.
     *
     * @param user               the user associated with this session
     * @param fileName           the name of the file to create
     * @param id                 the id of the user who owns this repo
     * @param name               the name of the repo
     * @param redirectTo         the url to redirect the user to afterwards
     * @param response           a {@link HttpServletResponse} object to send back error codes as needed
     * @param redirectAttributes attributes to show the redirected view
     * @return the next view to show the user
     * @throws IOException if something goes wrong with I/O operations
     */
    @PostMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}/create-file")
    public String createFile(@SessionAttribute("user") User user, @RequestParam("file-name") String fileName,
                             @PathVariable String id, @PathVariable String name,
                             @RequestParam("redirect_to") String redirectTo, HttpServletResponse response,
                             RedirectAttributes redirectAttributes) throws IOException {
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
        if (repo.createFile(relPath + File.separator + fileName)) {
            // success!
            repo.save();

            redirectAttributes.addFlashAttribute("color", "#228B22");
            redirectAttributes.addFlashAttribute("message", "File successfully created.");
        } else {
            // it failed
            redirectAttributes.addFlashAttribute("color", "red");
            redirectAttributes.addFlashAttribute("message", "The file could not be created.");
        }

        return "redirect:" + redirectTo;
    }

    /**
     * Deletes the given file from the given repo.
     *
     * @param user               the user trying to delete the file
     * @param id                 the id of the user who owns this repo
     * @param name               the name of the repository
     * @param redirectTo         where to redirect to after everything is done
     * @param response           the {@link HttpServletResponse} object used to send back error codes, if needed
     * @param redirectAttributes attributes to show the redirected page
     * @return the next view to show the user
     * @throws IOException if something goes wrong while manipulating files
     */
    @PostMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}/delete-file")
    public String deleteFile(@SessionAttribute("user") User user, @PathVariable String id,
                             @PathVariable String name, @RequestParam("redirect_to") String redirectTo,
                             HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException {
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
        boolean deleted = true;
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) for (File file : files) {
                if (!file.delete()) {
                    deleted = false;
                    break;
                }
            }

            if (deleted) deleted = f.delete();
        } else {
            deleted = f.delete();
        }

        if (deleted) {
            // success!
            repo.save();

            redirectAttributes.addFlashAttribute("color", "#228B22");
            redirectAttributes.addFlashAttribute("message", "The file has been successfully deleted.");

            return "redirect:" + redirectTo + "..";
        } else {
            // it failed
            redirectAttributes.addFlashAttribute("color", "red");
            redirectAttributes.addFlashAttribute("message", "This file could not be deleted.");

            return "redirect:" + redirectTo;
        }
    }

    /**
     * Shows a log of previous commits.
     *
     * @param user     the user trying to access the log of this repository
     * @param id       the id of the owner of the repository
     * @param name     the name of the repository
     * @param num      the number of log entries to show
     * @param response a {@link HttpServletResponse} object used to send back error codes
     * @param model    attributes to show the view
     * @return the view to show the user
     * @throws IOException     if something goes wrong with I/O operations
     * @throws GitAPIException if something goes wrong with the git command
     */
    @GetMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}/log")
    public String showLog(@SessionAttribute User user, @PathVariable String id, @PathVariable String name,
                          @RequestParam(value = "num", defaultValue = "10") int num, HttpServletResponse response,
                          Model model) throws IOException, GitAPIException {
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

        Git git = repo.getGit();
        if (git == null) {
            log.error("Yo tf it's opened but git is null?!");
            return "redirect:/member/repos";
        }

        List<String[]> commitData = new LinkedList<>();
        Iterator<RevCommit> iter = git.log().call().iterator();
        for (int i = 0; i < num && iter.hasNext(); i++) {
            RevCommit r = iter.next();

            // need full SHA1 hash, author, date, full message
            PersonIdent author = r.getAuthorIdent();
            int commitTime = r.getCommitTime();
            OffsetDateTime offsetTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(commitTime),
                    author.getTimeZone().toZoneId());

            commitData.add(new String[]{
                    r.name(), /* SHA1 hash */
                    author.getName() + " <" + author.getEmailAddress() + ">", /* author into */
                    offsetTime.toString(), /* date / time */
                    r.getFullMessage() /* full message */
            });
        }
        model.addAttribute("commit_data", commitData);

        return "member/repo/log";
    }

    /**
     * Adds files in the repository that match the given file pattern.
     *
     * @param user     the user trying to access the log of this repository
     * @param id       the id of the owner of the repository
     * @param name     the name of the repository
     * @param response a {@link HttpServletResponse} object used to send back error codes
     * @param pattern  the file pattern to match
     * @return the next view to show the user
     * @throws IOException     if I/O operations cannot be completed successfully
     * @throws GitAPIException if something goes wrong with calling Git
     */
    @PostMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}/add")
    public String addFiles(@SessionAttribute User user, @PathVariable String id, @PathVariable String name,
                           @RequestParam("pattern") String pattern, HttpServletResponse response)
            throws IOException, GitAPIException {
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

        Git git = repo.getGit();
        if (git == null) {
            log.error("Yo tf it's opened but git is null?!");
            return "redirect:/member/repos";
        }

        log.debug("Adding file pattern '{}'", pattern);
        git.add().addFilepattern(pattern).call();
        git.add().setUpdate(true).addFilepattern(pattern).call();

        return "redirect:..";
    }

    /**
     * Commits the currently staged files of the repository onto the working tree.
     *
     * @param user          the user who is attempting to perform the action
     * @param id            the ID of the user who owns the repository
     * @param name          the name of the repository
     * @param authorName    the name of the author of the commit
     * @param authorEmail   the email of the author of the commit
     * @param commiterName  the name of the commiter
     * @param commiterEmail the email of the commiter
     * @param response      the {@link HttpServletResponse} object to send back error codes with
     * @return the next view to show the user
     * @throws IOException     if something goes wrong with I/O operations
     * @throws GitAPIException if something goes wrong with calling Git
     */
    @PostMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}/commit")
    public String commit(@SessionAttribute User user, @PathVariable String id, @PathVariable String name,
                         @RequestParam("authorName") String authorName,
                         @RequestParam(value = "authorEmail", required = false) String authorEmail,
                         @RequestParam("commiterName") String commiterName,
                         @RequestParam(value = "commiterEmail", required = false) String commiterEmail,
                         HttpServletResponse response)
            throws IOException, GitAPIException {
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

        Git git = repo.getGit();
        if (git == null) {
            log.error("Yo tf it's opened but git is null?!");
            return "redirect:/member/repos";
        }

        repo.commit(authorName, authorEmail, commiterName, commiterEmail);

        return "redirect:..";
    }

    /**
     * Pushes the current working tree to the remote repository.
     *
     * @param user     the user trying to access the repository
     * @param id       the ID of the user who owns this repo
     * @param name     the name of the repo
     * @param response the {@link HttpServletResponse} object to send back error codes with
     * @return the next view to show the user
     * @throws IOException     if something goes wrong with I/O operations
     * @throws GitAPIException if something goes wrong with Git
     */
    @PostMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}/push")
    public String push(@SessionAttribute User user, @PathVariable String id, @PathVariable String name,
                       HttpServletResponse response) throws IOException, GitAPIException {
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

        Git git = repo.getGit();
        if (git == null) {
            log.error("Yo tf it's opened but git is null?!");
            return "redirect:/member/repos";
        }

        git.push().setCredentialsProvider(repo.getCreds()).call();

        return "redirect:..";
    }

    /**
     * Pulls changes from the remote repository.
     *
     * @param user     the user trying to access the repository
     * @param id       the ID of the user who owns this repo
     * @param name     the name of the repo
     * @param response the {@link HttpServletResponse} object to send back error codes with
     * @return the next view to show the user
     * @throws IOException     if something goes wrong with I/O operations
     * @throws GitAPIException if something goes wrong with Git
     */
    @PostMapping("/member/repos/{id:[a-z\\d]{3,12}}/{name}/pull")
    public String pull(@SessionAttribute User user, @PathVariable String id, @PathVariable String name,
                       HttpServletResponse response) throws IOException, GitAPIException {
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

        Git git = repo.getGit();
        if (git == null) {
            log.error("Yo tf it's opened but git is null?!");
            return "redirect:/member/repos";
        }

        git.pull().call();

        return "redirect:..";
    }
}
