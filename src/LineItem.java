public class LineItem {
    private Product product;
    private int quantity;

    public LineItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotal() {
        return product.getUnitPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%-20s  Qty: %4d  Unit: $%8.2f  Total: $%9.2f",
                product.getName(), quantity, product.getUnitPrice(), getTotal());
    }
}