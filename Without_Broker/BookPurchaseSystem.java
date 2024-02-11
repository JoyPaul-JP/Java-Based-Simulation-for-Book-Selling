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

    public void addToCatalog(Book book) {
        catalog.add(book);
    }

    public void displayCatalog() {
        System.out.println("Catalog for Seller " + name + ":");
        for (Book book : catalog) {
            System.out.println(book.title + " - Price: \u00A3" + book.price + " - Quantity: " + book.quantity);
        }
    }

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
        this.name = cleanAndValidateName(name);
        this.purchasedBooks = new ArrayList<>();
    }

    private String cleanAndValidateName(String name) {
        // Remove non-alphabetic characters
        name = name.replaceAll("[^a-zA-Z]", "");

        // If the name is empty, ask again
        while (name.isEmpty()) {
            System.out.println("Invalid name. Please enter a valid name.");
            System.out.print("Enter the name of the Buyer: ");
            name = new Scanner(System.in).nextLine().replaceAll("[^a-zA-Z]", "");
        }

        return name.toUpperCase();
    }

    public void searchAndPurchase(List<SellerAgent> sellers, Scanner scanner) {
        while (true) {
            System.out.print("Enter the target book for purchase (type 'exit' to stop): ");
            String targetBook = scanner.nextLine();

            if (targetBook.equalsIgnoreCase("exit")) {
                break;
            }

            // Find all sellers with the book in stock
            List<SellerAgent> availableSellers = new ArrayList<>();
            for (SellerAgent seller : sellers) {
                double totalPrice = seller.provideOffer(targetBook, 1);
                if (totalPrice != -1) {
                    availableSellers.add(seller);
                }
            }

            // Simulate the purchase process
            if (!availableSellers.isEmpty()) {
                availableSellers.sort((s1, s2) -> Double.compare(s1.provideOffer(targetBook, 1),
                        s2.provideOffer(targetBook, 1)));

                SellerAgent selectedSeller = availableSellers.get(0);

                double totalPrice = selectedSeller.provideOffer(targetBook, 1);
                int purchaseQuantity = Math.min(1, selectedSeller.catalog.stream()
                        .filter(book -> book.title.equalsIgnoreCase(targetBook))
                        .mapToInt(book -> book.quantity)
                        .sum());

                System.out.println(name + " purchased 1 copy of " + targetBook + " from " + selectedSeller.name +
                        " for \u00A3" + totalPrice);

                selectedSeller.servePurchaseOrder(targetBook, purchaseQuantity);
                purchasedBooks.add(new Book(targetBook, totalPrice, 1));
            } else {
                System.out.println("Book not available in any catalog.");
            }
        }
    }

    public void displayPurchasedBooks() {
        System.out.println("\nPurchased books by " + name + ":");
        double totalBookPrice = 0;

        for (Book book : purchasedBooks) {
            System.out.println(book.title + " - Price: \u00A3" + book.price + " - Quantity: " + book.quantity);

            if (book.price >= 0) {
                totalBookPrice += book.price * book.quantity;
            } else {
                System.out.println("Warning: Invalid price for " + book.title);
            }
        }

        System.out.println("Total Book Price: \u00A3" + (totalBookPrice >= 0 ? totalBookPrice : "N/A"));
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
            seeCatalogs = scanner.nextLine().toLowerCase().replaceAll("[^a-zA-Z]", "");
        } while (!seeCatalogs.equals("yes") && !seeCatalogs.equals("no"));

        if (seeCatalogs.equals("yes")) {
            for (SellerAgent seller : sellers) {
                seller.displayCatalog();
                System.out.println();
            }
        }

        // Asking the buyer for their name
        System.out.print("Enter the name of the Buyer: ");
        String buyerName = scanner.nextLine();

        // Creating a BuyerAgent
        BuyerAgent buyer = new BuyerAgent(buyerName);

        // Simulating the search and purchase process
        buyer.searchAndPurchase(sellers, scanner);

        // Displaying the purchased books
        buyer.displayPurchasedBooks();

        // Displaying catalogs after the purchase
        System.out.println("\nCatalogs after purchase:");
        for (SellerAgent seller : sellers) {
            seller.displayCatalog();
            System.out.println();
        }

        scanner.close();
    }
}
