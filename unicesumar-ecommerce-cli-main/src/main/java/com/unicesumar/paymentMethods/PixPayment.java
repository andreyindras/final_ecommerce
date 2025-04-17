package com.unicesumar.paymentMethods;

import java.util.UUID;

public class PixPayment implements PaymentMethod {
    public void pay(double amount) {
        String chavePix = UUID.randomUUID().toString();
        System.out.printf("Pagamento confirmado com sucesso via PIX. Chave de Autenticação: %s%n", chavePix);

    }
}