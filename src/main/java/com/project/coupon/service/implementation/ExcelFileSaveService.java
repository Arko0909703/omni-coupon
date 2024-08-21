package com.project.coupon.service.implementation;

import com.project.coupon.jpaEntities.ProductMaster;
import com.project.coupon.jpaRepositories.ProductMasterRepository;
import com.project.coupon.service.ExcelFileSave;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class ExcelFileSaveService implements ExcelFileSave {

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Override
    public void save(MultipartFile file) throws IOException {

        List<ProductMaster> productMasterList=new ArrayList<>();
        Workbook workbook=new XSSFWorkbook(file.getInputStream());
        Sheet sheet= workbook.getSheetAt(0);

        for(Row row: sheet)
        {
            if (row.getRowNum() == 0) {
                // Skip header row
                continue;
            }
            ProductMaster productMaster=new ProductMaster();
            if(row.getCell(0)!=null)
            {
                productMaster.setItemCode(getCellValueAsString(row.getCell(0)));
            }
            else {
                System.out.println("Get all cell values: "+row.getCell(1) + " - "+ row.getCell(2)+ " - "+ row.getCell(3)+ " - "+ row.getCell(4));
                throw new IllegalArgumentException("Product ID is missing or invalid in the Excel file.");
            }

            if(row.getCell(1)!=null )
            {
                productMaster.setDescription(getCellValueAsString(row.getCell(1)));
            }

            if(row.getCell(2)!=null )
            {
                productMaster.setCategory(getCellValueAsString(row.getCell(2)));
            }

            if(row.getCell(3)!=null )
            {
                productMaster.setSubCategory(getCellValueAsString(row.getCell(3)));
            }

            if(row.getCell(4)!=null )
            {
                productMaster.setName(getCellValueAsString(row.getCell(4)));
            }

            if(row.getCell(5)!=null)
            {
                productMaster.setProductRange(getCellValueAsString(row.getCell(5)));
            }

            if(row.getCell(6)!=null )
            {
                productMaster.setSize(getCellValueAsString(row.getCell(6)));
            }

            if(row.getCell(7)!=null )
            {
                productMaster.setDietPreference(getCellValueAsString(row.getCell(7)));
            }

            if(row.getCell(8)!=null )
            {
                productMaster.setOldOrFresh(getCellValueAsString(row.getCell(8)));
            }

            productMasterList.add(productMaster);
        }

        productMasterRepository.saveAll(productMasterList);
        workbook.close();
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    String value=String.valueOf(cell.getNumericCellValue());
                    return value.substring(0,value.length()-2);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }
}
