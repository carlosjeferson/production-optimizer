package com.factory.api.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "raw_materials")
public class RawMaterial extends PanacheEntity {
    @NotBlank
    @Column(unique = true)
    public String code;

    @NotBlank
    public String name;

    @Min(0)
    public Double quantity;
}