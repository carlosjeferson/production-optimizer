package com.factory.api.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "products")
public class Product extends PanacheEntity {
    @NotBlank
    @Column(unique = true)
    public String code;

    @NotBlank
    public String name;

    @Min(0)
    public Double price;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @Valid
    public List<ProductComposition> composition = new ArrayList<>();
}