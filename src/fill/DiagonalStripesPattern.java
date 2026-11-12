package fill;

//diagonal stripes "/", stripe and the gap have the same width stripeWidth px (measured horizontally)
//same as vertical stripes, but every row down is shifted by one pixel - so x + y is used instead of x
public class DiagonalStripesPattern extends TwoColorPattern {
    private final int stripeWidth;

    public DiagonalStripesPattern(int stripeWidth, int primaryColor, int secondaryColor) {
        super(primaryColor, secondaryColor);
        this.stripeWidth = checkSize(stripeWidth);
    }

    @Override
    protected boolean isPrimary(int x, int y) {
        return isEvenCell(x + y, stripeWidth);
    }

    public int getStripeWidth() {
        return stripeWidth;
    }
}
