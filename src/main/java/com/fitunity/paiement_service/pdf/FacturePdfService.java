package com.fitunity.paiement_service.pdf;

import com.fitunity.paiement_service.entity.Paiement;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class FacturePdfService {

    public byte[] genererFacture(Paiement paiement) throws IOException {

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 24);
        content.newLineAtOffset(220, 780);
        content.showText("FitUnity");
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 16);
        content.newLineAtOffset(185, 750);
        content.showText("Recu de Paiement");
        content.endText();

        content.setLineWidth(1f);
        content.moveTo(50, 735);
        content.lineTo(545, 735);
        content.stroke();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.newLineAtOffset(50, 710);
        content.showText("Details du Paiement :");
        content.endText();

        int yPosition = 680;
        int lineHeight = 25;

        yPosition = ajouterLigne(content, "ID Paiement :",
                paiement.getPaiementId(), yPosition, lineHeight);
        yPosition = ajouterLigne(content, "Utilisateur :",
                paiement.getUserId(), yPosition, lineHeight);
        yPosition = ajouterLigne(content, "Reference :",
                paiement.getReferenceId(), yPosition, lineHeight);
        yPosition = ajouterLigne(content, "Type :",
                paiement.getReferenceType(), yPosition, lineHeight);
        yPosition = ajouterLigne(content, "Montant :",
                paiement.getAmount() + " " + paiement.getCurrency().toUpperCase(),
                yPosition, lineHeight);
        yPosition = ajouterLigne(content, "Statut :",
                paiement.getStatus().toString(), yPosition, lineHeight);
        yPosition = ajouterLigne(content, "Methode :",
                paiement.getPaiementMethod().toString(), yPosition, lineHeight);
        yPosition = ajouterLigne(content, "Date :",
                paiement.getPaiementDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                yPosition, lineHeight);

        if (paiement.getStripePaymentIntentId() != null) {
            yPosition = ajouterLigne(content, "ID Stripe :",
                    paiement.getStripePaymentIntentId(), yPosition, lineHeight);
        }

        content.setLineWidth(1f);
        content.moveTo(50, yPosition - 10);
        content.lineTo(545, yPosition - 10);
        content.stroke();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(150, yPosition - 40);
        content.showText("Merci pour votre confiance ! - FitUnity");
        content.endText();

        content.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();

        return out.toByteArray();
    }

    private int ajouterLigne(PDPageContentStream content,
                             String label, String valeur,
                             int yPosition, int lineHeight) throws IOException {

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(50, yPosition);
        content.showText(label);
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(200, yPosition);
        content.showText(valeur != null ? valeur : "N/A");
        content.endText();

        return yPosition - lineHeight;
    }
}