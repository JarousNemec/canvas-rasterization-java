package fill;

//vertical stripes, stripe and the gap between stripes have the same width stripeWidth px
//color depends only on x, so it's the same in every row
public class StripesPattern extends TwoColorPattern {
    private final int stripeWidth;

    public StripesPattern(int stripeWidth, int primaryColor, int secondaryColor) {
        super(primaryColor, secondaryColor);
        this.stripeWidth = checkSize(stripeWidth);
    }

    @Override
    protected boolean isPrimary(int x, int y) {
        return isEvenCell(x, stripeWidth);
    }

    public int getStripeWidth() {
        return stripeWidth;
    }
}
