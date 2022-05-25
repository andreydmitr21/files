package com.qtqt;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.collections.ContainExactTextsCaseSensitive;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import dev.failsafe.internal.util.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.codeborne.pdftest.PDF.containsText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.delete;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestClass {
    static {
        Configuration.browserVersion = "99";
    }

    @Test
    public void downloadFile() throws Exception {
        open("https://github.com/cheshi-mantu/qa-local-test-bed/blob/master/.gitignore");
        File file = $("#raw-url").download();
        try (InputStream inpStr = new FileInputStream(file)) {
            byte[] fileCont = inpStr.readAllBytes();
            String fileString = new String(fileCont, StandardCharsets.UTF_8);
        }
    }


    @Test
    public void pdfParsingTest() throws Exception {

        try (InputStream inps = getClass().getClassLoader().getResourceAsStream("pdf/ideapad_S145_15IIL_Spec.PDF")) {
            PDF pdf = new PDF(inps);
            //Assertions.assertEquals(pdf.numberOfPages,7);
            assertThat(pdf, containsText("Power"));
        }
    }

    @Test

    public void xlsParsingTest() throws Exception {

        try (InputStream inps = getClass().
                getClassLoader().
                getResourceAsStream("xls/чг продукты.xlsx")) {
            XLS xls = new XLS(inps);
            //Assertions.assertEquals(pdf.numberOfPages,7);
            String str = xls.excel.
                    getSheetAt(0).
                    getRow(3).
                    getCell(7).
                    getStringCellValue();

            org.assertj.core.api.Assertions.assertThat(str).contains("вода");
        }
    }

    ClassLoader cl = TestClass.class.getClassLoader();

    @Test
    public void csvParsingTest() throws Exception {
        try (
                InputStream inps = cl.getResourceAsStream("csv/test.csv");
                CSVReader csv = new CSVReader(new InputStreamReader(inps, StandardCharsets.UTF_8))
        ) {
            List<String[]> content = csv.readAll();
            org.assertj.core.api.Assertions.assertThat(content).contains(
                    new String[]{"Dmitry", " Tuchs"}
            );
        }
    }

    @Test
    public void zipParsingTest() throws Exception {
        String docxFile="q.docx";
        try (
                ZipInputStream inps = new ZipInputStream(
                        cl.getResourceAsStream("zip/спряжение глаголов.docx.zip"))) {
            ZipEntry entry;
            ZipInputStream zipIn = new ZipInputStream(inps);
            while ((entry = inps.getNextEntry()) != null) {
                if (entry.getName().contentEquals("спряжение глаголов.docx")) {
                    // read file
                    String entryFileName = entry.getName();
                    Path entryPath = Paths.get("/src/test/java/resources/TMP/",docxFile);
                    System.out.println(entryPath.toString());

                    // Create the entry file by creating necessary directories
                    try {
                        delete(entryPath);
                    } catch (Exception e)
                    {

                    }
                    createFile(entryPath);

                    // Create an output stream to extract the contents of the
                    // zip entry and write to the new file
                    try (BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(entryPath.toString()))) {
                        byte[] buffer = new byte[1024];
                        int count;
                        while ((count = zipIn.read(buffer)) != -1) {
                            bos.write(buffer, 0, count);
                        }
                    }

                    return;
                }
            }
            throw new Exception("no file "+myZip+ "inside zip");
        }
    }
}