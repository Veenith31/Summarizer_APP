/*package com.summarizer.project.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfService {

    public String extractTextFromPdf(InputStream inputStream) throws IOException {
        PDDocument document = PDDocument.load(inputStream);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }
}
*/

package com.summarizer.project.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfService {

    public String extractTextFromPdf(InputStream fileInputStream) throws IOException {
        // Load the PDF document from InputStream
        PDDocument document = PDDocument.load(fileInputStream);

        // Initialize PDFTextStripper to extract text
        PDFTextStripper pdfStripper = new PDFTextStripper();

        // Extract text from the PDF
        String extractedText = pdfStripper.getText(document);

        // Close the document after processing
        document.close();

        return extractedText;
    }

}

