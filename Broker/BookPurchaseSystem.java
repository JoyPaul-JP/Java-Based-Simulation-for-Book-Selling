import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Book {
    String title;
    double price;
    int quantity;

    public Book(String title, double price, int quantity) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }
}

class SellerAgent {
    String name;
    List<Book> catalog;

    public SellerAgent(String name) {
        this.name = name;
        this.catalog = new ArrayList<>();
    }

    // Add a book to the seller's catalog
    public void addToCatalog(Book book) {
        catalog.add(book);
    }

    // Display the catalog of the seller
    public void displayCatalog() {
        System.out.println("Catalog for Seller " + name + ":");
        for (Book book : catalog) {
            System.out.println(book.title + " - Price: \u00A3" + book.price + " - Quantity: " + book.quantity);
        }
    }

    // Provide an offer for the requested book and quantity
    public double provideOffer(String targetBook, int quantity) {
        double totalPrice = 0;
        int remainingQuantity = quantity;

        for (Book book : catalog) {
            if (book.title.equalsIgnoreCase(targetBook) && book.quantity > 0) {
                int purchaseQuantity = Math.min(book.quantity, remainingQuantity);
                totalPrice += purchaseQuantity * book.price;
                remainingQuantity -= purchaseQuantity;

                if (remainingQuantity == 0) {
                    break; // Buyer has purchased the required quantity
                }
            }
        }

        // Return -1 if the requested book is not in the catalog or couldn't fulfill the quantity
        return remainingQuantity == 0 ? totalPrice : -1;
    }

    // Serve a purchase order by updating the catalog
    public void servePurchaseOrder(String targetBook, int quantity) {
        for (Book book : catalog) {
            if (book.title.equalsIgnoreCase(targetBook) && book.quantity > 0) {
                int purchaseQuantity = Math.min(book.quantity, quantity);
                book.quantity -= purchaseQuantity;
                break;
            }
        }
    }
}

class BuyerAgent {
    String name;
    List<Book> purchasedBooks;

    public BuyerAgent(String name) {
        this.name = name.toUpperCase(); // Ensure buyer name is in uppercase
        this.purchasedBooks = new ArrayList<>();
    }

    // Simulate the search and purchase process
    public void searchAndPurchase(List<SellerAgent> sellers, Scanner scanner) {
        while (true) {
            System.out.print("Enter the target book for purchase (type 'exit' to stop): ");
            String targetBook = scanner.nextLine().toUpperCase(); // Make book name non-case sensitive

            if (targetBook.equalsIgnoreCase("exit")) {
                break;
            }

            // Find all sellers with the book in stock
            List<SellerAgent> availableSellers = new ArrayList<>();
            for (SellerAgent seller : sellers) {
                double totalPrice = seller.provideOffer(targetBook, 1); // Buyer will always buy one book
                if (totalPrice != -1) {
                    availableSellers.add(seller);
                }
            }

            // Simulate the purchase process
            if (!availableSellers.isEmpty()) {
                System.out.println(name + " purchased 1 copy of " + targetBook + " from:");

                SellerAgent bestSeller = availableSellers.get(0); // Initialize with the first seller
                double minPrice = bestSeller.provideOffer(targetBook, 1);
                for (SellerAgent seller : availableSellers) {
                    double totalPrice = seller.provideOffer(targetBook, 1);
                    if (totalPrice < minPrice) {
                        minPrice = totalPrice;
                        bestSeller = seller;
                    }
                }

                System.out.println(" - " + bestSeller.name + " for \u00A3" + minPrice);
                bestSeller.servePurchaseOrder(targetBook, 1);
                purchasedBooks.add(new Book(targetBook, minPrice, 1));

            } else {
                System.out.println("Book not available in any catalog.");
            }
        }
    }

    // Display the purchased books and total book cost
    public void displayPurchasedBooks() {
        System.out.println("\nPurchased books by " + name + ":");
        double totalBookCost = 0;

        for (Book book : purchasedBooks) {
            System.out.println(book.title + " - Price: \u00A3" + book.price + " - Quantity: " + book.quantity);
            totalBookCost += book.price * book.quantity;
        }

        System.out.println("Total Book Cost: \u00A3" + totalBookCost);
    }
}

class BrokerAgent {
    String name;
    double brokerCost;

    public BrokerAgent(String name) {
        this.name = name;
        this.brokerCost = 0.0;
    }

    // Process the purchase, calculate total cost, and update catalogs
    public void processPurchase(BuyerAgent buyer, List<SellerAgent> sellers) {
        // Calculate the total book cost including broker cost
        double totalBookCost = 0;
        for (Book book : buyer.purchasedBooks) {
            totalBookCost += book.price * book.quantity;
        }

        // Set broker cost to zero if total book cost is zero
        brokerCost = totalBookCost == 0 ? 0 : brokerCost;

        // Display the total book cost
        System.out.println("\nTotal Book Cost for " + buyer.name + ": \u00A3" + totalBookCost);

        // Display the total cost including broker cost
        double totalCost = totalBookCost + brokerCost;
        System.out.println("Total Cost for " + buyer.name + " (including broker cost): \u00A3" + totalCost);

        // Update the catalogs after the purchase
        System.out.println("\nUpdated Catalogs after purchase:");
        for (SellerAgent seller : sellers) {
            seller.displayCatalog();
            System.out.println();
        }
    }
}

public class BookPurchaseSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Creating SellerAgents
        SellerAgent seller1 = new SellerAgent("Seller1");
        seller1.addToCatalog(new Book("Book1", 20.0, 3));
        seller1.addToCatalog(new Book("Book2", 15.0, 2));
        seller1.addToCatalog(new Book("Book3", 18.0, 4));

        SellerAgent seller2 = new SellerAgent("Seller2");
        seller2.addToCatalog(new Book("Book2", 17.5, 1));
        seller2.addToCatalog(new Book("Book4", 22.0, 5));
        seller2.addToCatalog(new Book("Book5", 19.0, 3));

        SellerAgent seller3 = new SellerAgent("Seller3");
        seller3.addToCatalog(new Book("Book1", 16.0, 2));
        seller3.addToCatalog(new Book("Book4", 21.5, 1));
        seller3.addToCatalog(new Book("Book6", 25.0, 6));

        SellerAgent seller4 = new SellerAgent("Seller4");
        seller4.addToCatalog(new Book("Book7", 30.0, 8));
        seller4.addToCatalog(new Book("Book8", 18.0, 3));
        seller4.addToCatalog(new Book("Book9", 22.5, 2));

        List<SellerAgent> sellers = new ArrayList<>();
        sellers.add(seller1);
        sellers.add(seller2);
        sellers.add(seller3);
        sellers.add(seller4);

        // Asking the buyer if they want to see the catalogs
        String seeCatalogs;
        do {
            System.out.print("Do you want to see the catalogs? (yes/no): ");
            seeCatalogs = scanner.nextLine().toLowerCase();
        } while (!seeCatalogs.equals("yes") && !seeCatalogs.equals("no"));

        if (seeCatalogs.equals("yes")) {
            for (SellerAgent seller : sellers) {
                seller.displayCatalog();
                System.out.println();
            }
        }

        // Asking the buyer for their name
        String buyerName;
        do {
            System.out.print("Enter the name of the Buyer: ");
            buyerName = scanner.nextLine().replaceAll("[^a-zA-Z]", "");
        } while (buyerName.isEmpty());

        // Creating a BuyerAgent
        BuyerAgent buyer = new BuyerAgent(buyerName);

        // Asking the broker for their name and cost
        System.out.print("Enter the name of the Broker: ");
        String brokerName = scanner.nextLine();

        double brokerCost = -1.0;
        while (brokerCost < 0) {
            System.out.print("Enter the broker cost: \u00A3");
            String rawInput = scanner.nextLine().replaceAll("[^0-9.]", "");

            try {
                brokerCost = Double.parseDouble(rawInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        // Creating a BrokerAgent
        BrokerAgent broker = new BrokerAgent(brokerName);
        broker.brokerCost = brokerCost;

        // Simulating the search and purchase process
        buyer.searchAndPurchase(sellers, scanner);

        // Processing the purchase with the broker
        broker.processPurchase(buyer, sellers);

        scanner.close();
    }
}
