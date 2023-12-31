package com.example.library.service;

import com.example.library.model.Author;

import java.util.List;

/**
 * The {@code IAuthorService} interface defines methods for interacting with author-related operations
 * within the library management system.
 */
public interface IAuthorService {

    /**
     * Adds a new author to the library's collection.
     *
     * @param author The author to be added.
     */
    void addNewAuthor(Author author);

    /**
     * Retrieves a list of all authors present in the library.
     *
     * @return A list containing all authors.
     */
    List<Author> findAllAuthors();

    /**
     * Retrieves a list of authors based on a list of author IDs.
     *
     * @param authorId A list of author IDs for which authors are to be retrieved.
     * @return A list of authors corresponding to the given author IDs.
     */
    List<Author> findAllAuthorsById(List<Long> authorId);

    /**
     * Retrieves an author by their unique identifier.
     *
     * @param id The unique identifier of the author to be retrieved.
     * @return The author corresponding to the given ID, or {@code null} if not found.
     */
    Author findAuthorById(Long id);

    /**
     * Retrieves the total count of authors in the system.
     *
     * @return The total number of authors.
     */
    Long getAuthorsCount();

    /**
     * Retrieves a list of authors with pagination support.
     *
     * @param page The page number to retrieve.
     * @param pageSize The maximum number of authors to include on each page.
     * @return A list of Author objects within the specified page and page size.
     */
    List<Author> findAllAuthorsWithPagination(int page, int pageSize);
}