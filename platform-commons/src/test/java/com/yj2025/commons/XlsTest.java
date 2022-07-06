package com.yj2025.commons;

import com.alibaba.excel.EasyExcel;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XlsTest {

    @Test
    public void customHandlerWrite() {
        FileOutputStream out = null;
        File file = new File("/Users/serv/Downloads/创芯货品3.xlsx");
        try (XSSFWorkbook wb = new XSSFWorkbook(file)) {
            // 选中指定sheet
            XSSFSheet sheet = wb.getSheetAt(0);
            String[] values = {"blue", "red", "black"};

            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
            XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(values);
            CellRangeAddressList addressList = new CellRangeAddressList(3, 10, 1, 1);
            DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
            //这两行设置单元格只能是列表中的内容，否则报错
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
            // 写入文件
            out = new FileOutputStream("/Users/serv/Downloads/fff.xlsx");
            wb.write(out);
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
