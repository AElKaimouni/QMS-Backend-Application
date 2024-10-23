package com.example.qms.utils;

import com.google.zxing.WriterException;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class PdfService {
    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generatePdf(int reservationId, int position, String queueName, String qrCode) {
        Context context = new Context();
        context.setVariable("position", position);
        context.setVariable("reservationId", reservationId);
        context.setVariable("queueName", queueName);

        // Generate the QR code
        byte[] qrCodeBytes = null;
        try {
            qrCodeBytes = QRCodeGenerator.generateQrCode(qrCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Encode QR code to Base64 to use in the HTML img tag
        String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
        context.setVariable("qrCode", "data:image/png;base64," + qrCodeBase64);

        // Process the HTML template with Thymeleaf
        String htmlContent = templateEngine.process("reservation-ticket", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setTagged();
            PageSize pageSize = new PageSize(350, 400);
            pdf.setDefaultPageSize(pageSize);
            ConverterProperties properties = new ConverterProperties();
            MediaDeviceDescription mediaDeviceDescription = new MediaDeviceDescription(MediaType.SCREEN);
            mediaDeviceDescription.setWidth(pageSize.getWidth());
            properties.setMediaDeviceDescription(mediaDeviceDescription);
            HtmlConverter.convertToPdf(htmlContent, pdf, properties);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return outputStream.toByteArray();
    }
}
