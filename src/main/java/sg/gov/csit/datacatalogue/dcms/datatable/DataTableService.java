package sg.gov.csit.datacatalogue.dcms.datatable;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;

import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumnService;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;
import sg.gov.csit.datacatalogue.dcms.exception.IncorrectFileTypeException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerService;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Service
@AllArgsConstructor
public class DataTableService {
    @Autowired
    DataTableRepository dataTableRepository;

    @Autowired
    OfficerService officerService;

    @Autowired
    DataTableColumnService dataTableColumnService;

    @Autowired
    ModelMapper modelMapper;

    // setter injection for datasetService. because DataSetService will call DataTableService = circular dependency
    DatasetService datasetService;

    @Autowired
    public void setDatasetService(DatasetService datasetService) {
        this.datasetService=datasetService;
    }

    public boolean uploadFile(MultipartFile file, String tableName, String datasetId, String description, List<String> dataTypes, String pf) throws IOException, CsvException, SQLException {
        // verify officer exists
        Optional<Officer> officer = officerService.getOfficer(pf);
        if (officer.isEmpty()) {
            throw new OfficerNotFoundException(pf);
        }

        // get dataset and verify that it exists
        Optional<Dataset> dataset = datasetService.getDatasetById(Long.parseLong(datasetId));
        if (dataset.isEmpty()) {
            throw new DatasetExistsException(datasetId);
        }

        // check if dataTable already exists
        DataTable dataTable = dataTableRepository.findByNameAndDatasetId(tableName, Long.parseLong(datasetId));

        boolean dataTableExists = true; // set to true if dataTable doesn't exist
        if (dataTable == null) { // dataTable hasn't existed yet
            dataTableExists = false;
        }

        // verify table
        List<String> headerList = new ArrayList<>();
        List<List<String>> stringRecords = new ArrayList<>();
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        // currently only deals with .csv formats
        if (ext.equals("csv")) {
            Reader reader = new InputStreamReader(file.getInputStream());
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> stringRecordsArray = csvReader.readAll();
            headerList = Arrays.asList(stringRecordsArray.get(0));
            for (String[] array:stringRecordsArray) {
                stringRecords.add(Arrays.asList(array));
            }
            // remove headers
            stringRecords.remove(0);
            System.out.println("csv operations completed");
        } else if (ext.equals("xls") || ext.equals("xlsx")) {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                List<String> rowList = new ArrayList<>();

                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getCellTypeEnum()) {
                        case STRING:
                            rowList.add(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                rowList.add(new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue()));
                            } else {
                                rowList.add(Double.toString(cell.getNumericCellValue()));
                            }

                            break;
                    }
                }
                stringRecords.add(rowList);
            }
            headerList = stringRecords.get(0);
            stringRecords.remove(0);
            System.out.println("xlsx/xls operations completed");

        }else {
            throw new IncorrectFileTypeException(ext);
        }

        // allow users to choose their own header types. so far only Number/Date/Text
        List<String> headerTypes = new ArrayList<>();
        for (String s: dataTypes) {
            if (s.equals("Number")) {
                headerTypes.add(" NUMERIC");
            } else if (s.equals("Date")) {
                headerTypes.add(" DATE");
            } else {
                headerTypes.add(" varChar(255)");
            }
        }

        // create table and insert values
        DatabaseActions databaseActions = new DatabaseActions();
        boolean hasCreatedDatatable = databaseActions.createDatatable(tableName, headerList, headerTypes, stringRecords, dataset.get().getName(), dataTableExists);

        if (hasCreatedDatatable) {
            if (!dataTableExists) { // create dataTable object in db
                dataTable = new DataTable(tableName, description, dataset.get(), officer.get());
                dataTableRepository.save(dataTable);
            } else { // overwrite existing description with new one
                dataTable.setDescription(description);
                dataTableRepository.save(dataTable);
            }

            // create DataTableColumns
            List<DataTableColumn> dclist = new ArrayList<>();
            for (int i=0; i<headerList.size();i++) {
                DataTableColumn dtc = new DataTableColumn(headerList.get(i), "", dataTypes.get(i), dataTable);
                dclist.add(dtc);
            }
            dataTable.setDataTableColumnList(dclist);
            dataTableRepository.save(dataTable);

            System.out.println("Successfully uploaded data file into db");
            return true;
        } else {
            return false;
        }
    }

    public List<DataTableDto> getAllDataTableDtos() {
        List<DataTable> dataTables = dataTableRepository.findAll();
        return dataTables.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // converts Dataset to DatasetDto
    public DataTableDto convertToDto(DataTable dataTable) {
        DataTableDto dataTableDto = modelMapper.map(dataTable, DataTableDto.class);
        return dataTableDto;
    }
}
