package com.project.coupon.service.implementation;

import com.project.coupon.jpaEntities.StoreMaster;
import com.project.coupon.jpaRepositories.StoreMasterRepository;
import com.project.coupon.service.ExcelStoreFileSave;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class ExcelStoreFileSaveImplementation implements ExcelStoreFileSave {

    @Autowired
    StoreMasterRepository storeMasterRepository;

    //@Override
    public void save1(MultipartFile file) throws IOException {

        List<StoreMaster> storeMasterList=new ArrayList<>();
        Workbook workbook=new XSSFWorkbook(file.getInputStream());
        Sheet sheet= workbook.getSheetAt(0);

        for(Row row: sheet) {
            if(row.getRowNum()==0 ||  row.getCell(21)==null || row.getCell(21).toString().equals("NA")) {
                // Skip header row
                continue;
            }

            StoreMaster storeMaster=new StoreMaster();

            if(row.getCell(1)!=null)
            {
                storeMaster.setChampsId(getCellValueAsString(row.getCell(1)));
            }

            if(row.getCell(2)!=null )
            {
                storeMaster.setBu(getCellValueAsString(row.getCell(2)));
            }
            if(row.getCell(3)!=null )
            {
                storeMaster.setStoreCoden1(getCellValueAsString(row.getCell(3)));
            }
            if(row.getCell(4)!=null )
            {
                storeMaster.setStoreCoden2(getCellValueAsString(row.getCell(4)));
            }
            if(row.getCell(5)!=null)
            {
                storeMaster.setZomato(getCellValueAsString(row.getCell(5)));
            }

            if(row.getCell(6)!=null )
            {
                storeMaster.setSwiggy(getCellValueAsString(row.getCell(6)));
            }
            if(row.getCell(7)!=null )
            {
                storeMaster.setStoreNamePratap(getCellValueAsString(row.getCell(7)));
            }
            if(row.getCell(8)!=null)
            {
                storeMaster.setMaStoreName(getCellValueAsString(row.getCell(8)));
            }
            if(row.getCell(9)!=null)
            {
                storeMaster.setBdName(getCellValueAsString(row.getCell(9)));
            }
            if(row.getCell(10)!=null)
            {
                storeMaster.setPAndLName(getCellValueAsString(row.getCell(10)));
            }
            if(row.getCell(13)!=null)
            {
                storeMaster.setState(getCellValueAsString(row.getCell(13)));
            }
            if(row.getCell(20)!=null)
            {
              storeMaster.setOperationalStatus(getCellValueAsString(row.getCell(20)));
            }
            if(row.getCell(26)!=null)
            {
                storeMaster.setStoreCode(getCellValueAsString(row.getCell(26)));
            }
            if(row.getCell(27)!=null)
            {
                storeMaster.setName(getCellValueAsString(row.getCell(27)));
            }
            if(row.getCell(28)!=null)
            {
                storeMaster.setCity(getCellValueAsString(row.getCell(28)));
            }

            if(row.getCell(29)!=null)
            {
                storeMaster.setFranchise(getCellValueAsString(row.getCell(29)));
            }

            if(row.getCell(30)!=null)
            {
                storeMaster.setZone(getCellValueAsString(row.getCell(30)));
            }

            if(row.getCell(31)!=null)
            {
                storeMaster.setFormat(getCellValueAsString(row.getCell(31)));
            }

            if(row.getCell(33)!=null)
            {
                storeMaster.setRegion(getCellValueAsString(row.getCell(33)));
            }

            if(row.getCell(34)!=null)
            {
                storeMaster.setTier(getCellValueAsString(row.getCell(34)));
            }

            if(row.getCell(35)!=null)
            {
                storeMaster.setCluster(getCellValueAsString(row.getCell(35)));
            }

            storeMasterList.add(storeMaster);
        }
        storeMasterRepository.saveAll(storeMasterList);
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
                    // Convert numeric value to a string without scientific notation
                    return String.valueOf((long) cell.getNumericCellValue());
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

    @Override
    public void save(MultipartFile file) throws IOException {

        List<StoreMaster> storeMasterList=new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()))
        {

            for (CSVRecord csvRecord : csvParser) {
                StoreMaster storeMaster = new StoreMaster();
                if(csvRecord.get("CODE")!="NA") {
                    storeMaster.setStoreCode(csvRecord.get("CODE"));
                }
                else {
                    continue;
                }

                storeMaster.setChampsId(csvRecord.get("Champs"));

                storeMaster.setBu(csvRecord.get("BU"));

                storeMaster.setStoreCoden1(csvRecord.get("Store coden1"));

                storeMaster.setStoreCoden2(csvRecord.get("Store code"));

                storeMaster.setZomato(csvRecord.get("Zomato"));
                storeMaster.setSwiggy(csvRecord.get("Swiggy"));
                storeMaster.setStoreNamePratap(csvRecord.get("Store Name Pratap"));
                storeMaster.setMaStoreName(csvRecord.get("MA Store Name"));
                storeMaster.setBdName(csvRecord.get("BD Name"));
                storeMaster.setPAndLName(csvRecord.get("P&L Name"));
                storeMaster.setState(csvRecord.get("State"));
                storeMaster.setOperationalStatus(csvRecord.get("Operational Status"));
                storeMaster.setName(csvRecord.get("Name"));
                storeMaster.setCity(csvRecord.get("City"));
                storeMaster.setFranchise(csvRecord.get("FZE"));
                storeMaster.setZone(csvRecord.get("Zone"));
                storeMaster.setFormat(csvRecord.get("Format"));
                storeMaster.setRegion(csvRecord.get("Region"));
                storeMaster.setTier(csvRecord.get("Tier"));
                storeMaster.setCluster(csvRecord.get("Cluster"));

                storeMasterList.add(storeMaster);
            }
        }

        storeMasterRepository.saveAll(storeMasterList);
    }
}
