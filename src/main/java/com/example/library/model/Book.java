package com.example.library.model;

import lombok.*;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String photo;
    private String description;
    private Date publicationDate;

    @OneToOne(mappedBy = "book", cascade = CascadeType.REMOVE)
    @PrimaryKeyJoinColumn
    @ToString.Exclude
    private BookCount bookCount;

    @ManyToMany
    @JoinTable(
            name = "authors_books",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @ToString.Exclude
    private List<Author> authors;

    @OneToMany(mappedBy = "book")
    @ToString.Exclude
    private List<Order> orders;
}
