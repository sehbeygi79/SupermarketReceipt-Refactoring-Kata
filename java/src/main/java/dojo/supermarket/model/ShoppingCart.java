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
            if (offers.containsKey(p)) {
                Discount discount = calcDiscount(p, offers.get(p),
                        catalog.getUnitPrice(p), productQuantities.get(p));
                if (discount != null)
                    receipt.addDiscount(discount);
            }
        }
    }

    private Discount calcDiscount(Product p, Offer offer, double unitPrice, double quantity) {
        int quantityAsInt = (int) quantity;
        int quantityDivision = quantityAsInt / offer.getMinNumOfRequiredItems();
        Discount discount = null;

        switch (offer.getOfferType()) {
            case TWO_FOR_AMOUNT:
                if (quantityAsInt >= 2) {
                    double discountedPrice = offer.getArgument() * quantityDivision;
                    double residuePrice = (quantityAsInt % 2) * unitPrice;
                    double currPrice = discountedPrice + residuePrice;
                    double discountAmount = quantity * unitPrice - currPrice;
                    discount = new Discount(p, "2 for " + offer.getArgument(), -discountAmount);
                }
                break;
            case THREE_FOR_TWO:
                if (quantityAsInt >= 3) {
                    double discountedPrice = quantityDivision * 2 * unitPrice;
                    double residuePrice = (quantityAsInt % 3) * unitPrice;
                    double currPrice = discountedPrice + residuePrice;
                    double discountAmount = quantity * unitPrice - currPrice;
                    discount = new Discount(p, "3 for 2", -discountAmount);
                }
                break;
            case TEN_PERCENT_DISCOUNT: {
                double discountAmount = quantity * unitPrice * offer.getArgument() / 100.0;
                discount = new Discount(p, offer.getArgument() + "% off", -discountAmount);
            }
                break;
            case FIVE_FOR_AMOUNT:
                if (quantityAsInt >= 5) {
                    double discountedPrice = offer.getArgument() * quantityDivision;
                    double residuePrice = (quantityAsInt % 5) * unitPrice;
                    double currPrice = discountedPrice + residuePrice;
                    double discountAmount = quantity * unitPrice - currPrice;
                    discount = new Discount(p, offer.getMinNumOfRequiredItems() + " for " + offer.getArgument(),
                            -discountAmount);
                }
                break;
            default:
                break;
        }
        return discount;
    }
}
