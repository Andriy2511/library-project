package com.example.library.controller;

import com.example.library.DTO.ReaderDTO;
import javax.validation.Valid;

import com.example.library.model.Reader;
import com.example.library.service.IRoleService;
import com.example.library.service.implementation.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;

@Controller
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final IRoleService roleService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.roleService = roleService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, @AuthenticationPrincipal Reader reader) {
        model.addAttribute("readerDTO", new ReaderDTO());
        model.addAttribute("isAdmin" , roleService.isUserContainRole(reader, "ADMIN"));
        return "login";
    }

    @PostMapping("/authorize")
    public String loginUser(@ModelAttribute("readerDTO") @Valid ReaderDTO readerDTO, BindingResult result) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(readerDTO.getUsername(), readerDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return getPageByRole(authentication);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            result.rejectValue("username", "error.invalidCredentials", "Invalid username or password");
            return "login";
        }
    }

    private String getPageByRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            return "redirect:/admin/showAddBookForm";
        } else if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("USER"))) {
            return "redirect:/user/showReaderOrderList";
        } else {
            return "redirect:/registration";
        }
    }
}