package com.factory.api.resource;

import com.factory.api.dto.ProductionSuggestionDTO;
import com.factory.api.service.ProductionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/production-optimization")
@Produces(MediaType.APPLICATION_JSON)
public class ProductionResource {

    @Inject
    ProductionService productionService;

    @GET
    public ProductionSuggestionDTO getSuggestion() {
        return productionService.calculateOptimalProduction();
    }
}