package sg.gov.csit.datacatalogue.dcms.datatable;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvFormat;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;

import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;
import sg.gov.csit.datacatalogue.dcms.exception.IncorrectFileTypeException;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class DataTableService {
    @Autowired
    DataTableRepository dataTableRepository;

    @Autowired
    DatasetService datasetService;

    public List<DataTable> getAllDatatables() { return dataTableRepository.findAll(); }

    public boolean uploadFile(MultipartFile file, String tableName, String datasetId, String description, List<String> dataTypes) {
        // get dataset and verify that it exists
        Optional<Dataset> dataset = datasetService.getDatasetById(Long.parseLong(datasetId));
        if (dataset.isEmpty()) {
            throw new DatasetExistsException(datasetId);
        }

        // check if dataTable already exists
        DataTable dataTable = dataTableRepository.findByName(tableName);
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
            try {
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVReader csvReader = new CSVReader(reader);
                List<String[]> stringRecordsArray = csvReader.readAll();
                headerList = Arrays.asList(stringRecordsArray.get(0));
                for (String[] array:stringRecordsArray) {
                    stringRecords.add(Arrays.asList(array));
                }
            } catch (IOException | CsvException e) {
                System.out.println(e);
            }
            // remove headers
            stringRecords.remove(0);
            System.out.println("csv operations completed");
        } else {
            throw new IncorrectFileTypeException(ext);
        }

        // temp placement until user can choose their own header types
        List<String> headerTypes = new ArrayList<>();
        for (String s: dataTypes) {
            if (s.equals("Number")) {
                headerTypes.add(" INT");
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
                dataTableRepository.save(new DataTable(tableName, description, dataset.get()));
            }
            System.out.println("Successfully uploaded data file into db");
            return true;
        } else {
            return false;
        }
    }

    public List<String> getAllDataTableNames() { return dataTableRepository.findAllDataTableNames(); }
}
