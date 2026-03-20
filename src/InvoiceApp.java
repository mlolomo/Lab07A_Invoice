import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;

public class InvoiceApp extends JFrame {

    private static final String INT_PATTERN    = "^[0-9]*$";
    private static final String DOUBLE_PATTERN = "^[0-9]*\\.?[0-9]{0,2}$";

    private Invoice invoice;

    private JTextField invoiceNumberTf;
    private JTextField customerNameTf;

    private JTextField productNameTf;
    private JTextField unitPriceTf;
    private JTextField quantityTf;

    private JTextArea invoiceDisplayArea;

    public InvoiceApp() {
        super("Lab07A – Invoice Application");
        invoice = new Invoice("", "");
        buildUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildHeaderPanel(),   BorderLayout.NORTH);
        add(buildEntryPanel(),    BorderLayout.CENTER);
        add(buildDisplayPanel(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBorder(BorderFactory.createTitledBorder("Invoice Info"));

        invoiceNumberTf = new JTextField(10);
        customerNameTf  = new JTextField(20);

        p.add(new JLabel("Invoice #:"));
        p.add(invoiceNumberTf);
        p.add(Box.createHorizontalStrut(16));
        p.add(new JLabel("Customer Name:"));
        p.add(customerNameTf);

        return p;
    }

    private JPanel buildEntryPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add Line Item"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        productNameTf = new JTextField(18);
        unitPriceTf   = new JTextField(10);
        quantityTf    = new JTextField(6);

        applyRegexFilter(unitPriceTf, DOUBLE_PATTERN);
        applyRegexFilter(quantityTf,  INT_PATTERN);

        gc.gridx = 0; gc.gridy = 0; form.add(new JLabel("Product Name:"), gc);
        gc.gridx = 1;               form.add(productNameTf, gc);

        gc.gridx = 0; gc.gridy = 1; form.add(new JLabel("Unit Price ($):"), gc);
        gc.gridx = 1;               form.add(unitPriceTf, gc);

        gc.gridx = 0; gc.gridy = 2; form.add(new JLabel("Quantity:"), gc);
        gc.gridx = 1;               form.add(quantityTf, gc);

        wrapper.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton addItemBtn    = new JButton("Add Item");
        JButton generateBtn   = new JButton("Generate Invoice");
        JButton clearBtn      = new JButton("Clear All");

        addItemBtn.addActionListener(this::onAddItem);
        generateBtn.addActionListener(this::onGenerateInvoice);
        clearBtn.addActionListener(this::onClearAll);

        buttons.add(addItemBtn);
        buttons.add(generateBtn);
        buttons.add(clearBtn);
        wrapper.add(buttons, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel buildDisplayPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Invoice Display"));

        invoiceDisplayArea = new JTextArea(16, 70);
        invoiceDisplayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        invoiceDisplayArea.setEditable(false);

        p.add(new JScrollPane(invoiceDisplayArea), BorderLayout.CENTER);
        return p;
    }


    private void onAddItem(ActionEvent e) {
        String name      = productNameTf.getText().trim();
        String priceText = unitPriceTf.getText().trim();
        String qtyText   = quantityTf.getText().trim();

        if (name.isEmpty() || priceText.isEmpty() || qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in Product Name, Unit Price, and Quantity.",
                    "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double unitPrice = Double.parseDouble(priceText);
        int    quantity  = Integer.parseInt(qtyText);

        Product  product  = new Product(name, unitPrice);
        LineItem lineItem = new LineItem(product, quantity);
        invoice.addLineItem(lineItem);

        invoiceDisplayArea.append("Added: " + lineItem + "\n");

        productNameTf.setText("");
        unitPriceTf.setText("");
        quantityTf.setText("");
        productNameTf.requestFocus();
    }

    private void onGenerateInvoice(ActionEvent e) {
        String invNum   = invoiceNumberTf.getText().trim();
        String custName = customerNameTf.getText().trim();

        if (invNum.isEmpty() || custName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter Invoice # and Customer Name before generating.",
                    "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (invoice.getLineItems().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add at least one line item before generating.",
                    "No Items", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Invoice finalInvoice = new Invoice(invNum, custName);
        for (LineItem li : invoice.getLineItems()) {
            finalInvoice.addLineItem(li);
        }

        invoiceDisplayArea.setText(finalInvoice.format());

        System.out.println(finalInvoice.format());
    }

    private void onClearAll(ActionEvent e) {
        invoiceNumberTf.setText("");
        customerNameTf.setText("");
        productNameTf.setText("");
        unitPriceTf.setText("");
        quantityTf.setText("");
        invoice.clearLineItems();
        invoiceDisplayArea.setText("");
    }

    private void applyRegexFilter(JTextField field, String regex) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length,
                                String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nextText = currentText.substring(0, offset)
                        + text
                        + currentText.substring(offset + length);
                if (nextText.isEmpty() || nextText.matches(regex)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InvoiceApp::new);
    }
}