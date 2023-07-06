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

    private Discount calcDiscount(Product p, Offer offer, double unitPrice, double quantity, int minNumOfRequiredItems) {
        int quantityAsInt = (int) quantity;
        int numberOfXs = quantityAsInt / minNumOfRequiredItems;
        if (offer.getOfferType() == SpecialOfferType.TWO_FOR_AMOUNT && quantityAsInt >= 2) {
            int intDivision = quantityAsInt / minNumOfRequiredItems;
            double pricePerUnit = offer.getArgument() * intDivision;
            double theTotal = (quantityAsInt % 2) * unitPrice;
            double total = pricePerUnit + theTotal;
            double discountN = unitPrice * quantity - total;
            return new Discount(p, "2 for " + offer.getArgument(), -discountN);
        }
        if (offer.getOfferType() == SpecialOfferType.THREE_FOR_TWO && quantityAsInt > 2) {
            double discountAmount = quantity * unitPrice
                    - ((numberOfXs * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
            return new Discount(p, "3 for 2", -discountAmount);
        }
        if (offer.getOfferType() == SpecialOfferType.TEN_PERCENT_DISCOUNT) {
            return new Discount(p, offer.getArgument() + "% off",
                    -quantity * unitPrice * offer.getArgument() / 100.0);
        }
        if (offer.getOfferType() == SpecialOfferType.FIVE_FOR_AMOUNT && quantityAsInt >= 5) {
            double discountTotal = unitPrice * quantity
                    - (offer.getArgument() * numberOfXs + quantityAsInt % 5 * unitPrice);
            return new Discount(p, minNumOfRequiredItems + " for " + offer.getArgument(), -discountTotal);
        }
        return null;
    }
}
