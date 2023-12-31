package com.example.library.controller;

import com.example.library.DTO.OrderDTO;
import com.example.library.component.ListPaginationData;
import com.example.library.model.*;
import com.example.library.service.*;
import com.example.library.service.implementation.ReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final IReaderService readerService;
    private final IOrderService orderService;
    private final IBookService bookService;
    private final IMessageService messageService;
    private final IFineService fineService;
    private final ListPaginationData listPaginationData;

    @Autowired
    public UserController(ReaderService readerService, IOrderService orderService, IBookService bookService, IMessageService messageService, IFineService fineService, ListPaginationData listPaginationData) {
        this.readerService = readerService;
        this.orderService = orderService;
        this.bookService = bookService;
        this.messageService = messageService;
        this.fineService = fineService;
        this.listPaginationData = listPaginationData;
    }

    @GetMapping("/showReaderOrderList")
    public String showOrderByReader(Model model, Principal principal) {
        Reader reader = readerService.findReaderByName(principal.getName());
        listPaginationData.setTotalRecords(orderService.getOrdersCountByReader(reader));
        model.addAttribute("orders", orderService.findOrdersByReaderWithPagination(reader, listPaginationData.getPage(), listPaginationData.getPageSize()));
        return "reader/reader-order-list";
    }

    @GetMapping("/showReaderMessages")
    public String showReaderMessages(Model model, Principal principal) {
        Reader reader = readerService.findReaderByName(principal.getName());
        listPaginationData.setTotalRecords(messageService.getMessagesCountByReader(reader));
        model.addAttribute("messages", messageService.findMessagesByReaderWithPagination(reader, listPaginationData.getPage(), listPaginationData.getPageSize()));
        return "reader/message-page";
    }

    @GetMapping("/showMessageInfo/{messageId}")
    public String showMessageInfo(Model model, @PathVariable Long messageId) {
        Message message = messageService.findMessageById(messageId);
        log.info(String.valueOf(message));
        model.addAttribute("message", message);
        return "reader/message-info";
    }

    /**
     * Confirms a new order and adds it to the database.
     * If the return date isn't after tomorrow,
     * the order-confirmation-page is displayed with an appropriate message.
     *
     * @param bookId The ID of the book for which the order is being confirmed.
     * @param orderDTO The data transfer object containing order details, including the return date.
     * @param reader The currently authenticated reader performing the order confirmation.
     * @param unconfirmedOrders The list of unconfirmed orders stored in the session.
     * @param model The Spring MVC model for rendering views and attributes.
     * @return A view name to redirect after the order confirmation process.
     */
    @PostMapping("/confirmOrder/{bookId}")
    public String confirmNewOrder(
            @PathVariable Long bookId,
            @ModelAttribute("orderDTO") @Valid OrderDTO orderDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal Reader reader,
            @SessionAttribute("unconfirmedOrders") List<Order> unconfirmedOrders,
            Model model) {

        if(bindingResult.hasErrors()){
            model.addAttribute("unconfirmedOrders", unconfirmedOrders);
            return "reader/order-confirmation-page";
        }
        
        removeOrderFromListByBookId(unconfirmedOrders, bookId);

        Book book = bookService.findBookById(bookId);
        int countOfAvailableBooks = bookService.getCountOfAvailableBooks(book);
        if(countOfAvailableBooks > 0) {
            Order order = new Order();
            order.setReader(reader);
            order.setBook(book);
            order.setReturned(false);
            order.setOrderDate(getCurrentDate());
            order.setReturnDate(orderDTO.getReturnDate());
            orderService.saveOrder(order);
        } else {
            model.addAttribute("notAvailableBook", "We are sorry, but this book is currently out of stock");
        }

        return "redirect:/user/showOrderConfirmationPage";
    }

    @PostMapping("/cancelOrder/{bookId}")
    public String cancelOrder(@PathVariable Long bookId, @SessionAttribute("unconfirmedOrders") List<Order> unconfirmedOrders) {
        removeOrderFromListByBookId(unconfirmedOrders, bookId);
        log.info("List after canceling order: {}", unconfirmedOrders);
        return "redirect:/user/showOrderConfirmationPage";
    }

    @GetMapping("/showOrderConfirmationPage")
    public String showConfirmOrdersPage(Model model, @SessionAttribute("unconfirmedOrders") List<Order> unconfirmedOrders) {
        log.info("Unconfirmed Orders list is: {}", unconfirmedOrders);
        model.addAttribute("unconfirmedOrders", unconfirmedOrders);
        model.addAttribute("orderDTO", new OrderDTO(new Date()));
        return "reader/order-confirmation-page";
    }

    @PostMapping("/addOrder/{bookId}")
    public String addNewOrder(@SessionAttribute("unconfirmedOrders") List<Order> unconfirmedOrders,
                              @PathVariable Long bookId, @AuthenticationPrincipal Reader reader,
                              HttpSession session) {

        Book book = bookService.findBookById(bookId);
        int countOfAvailableBooks = bookService.getCountOfAvailableBooks(book);
        if(countOfAvailableBooks > 0) {
            Order order = new Order();
            order.setReader(reader);
            order.setBook(book);
            order.setReturned(false);
            order.setOrderDate(getCurrentDate());
            order.setReturnDate(getCurrentDate());
            unconfirmedOrders.add(order);
        } else {
            Map<Long, String> notAvailableBooks = new HashMap<>();
            notAvailableBooks.put(bookId, "We are sorry, but this book is currently out of stock");
            session.setAttribute("notAvailableBook", notAvailableBooks);
        }

        return "redirect:/catalog/showBookCatalog";
    }

    @GetMapping("/returnOrder/{id}")
    public String returnOrder(@PathVariable Long id, @AuthenticationPrincipal Reader reader) {
        List<Order> orderList = orderService.findOrdersByReader(reader);
        for (Order userOrder : orderList) {
            if(userOrder.getId().equals(id) && !userOrder.isReturned())
                orderService.setOrderStatusReturned(userOrder.getId());
        }

        return "redirect:/user/showReaderOrderList";
    }

    @GetMapping("/showFineList")
    public String showFineList(Model model, @AuthenticationPrincipal Reader reader) {
        listPaginationData.setTotalRecords(fineService.getFinesCountByReader(reader));
        model.addAttribute("fines",
                fineService.findAllFinesByReaderWithPagination(listPaginationData.getPage(), listPaginationData.getPageSize(), reader.getId()));
        return "reader/fine-list-reader";
    }

    @PostMapping("/payFine/{fineId}")
    public String payFine(@AuthenticationPrincipal Reader reader, @PathVariable Long fineId) {
        Fine fine = fineService.getFineById(fineId);
        if(fine.getOrder().getReader().equals(reader) && !fine.isPaid()){
            fine.setPaid(true);
            fineService.updateFine(fine);
        }
        return "redirect:/user/showFineList";
    }

    private void removeOrderFromListByBookId(List<Order> orders, Long bookId){
        for (Order order : orders) {
            if(order.getBook().getId().equals(bookId)){
                orders.remove(order);
                break;
            }
        }
    }

    private Date getCurrentDate(){
        LocalDate currentDate = LocalDate.now();
        return java.sql.Date.valueOf(currentDate);
    }
}
