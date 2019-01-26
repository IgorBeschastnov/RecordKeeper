package Parsers;

import java.io.File;
import java.io.FileOutputStream;

import Model.Paragraph;
import Model.Template;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public abstract class TemplateWriter {

    public static void WriteTemplateInstance(Template template, File file) throws Exception{
        try (XWPFDocument doc = new XWPFDocument()) {
            for (Paragraph paragraph: template) {
                /*DEBUG*/System.out.println("writing paragraph...");
                /*DEBUG*/System.out.print("paragraph elements : ");
                /*DEBUG*/System.out.println(paragraph.getSize());
                String text = paragraph.getText();
                int start = 0;
                int nl = -1;
                int parNum = 0;
                while (nl != text.length()-1) {
                    // FIXME: 31.05.2018 This won't work on macOS due to the different line ending. I tried to use System.lineSeparator(), but that didn't work on Windows for some reason
                    nl = text.indexOf("\n", start);
                    if (nl == -1) {
                        nl = text.length()-1;
                    }

                    XWPFParagraph pr = doc.createParagraph();
                    pr.setSpacingAfter((int)pr.getSpacingBetween());
                    pr.setSpacingBefore((int)pr.getSpacingBetween());
                    pr.setAlignment(ParagraphAlignment.THAI_DISTRIBUTE);
                    XWPFRun run = pr.createRun();
                    run.setFontSize(14);
                    run.setFontFamily("Times New Roman");
                    if (parNum == 0) {
                        run.setBold(true);
                    }
                    ///*DEBUG*/System.out.println(start + " " + nl);
                    ///*DEBUG*/System.out.println(text.substring(start,nl));
                    run.setText(text.substring(start,nl));
                    start = nl+1;
                    parNum++;
                }
            }
            try (FileOutputStream out = new FileOutputStream(file)) {
                doc.write(out);
            }
        }
    }
}
