package com.example.demo;

import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    private static final float FONT_SIZE = 12;
    private static final float LEADING = -1.2f * FONT_SIZE;

    public static void main(String... args) {

        try (final PDDocument doc = new PDDocument()) {

            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            PDRectangle mediaBox = page.getMediaBox();
            OTFParser otfParser = new OTFParser();
            OpenTypeFont otf = otfParser
                    .parse(new File("C:\\Users\\thanhnd\\Desktop\\pdfbox\\AOTFShinGoProBold.otf"));
            PDFont font = PDType0Font.load(doc, otf, false);
            float marginY = 30;
            float marginX = 60;
            float width = mediaBox.getWidth() - 2 * marginX - 450;
            System.out.println(width);
            float startX = mediaBox.getLowerLeftX() + marginX;
            float startY = mediaBox.getUpperRightY() - marginY;

            String text = "〒192-0393 東京都 八王子市東中野742-1" ;

            contentStream.beginText();
            addParagraph(contentStream, width, startX, startY, text, true, font);
            contentStream.endText();

            contentStream.close();

            doc.save(new File("C:\\Users\\thanhnd\\AppData\\Roaming\\Microsoft\\Windows\\Network Shortcuts\\example.pdf"));
        } catch (IOException e) {
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }

    private static void addParagraph(PDPageContentStream contentStream, float width, float sx,
                                     float sy, String text, boolean justify, PDFont font) throws IOException {
        List<String> lines = parseLines(text, width, font);
        contentStream.setFont(font, FONT_SIZE);
        contentStream.newLineAtOffset(sx, sy);
        for (String line : lines) {
            float charSpacing = 0;
            if (justify) {
                if (line.length() > 1) {
                    float size = FONT_SIZE * font.getStringWidth(line) / 1000;
                    float free = width - size;
                    if (free > 0 && !lines.get(lines.size() - 1).equals(line)) {
                        charSpacing = free / (line.length() - 1);
                    }
                }
            }
            contentStream.setCharacterSpacing(charSpacing);
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, LEADING);
        }
    }

    private static List<String> parseLines(String text, float width, PDFont font) throws IOException {
        List<String> lines = new ArrayList<>();
        int lastSpace = -1;
        while (text.length() > 0) {
            int spaceIndex
                    = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, 1);
            float size = FONT_SIZE * font.getStringWidth(subString) / 1000;
            if (size > width) {
                if (lastSpace < 0) {
                    lastSpace = spaceIndex;
                }
                subString = text.substring(0, lastSpace);
                lines.add(subString);
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == text.length()) {
                lines.add(text);
                text = "";
            } else {
                System.out.println(lastSpace);
                System.out.println(spaceIndex);
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }
}

