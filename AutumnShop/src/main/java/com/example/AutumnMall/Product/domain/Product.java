package com.example.AutumnMall.Product.domain;

import com.example.AutumnMall.Member.domain.Favorites;
import com.example.AutumnMall.Member.domain.RecentProduct;
import com.example.AutumnMall.utils.audit.Auditable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Double price;

    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String imageUrl;

    @Embedded
    private Rating rating;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Favorites> favorites = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RecentProduct> recentProducts = new ArrayList<>();
}

