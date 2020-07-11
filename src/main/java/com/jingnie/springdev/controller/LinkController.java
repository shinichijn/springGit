package com.jingnie.springdev.controller;

import com.jingnie.springdev.domain.Comment;
import com.jingnie.springdev.domain.Link;
import com.jingnie.springdev.service.CommentService;
import com.jingnie.springdev.service.LinkService;
import com.jingnie.springdev.service.UserService;
import com.jingnie.springdev.domain.User;
import com.jingnie.springdev.repository.RoleRepository;
import com.jingnie.springdev.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class LinkController {

    private static final Logger logger = LoggerFactory.getLogger(LinkController.class);

    private LinkService linkService;
    private CommentService commentService;
    private UserRepository userRepository;
    private UserService userService;
    private RoleRepository roleRepository;

    private Map<String,User> users = new HashMap<>();

    public LinkController(LinkService linkService, CommentService commentService,UserRepository userRepository, RoleRepository roleRepository) {
        this.linkService = linkService;
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("links",linkService.findAll());
        model.addAttribute("cont","test");
        return "link/list";
    }

    @GetMapping("/link/{id}")
    public String read(@PathVariable Long id, Model model) {
        Optional<Link> link = linkService.findById(id);
        if( link.isPresent() ) {
            Link currentLink = link.get();
            Comment comment = new Comment();
            comment.setLink(currentLink);
            model.addAttribute("comment",comment);
            model.addAttribute("link",currentLink);
            model.addAttribute("success", model.containsAttribute("success"));
            return "link/view";  
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/link/submit")
    public String newLinkForm(Model model) {
        model.addAttribute("link",new Link());
        return "link/submit";
    }

    @PostMapping("/link/submit")
    public String createLink(@Valid Link link, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if( bindingResult.hasErrors() ) {
            logger.info("Validation errors were found while submitting a new link.");
            model.addAttribute("link",link);
            return "link/submit";
        } else {
            // save our link

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentPrincipalName = authentication.getName();
            System.out.println("User's name = " + currentPrincipalName);
            Optional<User> user = userRepository.findByEmail(currentPrincipalName);

            user.ifPresent(one -> {
                link.setUser(one);
            });


            linkService.save(link);
            logger.info("New link was saved successfully");
            redirectAttributes
                    .addAttribute("id",link.getId())
                    .addFlashAttribute("success",true);
            return "redirect:/link/{id}";
        }
    }

    @Secured({"ROLE_USER"})
    @PostMapping("/link/comments")
    public String addComment(@Valid Comment comment, BindingResult bindingResult){
        if( bindingResult.hasErrors() ){
            logger.info("There was a problem adding a new comment.");
        } else {
            commentService.save(comment);
            logger.info("New comment was saved successfully.");
        }
        return "redirect:/link/" + comment.getLink().getId();
    }

}
