package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

    private final List<ProductQuantity> items = new ArrayList<>();
    private final Map<Product, Double> productQuantities = new HashMap<>();

    List<ProductQuantity> getItems() {
        return Collections.unmodifiableList(items);
    }

    void addItem(Product product) {
        addItemQuantity(product, 1.0);
    }

    Map<Product, Double> productQuantities() {
        return Collections.unmodifiableMap(productQuantities);
    }

    public void addItemQuantity(Product product, double quantity) {
        items.add(new ProductQuantity(product, quantity));
        if (productQuantities.containsKey(product)) {
            productQuantities.put(product, productQuantities.get(product) + quantity);
        } else {
            productQuantities.put(product, quantity);
        }
    }

    void handleOffers(Receipt receipt, Map<Product, Offer> offers, SupermarketCatalog catalog) {
        for (Product p : productQuantities().keySet()) {
            double quantity = productQuantities.get(p);
            if (offers.containsKey(p)) {
                Offer offer = offers.get(p);

                double unitPrice = catalog.getUnitPrice(p);
                Discount discount = null;
                int minNumOfRequiredItems = offer.getMinNumOfRequiredItems();

                discount = calcDiscount(p, offer, unitPrice, quantity, minNumOfRequiredItems);
                if (discount != null)
                    receipt.addDiscount(discount);
            }
        }
    }

    private Discount calcDiscount(Product p, Offer offer, double unitPrice, double quantity,
            int minNumOfRequiredItems) {
        int quantityAsInt = (int) quantity;
        int numberOfXs = quantityAsInt / minNumOfRequiredItems;
        Discount discount = null;

        switch (offer.getOfferType()) {
            case TWO_FOR_AMOUNT:
                if (quantityAsInt >= 2) {
                    double pricePerUnit = offer.getArgument() * numberOfXs;
                    double theTotal = (quantityAsInt % 2) * unitPrice;
                    double total = pricePerUnit + theTotal;
                    double discountN =  quantity * unitPrice - total;
                    discount = new Discount(p, "2 for " + offer.getArgument(), -discountN);
                }
                break;
            case THREE_FOR_TWO:
                if (quantityAsInt >= 3) {
                    double discountAmount = quantity * unitPrice
                            - ((numberOfXs * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
                    discount = new Discount(p, "3 for 2", -discountAmount);
                }
                break;
            case TEN_PERCENT_DISCOUNT:
                discount = new Discount(p, offer.getArgument() + "% off",
                        -quantity * unitPrice * offer.getArgument() / 100.0);
                break;
            case FIVE_FOR_AMOUNT:
                if (quantityAsInt >= 5) {
                    double discountTotal = quantity * unitPrice
                            - (offer.getArgument() * numberOfXs + quantityAsInt % 5 * unitPrice);
                    discount = new Discount(p, minNumOfRequiredItems + " for " + offer.getArgument(), -discountTotal);
                }
            default:
                break;
        }
        return discount;
    }
}
