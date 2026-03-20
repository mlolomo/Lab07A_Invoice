import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private String invoiceNumber;
    private String customerName;
    private List<LineItem> lineItems;

    public Invoice(String invoiceNumber, String customerName) {
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.lineItems = new ArrayList<>();
    }

    public String getInvoiceNumber() { return invoiceNumber; }
    public String getCustomerName() { return customerName; }

    public void addLineItem(LineItem item) {
        lineItems.add(item);
    }

    public void clearLineItems() {
        lineItems.clear();
    }

    public List<LineItem> getLineItems() {
        return new ArrayList<>(lineItems);
    }

    public double getTotalAmountDue() {
        double total = 0;
        for (LineItem item : lineItems) {
            total += item.getTotal();
        }
        return total;
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("=============================================================\n");
        sb.append(String.format("  INVOICE #%-10s   Customer: %s\n", invoiceNumber, customerName));
        sb.append("=============================================================\n");
        sb.append(String.format("%-20s  %6s  %10s  %11s\n",
                "Product", "Qty", "Unit Price", "Total"));
        sb.append("-------------------------------------------------------------\n");
        for (LineItem item : lineItems) {
            sb.append(item.toString()).append("\n");
        }
        sb.append("-------------------------------------------------------------\n");
        sb.append(String.format("%-44s TOTAL DUE: $%8.2f\n", "", getTotalAmountDue()));
        sb.append("=============================================================\n");
        return sb.toString();
    }
}