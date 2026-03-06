package com.factory.api.resource;

import com.factory.api.model.Product;
import com.factory.api.model.ProductComposition;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @GET
    public List<Product> getAll() {
        return Product.listAll();
    }

    @POST
    @Transactional
    public Response create(@Valid Product product) {
        product.persist();
        return Response.status(Response.Status.CREATED).entity(product).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Product update(@PathParam("id") Long id, @Valid Product product) {
        Product entity = Product.findById(id);
        if (entity == null) {
            throw new NotFoundException("Product not found with id: " + id);
        }

        entity.code = product.code;
        entity.name = product.name;
        entity.price = product.price;

        entity.composition.clear();

        if (product.composition != null) {
            for (ProductComposition newComp : product.composition) {
                newComp.persist();
                entity.composition.add(newComp);
            }
        }

        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public boolean delete(@PathParam("id") Long id) {
        return Product.deleteById(id);
    }
}