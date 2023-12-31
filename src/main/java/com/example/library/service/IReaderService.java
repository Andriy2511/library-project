package com.example.library.service;

import com.example.library.model.Reader;
import com.example.library.model.Role;

import java.util.List;

/**
 * The {@code IReaderService} interface defines methods for managing reader-related operations
 * within the library management system.
 */
public interface IReaderService {
    /**
     * The method saves the user to the database
     * @param reader An object of the Reader class which should have the following parameters: username, password, email, role
     */
    void registerUser(Reader reader);

    /**
     * The method checks if the user with current name exists in the database
     * @param username Users username
     * @return If the user exists the method returns true, otherwise, it returns false
     */

    boolean isUserExistCheckByUsername(String username);

    /**
     * The method checks if the user with current email exists in the database
     * @param email Users email
     * @return If the user exists the method returns true, otherwise, it returns false
     */
    boolean isUserExistCheckByEmail(String email);

    /**
     * Finds a reader by their username.
     *
     * @param username The username of the reader.
     * @return The reader with the given username, or {@code null} if not found.
     */
    Reader findReaderByName(String username);

    /**
     * Finds a reader by their unique identifier.
     *
     * @param id The unique identifier of the reader.
     * @return The reader with the given ID, or {@code null} if not found.
     */
    Reader findReaderById(Long id);

    /**
     * Saves a reader's information to the database.
     *
     * @param reader The reader object to be saved.
     */
    void saveReader(Reader reader);

    /**
     * Retrieves a list of readers based on their roles.
     *
     * @param roleList A list of roles for filtering readers.
     * @return A list of readers with the specified roles.
     */
    List<Reader> findAllReadersByRoles(List<Role> roleList);

    /**
     * Retrieves a paginated list of readers who possess any of the specified roles.
     *
     * @param roleList        The list of roles to filter readers by.
     * @param numberOfPage    The page number (starting from 1) to retrieve.
     * @param recordsPerPage  The maximum number of readers to include on each page.
     * @return A list of Reader objects who have any of the specified roles, within the specified page and records per page.
     */
    List<Reader> findAllReadersByRolesWithPagination(List<Role> roleList, int numberOfPage, int recordsPerPage);

    /**
     * Retrieves the count of readers who possess a specific role.
     *
     * @param role The role for which to count readers.
     * @return The total number of readers who have the specified role.
     */
    Long getCountReadersByRole(Role role);
}

