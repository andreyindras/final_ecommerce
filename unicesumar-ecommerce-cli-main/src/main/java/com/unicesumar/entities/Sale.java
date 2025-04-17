package com.unicesumar.entities;

import com.unicesumar.paymentMethods.PaymentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Sale extends Entity {
    private User client;
    private List<Product> products;
    private double totalValue;
    private PaymentType paymentType;
    private LocalDateTime saleDate;

    public Sale(User client, List<Product> products, double totalValue, PaymentType paymentType, LocalDateTime saleDate) {
        this.client = client;
        this.products = products;
        this.totalValue = totalValue;
        this.paymentType = paymentType;
        this.saleDate = saleDate;
    }

    public Sale(UUID uuid, User client, List<Product> products, double totalValue, PaymentType paymentType, LocalDateTime saleDate) {
        super(uuid);
        this.client = client;
        this.products = products;
        this.totalValue = totalValue;
        this.paymentType = paymentType;
        this.saleDate = saleDate;
    }

    public User getClient() {
        return client;
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n===Resumo da venda===\n");
        sb.append("Cliente: ").append(client.getName()).append("\n");
        sb.append("Produtos:\n");
        products.forEach(p -> sb.append("- ").append(p.getName()).append("\n"));
        sb.append("Valor total: R$ ").append(String.format("%.2f", totalValue)).append("\n");
        sb.append("Pagamento: ").append(paymentType).append("\n");
        sb.append("\nVenda registrada com sucesso!\n");
        return sb.toString();
    }
}
