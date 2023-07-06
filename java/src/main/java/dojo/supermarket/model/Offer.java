package dojo.supermarket.model;

public class Offer {

    private SpecialOfferType offerType;
    private final Product product;
    private double argument;

    public Offer(SpecialOfferType offerType, Product product, double argument) {
        this.offerType = offerType;
        this.argument = argument;
        this.product = product;
    }

    Product getProduct() {
        return product;
    }

    public SpecialOfferType getOfferType() {
        return offerType;
    }

    public double getArgument() {
        return argument;
    }

    public int getMinNumOfRequiredItems() {
        switch (offerType) {
            case THREE_FOR_TWO:
                return 3;
            case TWO_FOR_AMOUNT:
                return 2;
            case FIVE_FOR_AMOUNT:
                return 5;
            default:
                return 1;
        }
    }
}
