package com.example.managejob.service;

import com.example.managejob.model.Task;
import com.example.managejob.model.User;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Data
public class TaskExcelExporter {
    private XSSFWorkbook xssFworkbook;
    private XSSFSheet sheet;
    private List<Task> listTasks;

    public TaskExcelExporter(List<Task> listTasks) {
        xssFworkbook = new XSSFWorkbook();
        this.listTasks = listTasks;
        sheet = xssFworkbook.createSheet("Tasks");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Task ID");

        cell = row.createCell(1);
        cell.setCellValue("Name");
//
//        cell = row.createCell(2);
//        cell.setCellValue("Group");

        cell = row.createCell(3);
        cell.setCellValue("Description");

//        cell = row.createCell(4);
//        cell.setCellValue("Start Date");
//
//        cell = row.createCell(5);
//        cell.setCellValue("End Date");

        cell = row.createCell(6);
        cell.setCellValue("Assign");

        cell = row.createCell(7);
        cell.setCellValue("Status");

        cell = row.createCell(8);
        cell.setCellValue("Create By");
    }

    private void writeDataRows() {
        int rowCount = 1;
        for(Task task : listTasks){
            Row row = sheet.createRow(rowCount++);
            Cell cell = row.createCell(0);
            cell.setCellValue(task.getId());

            cell = row.createCell(1);
            cell.setCellValue(task.getName());
//
//            cell = row.createCell(2);
//            cell.setCellValue(task.getGroup().getName());

            cell = row.createCell(3);
            cell.setCellValue(task.getDescription());

//            cell = row.createCell(4);
//            cell.setCellValue(task.getStartDate());
//
//            cell = row.createCell(5);
//            cell.setCellValue(task.getEndDate());

            cell = row.createCell(6);
            cell.setCellValue(task.getUser().getName());

            cell = row.createCell(7);
            cell.setCellValue(task.getStatus().getStatus1());

            cell = row.createCell(8);
            cell.setCellValue(task.getModifyBy());
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
