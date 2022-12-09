package com.example.managejob.model;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Data
public class UserExcelExporter {
    private XSSFWorkbook xssFworkbook;
    private XSSFSheet sheet;
    private List<User> listUsers;

    public UserExcelExporter(List<User> listUsers) {
        xssFworkbook = new XSSFWorkbook();
        this.listUsers = listUsers;
        sheet = xssFworkbook.createSheet("Users");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("User ID");

        cell = row.createCell(1);
        cell.setCellValue("Email");

        cell = row.createCell(2);
        cell.setCellValue("Name");

        cell = row.createCell(3);
        cell.setCellValue("UserRole");
    }

    private void writeDataRows() {
        int rowCount = 1;
        for(User user : listUsers){
            Row row = sheet.createRow(rowCount++);
            Cell cell = row.createCell(0);
            cell.setCellValue(user.getId());

            cell = row.createCell(1);
            cell.setCellValue(user.getEmail());

            cell = row.createCell(2);
            cell.setCellValue(user.getName());

            cell = row.createCell(3);
            cell.setCellValue(user.getRoleUser().getRole());
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderRow();
        writeDataRows();

        ServletOutputStream outputStream =  response.getOutputStream();
        xssFworkbook.write(outputStream);
        xssFworkbook.close();
        outputStream.close();
    }
}
