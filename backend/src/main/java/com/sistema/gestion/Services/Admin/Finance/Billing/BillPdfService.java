package com.sistema.gestion.Services.Admin.Finance.Billing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class BillPdfService {

    public byte[] generarFacturaPDF(String jsonData) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(out)));

        // Título
        document.add(new Paragraph("Factura Electrónica"));
        document.add(new Paragraph("\n"));

        // Contenido de la factura
        document.add(new Paragraph("CUIT Emisor: 20304050607"));
        document.add(new Paragraph("Punto de Venta: 1"));
        document.add(new Paragraph("Tipo Comprobante: 6 (Factura B)"));
        document.add(new Paragraph("Número: 1"));
        document.add(new Paragraph("Importe Total: $121.00"));
        document.add(new Paragraph("CAE: 61123345678901"));
        document.add(new Paragraph("\n"));

        // Generar QR
        String base64Json = Base64.getEncoder().encodeToString(jsonData.getBytes());
        String qrUrl = "https://www.afip.gob.ar/fe/qr/?p=" + base64Json;
        ImageData imageData = ImageDataFactory.create(generarCodigoQR(qrUrl));
        Image qrImage = new Image(imageData);
        qrImage.scaleToFit(150, 150);
        document.add(qrImage);

        document.close();
        return out.toByteArray();
    }

    private byte[] generarCodigoQR(String data) throws WriterException, java.io.IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream qrOut = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", qrOut);
        return qrOut.toByteArray();
    }
}

