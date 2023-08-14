package com.example.library.controller;

import com.example.library.DTO.BookDTO;
import com.example.library.model.*;
import com.example.library.service.*;
import com.example.library.service.implementation.*;
import com.example.library.service.implementation.IReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final IReaderService readerService;
    private final IAuthorService authorService;
    private final IBookService bookService;
    private final IBookCountService bookCountService;
    private final IOrderService orderService;
    private final IRoleService roleService;

    @Autowired
    public AdminController(IReaderService readerService, IAuthorService authorService, IBookService bookService, IBookCountService bookCountService, IOrderService orderService, IRoleService roleService) {
        this.readerService = readerService;
        this.authorService = authorService;
        this.bookService = bookService;
        this.bookCountService = bookCountService;
        this.orderService = orderService;
        this.roleService = roleService;
    }

    @GetMapping("/showAddAuthorForm")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/add-author";
    }

    @PostMapping("/addAuthor")
    public String addAuthor(@ModelAttribute("author") Author author) {
        authorService.addNewAuthor(author);
        return "redirect:/author/showAddAuthorForm";
    }

    @GetMapping("/showAuthorList")
    public String showAuthorList(Model model) {
        model.addAttribute("authors", authorService.findAllAuthors());
        return "admin/author-list";
    }

    @GetMapping("/showAddBookForm")
    public String showAddBookForm(Model model) {
        model.addAttribute("bookDTO", new BookDTO());
        model.addAttribute("allAuthors", authorService.findAllAuthors());
        return "admin/add-book";
    }

    @GetMapping("/showBookList")
    public String showBookList(Model model) {
        model.addAttribute("books", bookService.findAllBooks());
        return "admin/book-list";
    }

    @PostMapping("/addBook")
    public String addBook(@ModelAttribute("bookDTO") BookDTO bookDTO, @RequestParam("authors") List<Long> authorId) {
        List<Author> selectedAuthors = authorService.findAllAuthorsById(authorId);
        bookDTO.setAuthors(selectedAuthors);
        Book book = BookDTO.mapToBook(bookDTO);
        bookService.addBook(book);

        BookCount bookCount = new BookCount();
        bookCount.setBook(book);
        bookCount.setCount(bookDTO.getBookCount());
        bookCountService.saveBookCount(bookCount);

        return "redirect:/book/showAddBookForm";
    }

    /**
     * Handles HTTP GET requests to "/order/showOrderList" URL path and then displaying a list of orders.
     *
     * @param model The Spring Model object used to convey data to the view.
     * @return The view name "admin/order-list" to display the list of orders.
     */
    @GetMapping("/showOrderList")
    public String showOrderList(Model model) {
        model.addAttribute("orders", orderService.showAllOrders());
        return "admin/order-list";
    }

    /**
     * Handles HTTP POST requests to block or unblock a user's account.
     *
     * @param id The ID of the reader whose account should be blocked or unblocked.
     * @return A redirect to the user list page after blocking/unblocking.
     */
    @PostMapping("/blockUser/{id}")
    public String blockUser(@PathVariable Long id) {
        Reader reader = readerService.findReaderById(id);
        if (reader != null) {
            reader.setBlocked(!reader.isBlocked());
            readerService.saveReader(reader);
        }
        return "redirect:/user/showUserList";
    }

    /**
     * Handles HTTP GET requests to display a list of readers (users).
     *
     * @param model The Spring Model object used to convey data to the view.
     * @return The view name "admin/user-list" to display the list of readers.
     */
    @GetMapping("/showUserList")
    public String showUserList(Model model) {
        List<Role> roleList = roleService.findAllRoleByName("USER");
        model.addAttribute("readers", readerService.findAllReadersByRoles(roleList));
        return "admin/user-list";
    }

    /**
     * Handles HTTP GET requests to display detailed information about a specific reader (user).
     *
     * @param id    The ID of the reader whose information should be displayed.
     * @param model The Spring Model object used to convey data to the view.
     * @return The view name "admin/user-info" to display detailed user information.
     */
    @GetMapping("/userInfo/{id}")
    public String showUserInfo(@PathVariable Long id, Model model) {
        Reader reader = readerService.findReaderById(id);
        model.addAttribute("reader", reader);
        return "admin/user-info";
    }


}