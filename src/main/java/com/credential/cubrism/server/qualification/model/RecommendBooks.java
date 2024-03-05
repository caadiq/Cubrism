package com.credential.cubrism.server.qualification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "RecommendBooks")
public class RecommendBooks {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "books_id", nullable = false)
    private UUID booksId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "authors", nullable = false)
    private String authors;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "sale_price")
    private Integer salePrice;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code")
    private QualificationDetails qualificationDetails;
}
