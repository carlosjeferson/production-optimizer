package com.factory.api.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_compositions")
public class ProductComposition extends PanacheEntity {
    @ManyToOne
    public RawMaterial rawMaterial;

    public Double requiredQuantity;
}