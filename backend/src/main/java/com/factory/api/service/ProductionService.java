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
        // 1. Carrega as bibliotecas nativas do Google OR-Tools (obrigatório)
        Loader.loadNativeLibraries();

        // 2. Cria o solver usando o backend SCIP (excelente para números inteiros)
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            throw new RuntimeException("Não foi possível inicializar o Solver SCIP.");
        }

        List<Product> products = Product.listAll();
        List<RawMaterial> inventory = RawMaterial.listAll();

        // 3. VARIÁVEIS: O que queremos descobrir? (Quantos de cada produto fabricar)
        // Guardamos as variáveis em um mapa para recuperar o resultado depois
        Map<Product, MPVariable> productVariables = new HashMap<>();
        for (Product product : products) {
            // makeIntVar(min, max, nome) -> Garante que não vamos fabricar produtos "quebrados" ou negativos
            MPVariable var = solver.makeIntVar(0.0, Double.POSITIVE_INFINITY, product.name);
            productVariables.put(product, var);
        }

        // 4. RESTRIÇÕES: O que nos limita? (Nosso estoque de matéria-prima)
        Map<Long, MPConstraint> inventoryConstraints = new HashMap<>();
        for (RawMaterial material : inventory) {
            // constraint(min, max, nome) -> O uso do material não pode passar da quantidade que temos
            MPConstraint constraint = solver.makeConstraint(0.0, material.quantity, "Material_" + material.id);
            inventoryConstraints.put(material.id, constraint);
        }

        // Vinculando a composição dos produtos às restrições de estoque
        for (Product product : products) {
            MPVariable productVar = productVariables.get(product);
            for (ProductComposition comp : product.composition) {
                MPConstraint constraint = inventoryConstraints.get(comp.rawMaterial.id);
                if (constraint != null) {
                    // Ex: constraint.setCoefficient(VarProdutoMadeira, 10)
                    constraint.setCoefficient(productVar, comp.requiredQuantity);
                }
            }
        }

        // 5. OBJETIVO: O que queremos maximizar? (O lucro total)
        MPObjective objective = solver.objective();
        for (Product product : products) {
            objective.setCoefficient(productVariables.get(product), product.price);
        }
        objective.setMaximization();

        // 6. RESOLUÇÃO: Manda o motor matemático encontrar a melhor resposta
        MPSolver.ResultStatus resultStatus = solver.solve();

        Map<String, Integer> suggestedProduction = new HashMap<>();
        double totalValue = 0.0;

        // 7. EXTRAÇÃO DOS RESULTADOS
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            for (Product product : products) {
                // Pega o valor calculado pelo solver para esta variável
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