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
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetRepository;

import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccess;
import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccessTypeEnum;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;
import sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess.DataTableColumnAccess;
import sg.gov.csit.datacatalogue.dcms.exception.*;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

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
    OfficerRepository officerRepository;

    @Autowired
    DatasetRepository datasetRepository;

    @Autowired
    ModelMapper modelMapper;

    public boolean uploadFile(MultipartFile file, String tableName, String datasetId, String description, List<String> dataTypes, String pf, List<String> dataColDescriptions) throws IOException, CsvException, SQLException {
        // verify officer exists
        Optional<Officer> officer = officerRepository.findByPf(pf);
        if (officer.isEmpty()) {
            throw new OfficerNotFoundException(pf);
        }

        // get dataset and verify that it exists
        Optional<Dataset> dataset = datasetRepository.findById(Long.parseLong(datasetId));
        if (dataset.isEmpty()) {
            throw new DatasetNotFoundException(Long.parseLong(datasetId));
        }

        // check if officer is custodian/owner
        if (!dataset.get().getOfficer().getPf().equals(officer.get().getPf()) && // check ownership
                (dataset.get().getOfficerCustodianList().stream().filter(custodianOfficer -> custodianOfficer.getPf().equals(officer.get().getPf())).count()==0)) { // check custodianship
            throw new DatasetAccessNotFoundException(pf, Long.parseLong(datasetId));
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
            switch(s) {
                case "Whole number (0 decimal places)":
                    headerTypes.add(" decimal(18, 0)");
                    break;
                case "Number (2 decimal places)":
                    headerTypes.add(" decimal(18, 2)");
                    break;
                case "Number (5 decimal places)":
                    headerTypes.add(" decimal(18, 5)");
                    break;
                case "Date":
                    headerTypes.add(" DATE");
                    break;
                case "Text":
                    headerTypes.add(" varChar(255)");
                    break;
            }
        }
        // create table and insert values
        DatabaseActions databaseActions = new DatabaseActions();
        boolean hasCreatedDatatable = databaseActions.createDatatable(tableName, headerList, headerTypes, stringRecords, dataset.get().getName(), dataTableExists);

        if (hasCreatedDatatable) {
            if (!dataTableExists) { // create dataTable object in db
                dataTable = new DataTable(tableName, description, dataset.get(), officer.get());
                DataTableAccess dataTableAccess = new DataTableAccess(dataTable, "Pf", pf); // add access for creator of datatable
                dataTable.getDataTableAccessList().add(dataTableAccess);
            } else { // overwrite existing description with new one
                dataTable.setDescription(description);
            }
            dataTableRepository.save(dataTable);

            // create DataTableColumns
            dataTable.getDataTableColumnList().clear(); // drop existing dataTableColumns
            for (int i=0; i<headerList.size();i++) {
                DataTableColumn dtc = new DataTableColumn(headerList.get(i), dataColDescriptions.get(i), dataTypes.get(i), dataTable);

                // add access for dtc. By default creator can view all datatable columns
                DataTableColumnAccess dataTableColumnAccess = new DataTableColumnAccess(dtc, "Pf", pf); // add access for creator of datatable
                dtc.getDataTableColumnAccessList().add(dataTableColumnAccess);

                // add dataTableColumn entity to dataTable
                dataTable.getDataTableColumnList().add(dtc);
            }
            dataTableRepository.save(dataTable);

            System.out.println("Successfully uploaded data file into db");
            return true;
        } else {
            return false;
        }
    }

    public List<DataTableDto> getDataTablesOfDataset(String pf, String datasetId) {
        List<DataTable> dataTables = dataTableRepository.findByDatasetId(Long.parseLong(datasetId));
        List<DataTable> filteredDataTables = dataTables.stream()
                .filter(d -> ValidateOfficerDataTableAccess(pf, d.getId()))
                .collect(Collectors.toList());

        return filteredDataTables.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DataTableDto> getAllDataTableDtos(String pf) {
        List<DataTable> dataTables = dataTableRepository.findAll();
        List<DataTable> filteredDataTables = dataTables.stream()
                                            .filter(d -> ValidateOfficerDataTableAccess(pf, d.getId()))
                                            .collect(Collectors.toList());

        return filteredDataTables.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public boolean ValidateOfficerDataTableAccess(String pf, Long dataTableId) {
        if (officerRepository.findByPf(pf).isPresent()) { // if officer exists
            Optional<DataTable> dataTable = dataTableRepository.findById(dataTableId);
            if(dataTable.isPresent()) { // if datatable exists
                List<DataTableAccess> dataTableAccessList = dataTable.get().getDataTableAccessList();
                return officerHasAccessForDataTable(pf, dataTableAccessList);
            } else {
                throw new DataTableNotFoundException(dataTableId);
            }
        } else {
            throw new OfficerNotFoundException(pf);
        }
    }

    public boolean officerHasAccessForDataTable(String pf, List<DataTableAccess> dataTableAccessList) {
        for (DataTableAccess dta:dataTableAccessList) {
            // DataTableAccessService check
            // check if value is officer("Pf")
            if (dta.getTypeInString().equals("Pf") & dta.getValue().equals(pf)) { // if value is 'pf' check pf = pf (this officer's)
                return true;
            }
        }
        return false;
    }

    public List<DataTableDto> getDataTablesCreatedByOfficer(String pf) {
        return dataTableRepository.findByOfficerPf(pf).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // converts Dataset to DatasetDto
    public DataTableDto convertToDto(DataTable dataTable) {
        DataTableDto dataTableDto = modelMapper.map(dataTable, DataTableDto.class);
        return dataTableDto;
    }

    public boolean addOfficerDataTableAccess(String officerPf, String dataTableId) {
        Optional<DataTable> dataTable = dataTableRepository.findById(Long.parseLong(dataTableId));
        if (dataTable.isPresent()) {
            List<DataTableAccess> dataTableAccessList = dataTable.get().getDataTableAccessList();
            if (!officerHasAccessForDataTable(officerPf, dataTableAccessList)) {
                dataTableAccessList.add(new DataTableAccess(dataTable.get(),"Pf", officerPf));
                dataTableRepository.save(dataTable.get());
            }
            return true;
        } else {
            throw new DataTableNotFoundException(Long.parseLong(dataTableId));
        }
    }

    public Optional<DataTable> getDataTableById(long dataTableId) {
        return dataTableRepository.findById(dataTableId);
    }

    public boolean removeOfficerDataTableAccess(String officerPf, String dataTableId) {
        Optional<DataTable> dataTable = dataTableRepository.findById(Long.parseLong(dataTableId));
        if(dataTable.isPresent()) {
            List<DataTableAccess> dataTableAccessList = dataTable.get().getDataTableAccessList();
            if (officerHasAccessForDataTable(officerPf, dataTableAccessList)) {
                dataTableAccessList.removeIf(dta -> dta.getType() == DataTableAccessTypeEnum.Pf && dta.getValue().equals(officerPf));
                dataTableRepository.save(dataTable.get());
            }
            return true;
        } else {
            throw new DataTableNotFoundException(Long.parseLong(dataTableId));
        }
    }

    public boolean editDataTableDescription(String description, Long dataTableId) {
        Optional<DataTable> dataTable = dataTableRepository.findById(dataTableId);
        System.out.println(dataTable.isEmpty());
        if (dataTable.isEmpty()) {
            throw new DataTableNotFoundException(dataTableId);
        }
        DataTable actualDataTable = dataTable.get();
        actualDataTable.setDescription(description);
        dataTableRepository.save(actualDataTable);
        return true;
    }

    public boolean dataTableNameIsUnique(String dataTableName, Long datasetId) { return dataTableRepository.findByNameAndDatasetId(dataTableName, datasetId)==null; }
}
