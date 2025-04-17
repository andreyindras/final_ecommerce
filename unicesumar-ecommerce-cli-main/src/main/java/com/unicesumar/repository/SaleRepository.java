package com.unicesumar.repository;

import com.unicesumar.entities.Product;
import com.unicesumar.entities.Sale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SaleRepository implements EntityRepository<Sale> {
    private final Connection connection;

    public SaleRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Sale entity) {
        String salesQuery = "INSERT INTO sales VALUES (?, ?, ?, ?)";
        String productsQuery = "INSERT INTO sale_products VALUES (?, ?)";

        try {
            PreparedStatement stmt = this.connection.prepareStatement(salesQuery);
            stmt.setString(1, entity.getUuid().toString());
            stmt.setString(2, entity.getClient().getUuid().toString());
            stmt.setString(3, entity.getPaymentType().name());
            stmt.setTimestamp(4, Timestamp.valueOf(entity.getSaleDate()));
            stmt.executeUpdate();

            for (Product product : entity.getProducts()) {
                PreparedStatement stmt2 = this.connection.prepareStatement(productsQuery);
                stmt2.setString(1, entity.getUuid().toString());
                stmt2.setString(2, product.getUuid().toString());
                stmt2.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Sale> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {
    }
}
