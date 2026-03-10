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
    private String code;

    @NotBlank
    private String name;

    @Min(0)
    private Double price;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @Valid
    private List<ProductComposition> composition = new ArrayList<>();

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public List<ProductComposition> getComposition() { return composition; }
    public void setComposition(List<ProductComposition> composition) { this.composition = composition; }
}