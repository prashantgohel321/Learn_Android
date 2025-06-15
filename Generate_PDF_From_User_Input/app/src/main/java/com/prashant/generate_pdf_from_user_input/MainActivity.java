package com.prashant.generate_pdf_from_user_input;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private EditText invoiceNumberEditText, clientNameEditText, clientAddressEditText, itemDescriptionEditText, quantityEditText;
    private EditText unitPriceEditText, taxRateEditText, discountEditText;
    private TextView subtotalTextView, taxAmountTextView, discountAmountTextView, grandTotalTextView;
    private Button generatePdfButton;

    // Permission Request Code
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        invoiceNumberEditText = findViewById(R.id.invoiceNumberEditText);
        clientNameEditText = findViewById(R.id.clientNameEditText);
        clientAddressEditText = findViewById(R.id.clientAddressEditText);
        itemDescriptionEditText = findViewById(R.id.itemDescriptionEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        unitPriceEditText = findViewById(R.id.unitPriceEditText);
        taxRateEditText = findViewById(R.id.taxRateEditText);
        discountEditText = findViewById(R.id.discountEditText);
        subtotalTextView = findViewById(R.id.subtotalTextView);
        taxAmountTextView = findViewById(R.id.taxAmountTextView);
        discountAmountTextView = findViewById(R.id.discountAmountTextView);
        grandTotalTextView = findViewById(R.id.grandTotalTextView);
        generatePdfButton = findViewById(R.id.generatePdfButton);

        // Add TextWatchers to trigger calculations on input change
        EditText[] textWatchers = {
                quantityEditText, unitPriceEditText, taxRateEditText, discountEditText
        };
        for (EditText editText : textWatchers) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    calculateTotals();
                }
            });
        }

        generatePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for storage permission before generating PDF
                if (checkStoragePermission()) {
                    generatePdf();
                } else {
                    requestStoragePermission();
                }
            }
        });

        // Initial calculation when the activity starts
        calculateTotals();
    }

    /**
     * Calculates the subtotal, tax amount, discount amount, and grand total
     * based on user input and updates the corresponding TextViews.
     */
    private void calculateTotals() {
        String quantityStr = quantityEditText.getText().toString();
        String unitPriceStr = unitPriceEditText.getText().toString();
        String taxRateStr = taxRateEditText.getText().toString();
        String discountStr = discountEditText.getText().toString();

        double quantity = parseDoubleOrZero(quantityStr);
        double unitPrice = parseDoubleOrZero(unitPriceStr);
        double taxRate = parseDoubleOrZero(taxRateStr);
        double discountPercentage = parseDoubleOrZero(discountStr);

        double subtotal = quantity * unitPrice;
        double taxAmount = subtotal * (taxRate / 100.0);
        double discountAmount = subtotal * (discountPercentage / 100.0);
        double grandTotal = subtotal + taxAmount - discountAmount;

        // Format numbers to two decimal places
        DecimalFormat df = new DecimalFormat("#.00");

        subtotalTextView.setText(String.format("$%s", df.format(subtotal)));
        taxAmountTextView.setText(String.format("$%s", df.format(taxAmount)));
        discountAmountTextView.setText(String.format("-$%s", df.format(discountAmount))); // Show discount as negative
        grandTotalTextView.setText(String.format("$%s", df.format(grandTotal)));
    }

    /**
     * Helper method to parse a String to a double, returning 0.0 if parsing fails.
     */
    private double parseDoubleOrZero(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Checks if the WRITE_EXTERNAL_STORAGE permission is granted.
     * For Android 10 (API 29) and below, this is crucial for shared storage.
     * For Android 11 (API 30) and above, Scoped Storage largely removes the need
     * for this permission for app-specific directories (like getExternalFilesDir).
     */
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ uses Scoped Storage. Writing to app-specific directories
            // (like getExternalFilesDir) does not require WRITE_EXTERNAL_STORAGE.
            // If writing to shared directories (e.g., Downloads), MediaStore API is preferred.
            // For this example, we'll write to app-specific external files dir, so no explicit
            // permission check for WRITE_EXTERNAL_STORAGE is strictly needed for API 30+.
            return true;
        } else {
            int result = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Requests the WRITE_EXTERNAL_STORAGE permission from the user.
     */
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) { // Only request for older Android versions
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    /**
     * Handles the result of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                generatePdf(); // Generate PDF after permission is granted
            } else {
                Toast.makeText(this, "Storage Permission Denied. Cannot generate PDF.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Generates the PDF invoice based on the input fields.
     */
    private void generatePdf() {
        // Retrieve input values
        String invoiceNumber = invoiceNumberEditText.getText().toString();
        if (invoiceNumber.isEmpty()) invoiceNumber = "N/A";
        String clientName = clientNameEditText.getText().toString();
        if (clientName.isEmpty()) clientName = "N/A";
        String clientAddress = clientAddressEditText.getText().toString();
        if (clientAddress.isEmpty()) clientAddress = "N/A";
        String itemDescription = itemDescriptionEditText.getText().toString();
        if (itemDescription.isEmpty()) itemDescription = "N/A";

        double quantity = parseDoubleOrZero(quantityEditText.getText().toString());
        double unitPrice = parseDoubleOrZero(unitPriceEditText.getText().toString());
        double taxRate = parseDoubleOrZero(taxRateEditText.getText().toString());
        double discountPercentage = parseDoubleOrZero(discountEditText.getText().toString());

        double subtotal = quantity * unitPrice;
        double taxAmount = subtotal * (taxRate / 100.0);
        double discountAmount = subtotal * (discountPercentage / 100.0);
        double grandTotal = subtotal + taxAmount - discountAmount;

        DecimalFormat df = new DecimalFormat("#,##0.00"); // Formatter for currency

        // Create a new PDF document
        PdfDocument pdfDocument = new PdfDocument();

        // Page attributes (A4 size: 595 width, 842 height in points)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // --- Setup Paint Objects ---
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(36f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);

        Paint headerPaint = new Paint();
        headerPaint.setColor(Color.DKGRAY);
        headerPaint.setTextSize(18f);
        headerPaint.setFakeBoldText(true);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12f);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(1f);

        // --- Draw Content on PDF ---

        // 1. Company Logo (Replace 'R.drawable.my_company_logo' with your actual logo drawable)
        Bitmap logoBitmap = null;
        try {
            logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.new_hanger_logo_with_org_bg); // Ensure this drawable exists
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Logo not found! Please check 'my_company_logo' in drawable.", Toast.LENGTH_LONG).show();
        }

        if (logoBitmap != null) {
            int logoWidth = 150; // Desired width of the logo in PDF points
            // Maintain aspect ratio
            int logoHeight = (int) (logoBitmap.getHeight() / (float) logoBitmap.getWidth() * logoWidth);
            int logoX = (pageInfo.getPageWidth() - logoWidth) - 40; // Right align with padding
            int logoY = 40; // Top padding
            canvas.drawBitmap(Bitmap.createScaledBitmap(logoBitmap, logoWidth, logoHeight, false), (float) logoX, (float) logoY, null);
        }

        // 2. Invoice Title
        float centerX = pageInfo.getPageWidth() / 2f;
        canvas.drawText("INVOICE", centerX, 100f, titlePaint);

        // 3. Invoice Details (left aligned)
        float yPos = 160f; // Starting Y position for details
        canvas.drawText("Invoice No: " + invoiceNumber, 50f, yPos, textPaint);
        yPos += 20f;
        canvas.drawText("Date: " + new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()), 50f, yPos, textPaint);

        // 4. Client Details
        yPos += 40f;
        canvas.drawText("BILL TO:", 50f, yPos, headerPaint);
        yPos += 20f;
        canvas.drawText(clientName, 50f, yPos, textPaint);
        yPos += 15f;
        // Split address into multiple lines if needed (simple approach)
        String[] addressLines = clientAddress.split("\n");
        for (String line : addressLines) {
            canvas.drawText(line, 50f, yPos, textPaint);
            yPos += 15f;
        }

        // Horizontal line separator
        yPos += 30f;
        canvas.drawLine(50f, yPos, pageInfo.getPageWidth() - 50f, yPos, linePaint);

        // 5. Item Header
        yPos += 20f;
        canvas.drawText("DESCRIPTION", 60f, yPos, headerPaint);
        canvas.drawText("QTY", 300f, yPos, headerPaint);
        canvas.drawText("UNIT PRICE", 380f, yPos, headerPaint);
        canvas.drawText("AMOUNT", 480f, yPos, headerPaint); // Right align this

        yPos += 10f;
        canvas.drawLine(50f, yPos, pageInfo.getPageWidth() - 50f, yPos, linePaint);

        // 6. Item Details (single item for simplicity)
        yPos += 20f;
        canvas.drawText(itemDescription, 60f, yPos, textPaint);
        canvas.drawText(String.valueOf(quantity), 300f, yPos, textPaint);
        canvas.drawText(df.format(unitPrice), 380f, yPos, textPaint);
        canvas.drawText(df.format(subtotal), 480f, yPos, textPaint); // This is the item's subtotal

        // Horizontal line separator
        yPos += 30f;
        canvas.drawLine(50f, yPos, pageInfo.getPageWidth() - 50f, yPos, linePaint);

        // 7. Summary Totals (right aligned)
        yPos += 20f;
        canvas.drawText("Subtotal:", 400f, yPos, textPaint);
        canvas.drawText(String.format("$%s", df.format(subtotal)), 480f, yPos, textPaint);

        yPos += 20f;
        canvas.drawText(String.format("Tax (%.0f%%):", taxRate), 400f, yPos, textPaint);
        canvas.drawText(String.format("$%s", df.format(taxAmount)), 480f, yPos, textPaint);

        yPos += 20f;
        canvas.drawText(String.format("Discount (%.0f%%):", discountPercentage), 400f, yPos, textPaint);
        canvas.drawText(String.format("-$%s", df.format(discountAmount)), 480f, yPos, textPaint);

        // Grand Total
        yPos += 30f;
        Paint grandTotalPaint = new Paint();
        grandTotalPaint.setColor(Color.BLACK);
        grandTotalPaint.setTextSize(20f);
        grandTotalPaint.setFakeBoldText(true);
        grandTotalPaint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText("GRAND TOTAL:", 470f, yPos, grandTotalPaint);
        canvas.drawText(String.format("$%s", df.format(grandTotal)), pageInfo.getPageWidth() - 50f, yPos, grandTotalPaint);


        // Finish the page
        pdfDocument.finishPage(page);

        // --- Save the PDF ---
        File folder = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS); // App-specific storage
        if (folder != null && !folder.exists()) {
            folder.mkdirs(); // Create directory if it doesn't exist
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Invoice_" + timeStamp + ".pdf";
        File file = new File(folder, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            pdfDocument.writeTo(fos);
            Toast.makeText(this, "PDF Generated: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openPdf(file); // Open the PDF after successful generation
        } catch (Exception e) {
            Log.e("PDF_GEN", "Error generating PDF: ", e);
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            pdfDocument.close();
        }
    }

    /**
     * Opens the generated PDF file using an external PDF viewer application.
     */
    private void openPdf(File pdfFile) {
        Uri uri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".fileprovider", // Make sure this matches your manifest
                pdfFile
        );
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant temporary read permission
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // Prevents adding to history stack
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No application found to open PDF.", Toast.LENGTH_SHORT).show();
            Log.e("PDF_OPEN", "Error opening PDF: ", e);
        }
    }
}
