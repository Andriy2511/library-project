package com.example.library.controller;

import com.example.library.DTO.ReaderDTO;
import com.example.library.service.ReaderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class RegistrationController {

    private final ReaderService readerService;

    public RegistrationController(ReaderService readerService){
        this.readerService = readerService;
    }

    /**
     * The method adds the readerDTO attribute of the ReaderDTO class
     * @param model A Model class object
     * @return registration.html page
     */
    @GetMapping("/registration")
    public String showRegistrationForm(Model model){
        model.addAttribute("readerDTO", new ReaderDTO());
        return "registration";
    }

    /**
     * This method is responsible for adding users to the database
     * @param readerDTO An object of the ReaderDTO class
     * @param result An object of the BindingResult class
     * @return If the user has been registered successfully, the method returns the login page, in other case, it returns the registration page
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute ("readerDTO") @Valid ReaderDTO readerDTO, BindingResult result){
        if(result.hasErrors()){
            return "registration";
        }

        try {
            if (readerService.isUserExistCheckByUsername(readerDTO.getUsername())) {
                log.info("A user with this name has already exists, username: {}", readerDTO.getUsername());
                result.rejectValue("username", "error.duplicateUsername", "Користувач з таким іменем вже існує");
                return "registration";
            }
            if (readerService.isUserExistCheckByEmail(readerDTO.getEmail())) {
                log.info("A user with this email has already exists, email: {}", readerDTO.getEmail());
                result.rejectValue("email", "error.duplicateEmail", "Така електронна пошта вже зареєстрована");
                return "registration";
            }

            readerService.registerUser(readerDTO);
            return "redirect:login";
        } catch (Exception e){
            log.error("Error while adding to the database: \n{}", e.getMessage());
            result.rejectValue( "database.addingReaderError", "Помилка під час реєстрації: " + e.getMessage());
            return "registration";
        }
    }
}
