package com.factory.api.service;

import com.factory.api.dto.ProductionSuggestionDTO;
import com.factory.api.model.Product;
import com.factory.api.model.ProductComposition;
import com.factory.api.model.RawMaterial;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ProductionService {

    public ProductionSuggestionDTO calculateOptimalProduction() {
        Loader.loadNativeLibraries();

        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            throw new RuntimeException("Não foi possível inicializar o Solver SCIP.");
        }

        List<Product> products = Product.listAll();
        List<RawMaterial> inventory = RawMaterial.listAll();

        Map<Product, MPVariable> productVariables = new HashMap<>();
        for (Product product : products) {
            MPVariable var = solver.makeIntVar(0.0, Double.POSITIVE_INFINITY, product.name);
            productVariables.put(product, var);
        }

        Map<Long, MPConstraint> inventoryConstraints = new HashMap<>();
        for (RawMaterial material : inventory) {
            MPConstraint constraint = solver.makeConstraint(0.0, material.quantity, "Material_" + material.id);
            inventoryConstraints.put(material.id, constraint);
        }

        for (Product product : products) {
            MPVariable productVar = productVariables.get(product);
            for (ProductComposition comp : product.composition) {
                MPConstraint constraint = inventoryConstraints.get(comp.rawMaterial.id);
                if (constraint != null) {
                    constraint.setCoefficient(productVar, comp.requiredQuantity);
                }
            }
        }

        MPObjective objective = solver.objective();
        for (Product product : products) {
            objective.setCoefficient(productVariables.get(product), product.price);
        }
        objective.setMaximization();

        MPSolver.ResultStatus resultStatus = solver.solve();

        Map<String, Integer> suggestedProduction = new HashMap<>();
        double totalValue = 0.0;

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            for (Product product : products) {
                int qtyToProduce = (int) productVariables.get(product).solutionValue();
                if (qtyToProduce > 0) {
                    suggestedProduction.put(product.name, qtyToProduce);
                }
            }
            totalValue = objective.value();
        } else {
            System.err.println("O solver não conseguiu encontrar uma solução ideal.");
        }

        return new ProductionSuggestionDTO(suggestedProduction, totalValue);
    }
}