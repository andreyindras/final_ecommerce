package com.unicesumar;

import com.unicesumar.entities.Product;
import com.unicesumar.entities.Sale;
import com.unicesumar.entities.User;
import com.unicesumar.paymentMethods.PaymentMethod;
import com.unicesumar.paymentMethods.PaymentType;
import com.unicesumar.repository.ProductRepository;
import com.unicesumar.repository.SaleRepository;
import com.unicesumar.repository.UserRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        ProductRepository listaDeProdutos = null;
        UserRepository listaDeUsuarios = null;
        SaleRepository listaDeVendas = null;

        Connection conn = null;

        // Parâmetros de conexão
        String url = "jdbc:sqlite:database.sqlite";

        // Tentativa de conexão
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                listaDeProdutos = new ProductRepository(conn);
                listaDeUsuarios = new UserRepository(conn);
                listaDeVendas = new SaleRepository(conn);
            } else {
                System.out.println("Falha na conexão.");
                System.exit(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n===MENU===");
            System.out.println("1 - Cadastrar Produto");
            System.out.println("2 - Listar Produtos");
            System.out.println("3 - Cadastrar Usuário");
            System.out.println("4 - Listar Usuários");
            System.out.println("5 - Registrar Venda");
            System.out.println("6 - Sair");
            System.out.print("\nEscolha uma opção: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.println("\n===Cadastrar Produto===");
                    System.out.print("Digite o nome do produto: ");
                    String nomeProduto = scanner.nextLine();
                    System.out.print("Digite o preço do produto: ");
                    double precoProduto = scanner.nextDouble();

                    Product novoProduto = new Product(nomeProduto, precoProduto);
                    listaDeProdutos.save(novoProduto);

                    System.out.println("Produto cadastrado com sucesso!");
                    break;
                case 2:
                    System.out.println("\n===Listar Produtos===");
                    List<Product> products = listaDeProdutos.findAll();

                    if (products.isEmpty()) {
                        System.out.println("Nenhum produto cadastrado.");
                    } else {
                        for (Product product : products) {
                            System.out.println(product);
                        }
                    }
                    break;
                case 3:
                    System.out.println("\n===Cadastrar Usuário===");
                    System.out.print("Digite o nome do usuário: ");
                    String nomeUsuario = scanner.nextLine();

                    System.out.print("Digite o e-mail do usuário: ");
                    String emailUsuario = scanner.nextLine();

                    System.out.print("Digite a senha do usuário: ");
                    String senhaUsuario = scanner.nextLine();

                    User novoUsuario = new User(nomeUsuario, emailUsuario, senhaUsuario);
                    listaDeUsuarios.save(novoUsuario);

                    System.out.println("Usuário cadastrado com sucesso!");
                    break;
                case 4:
                    System.out.println("\n===Listar Usuários===");
                    List<User> users = listaDeUsuarios.findAll();

                    if (users.isEmpty()) {
                        System.out.println("Nenhum usuário cadastrado.");
                    } else {
                        for (User user : users) {
                            System.out.println(user);
                        }
                    }
                    break;
                case 5:
                    System.out.println("\n===Registrar Venda===");
                    System.out.print("Digite o E-mail do usário: ");
                    String email = scanner.nextLine();

                    Optional<User> userOPT = listaDeUsuarios.findByEmail(email);
                    if (userOPT.isEmpty()) {
                        System.out.println("Usuário não foi encontrado!");
                    }

                    User user = userOPT.get();
                    System.out.println("Usuário encontrado: " + user.getName());

                    System.out.print("Digite os IDs dos produtos (separados por vírgula): ");
                    String IDs = scanner.nextLine();
                    String[] IDsProdutos = IDs.split(",");

                    List<Product> produtosSelecionados = new ArrayList<>();
                    double totalCompra = 0.0;

                    System.out.println("Produtos encontrados:");
                    for (String id : IDsProdutos) {
                        try {
                            UUID productId = UUID.fromString(id.trim());
                            Optional<Product> productOpt = listaDeProdutos.findById(productId);

                            if (productOpt.isPresent()) {
                                Product product = productOpt.get();
                                produtosSelecionados.add(product);
                                totalCompra += product.getPrice();
                                System.out.printf("- %s (R$ %.2f)%n", product.getName(), product.getPrice());
                            } else {
                                System.out.println("Produto com ID " + productId + " não encontrado");
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("Formato inválido do ID");
                        }
                    }

                    if (produtosSelecionados.isEmpty()) {
                        System.out.println("Nenhum produto válido selecionado!");
                        break;
                    }

                    System.out.println("\nEscolha a forma de pagamento:");
                    System.out.println("1 - Cartão de Crédito");
                    System.out.println("2 - Boleto");
                    System.out.println("3 - PIX");
                    System.out.print("Opção: ");
                    int paymentOption = scanner.nextInt();
                    scanner.nextLine();

                    PaymentType paymentType;
                    switch (paymentOption) {
                        case 1:
                            paymentType = PaymentType.CARTAO;
                            break;
                        case 2:
                            paymentType = PaymentType.BOLETO;
                            break;
                        case 3:
                            paymentType = PaymentType.PIX;
                            break;
                        default:
                            System.out.println("Opção inválida!");
                            paymentType = PaymentType.PIX;
                            break;

                    }

                    PaymentMethod paymentMethod = PaymentMethodFactory.create(paymentType);
                    PaymentManager paymentManager = new PaymentManager();
                    paymentManager.setPaymentMethod(paymentMethod);

                    System.out.println("\nAguarde, efetuando pagamento...");
                    paymentManager.pay(totalCompra);
                    Sale sale = new Sale(user, produtosSelecionados, totalCompra, paymentType, java.time.LocalDateTime.now());

                    listaDeVendas.save(sale);
                    System.out.println(sale);
                    break;
                case 6:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente");
            }

        } while (option != 6);

        scanner.close();
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
