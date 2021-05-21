package com.example.demo;

import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    private static final float FONT_SIZE = 12;
    private static final float LEADING = -1.2f * FONT_SIZE;
    private static final float STATUS = 2;

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
            float width = mediaBox.getWidth() - 2 * marginX - 350;
            float startX = mediaBox.getLowerLeftX() + marginX;
            float startY = mediaBox.getUpperRightY() - marginY;

            String text = "〒192-0393 東京都 八王   子市東中野742-1八王 八王 八王" ;
            contentStream.beginText();
            addParagraph(contentStream, width, startX, startY, text, font);
            contentStream.endText();
            contentStream.close();
            doc.save(new File("C:\\Users\\thanhnd\\AppData\\Roaming\\Microsoft\\Windows\\Network Shortcuts\\example.pdf"));
        } catch (IOException e) {
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }

    private static void addParagraph(PDPageContentStream contentStream, float width, float sx,
                                     float sy, String text, PDFont font) throws IOException {
        List<String> lines = parseLines(text, width, font);
        whenAlignCenter(contentStream,lines, font, sx , sy, width);
    }

    private static List<String> parseLines(String text, float width, PDFont font) throws IOException {
        List<String> lines = new ArrayList<>();
        String subString;
        int i = 0;
        int j = 0;
        while (text.length() > 0) {
            if (j == 0) {
                subString = text.substring(0,0);
            } else try {
                subString = text.substring(0, j);
            } catch (StringIndexOutOfBoundsException e) {
                subString = text.substring(0, j-1);
                lines.add(subString);
                break;
            }
            float size = FONT_SIZE * font.getStringWidth(subString) / 1000;
            if (size < width) {
                j++;
            } else if (size > width) {
                if (j != 0) {
                    j = j - 1;
                }
                subString = text.substring(i,j);
                text = text.substring(j).trim();
                lines.add(subString);
                j = 0;
            } else {
                subString = text.substring(i,j);
                text = text.substring(j).trim();
                lines.add(subString);
                j = 0;
            }
//            System.out.println(text.length());
//            int spaceIndex
//                    = text.indexOf(' ', lastSpace + 1);
//            if (spaceIndex < 0)
//                spaceIndex = text.length();
//            String subString = text.substring(0, spaceIndex);
//            float size = FONT_SIZE * font.getStringWidth(subString) / 1000;
//            if (size > width) {
//                if (lastSpace < 0) {
//                    lastSpace = spaceIndex;
//                }
//                subString = text.substring(0, lastSpace);
//                lines.add(subString);
//                text = text.substring(lastSpace).trim();
//                lastSpace = -1;
//            } else if (spaceIndex == text.length()) {
//                lines.add(text);
//                text = "";
//            } else {
//                lastSpace = spaceIndex;
//            }
        }
        return lines;
    }

    private static void whenAlignCenter(PDPageContentStream conn, List<String> lines, PDFont font, float sx, float sy ,float width) throws IOException {
        conn.setFont(font, FONT_SIZE);
        conn.newLineAtOffset(sx + coordinatesX(width, lines.get(0), font), sy);
        conn.setCharacterSpacing(setCharSpacing(lines.get(0), font, lines, width));
        conn.showText(lines.get(0));
        for (int i = 1; i < lines.size(); i++ ) {
            conn.setCharacterSpacing(setCharSpacing(lines.get(i), font, lines, width));
            conn.newLineAtOffset(coordinatesX(width, lines.get(i), font), LEADING);
            conn.showText(lines.get(i));
        }
    }

    private static float coordinatesX(float width, String line, PDFont font) throws IOException {
        if (STATUS == 1)
            return (width - FONT_SIZE * font.getStringWidth(line) / 1000) / 2;
        else if (STATUS == 2)
            return (width - FONT_SIZE * font.getStringWidth(line) / 1000);
        else return 0;
    }

    private static float setCharSpacing (String line, PDFont font, List<String> lines, float width) throws IOException {
        float charSpacing = 0;
        if (line.length() > 1) {
            float size = FONT_SIZE * font.getStringWidth(line) / 1000;
            float free = width - size;
            if (free > 0 && !lines.get(lines.size() - 1).equals(line)) {
                charSpacing = free / (line.length() - 1);
            }
        }
        return charSpacing;
    }
}

