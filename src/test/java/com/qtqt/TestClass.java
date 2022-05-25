package com.qtqt;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.collections.ContainExactTextsCaseSensitive;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static com.codeborne.pdftest.PDF.containsText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
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
        try (InputStream inps = cl.
                getResourceAsStream("csv/test.csv")) {
            CSVReader csv = new CSVReader(new InputStreamReader(inps));

        }
    }
}