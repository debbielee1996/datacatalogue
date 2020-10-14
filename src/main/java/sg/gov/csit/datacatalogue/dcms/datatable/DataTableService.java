package sg.gov.csit.datacatalogue.dcms.datatable;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.AfterAll;
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

import org.apache.commons.csv.CSVFormat;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.IncorrectFileTypeException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
                CsvFormat format = getDelimiter(file); // gets delimiter from file
                CSVParser csvParser = CSVFormat.newFormat(format.getDelimiter()).parse(new BufferedReader(new InputStreamReader(file.getInputStream())));
                List<CSVRecord> records = csvParser.getRecords();
                headerList = getListFromIterator(records.get(0).iterator()); // converts iterator to list of headers
                stringRecords = getTableValuesFromCSV(records); // includes header as well
            } catch (IOException e) {
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

    public CsvFormat getDelimiter(MultipartFile file) throws IOException {
        CsvParserSettings settings = new CsvParserSettings();
        settings.detectFormatAutomatically();
        CsvParser parser = new CsvParser(settings);
        List<String[]> rows = parser.parseAll(file.getInputStream());
        return parser.getDetectedFormat();
    }

    //https://www.geeksforgeeks.org/convert-an-iterator-to-a-list-in-java/
    public static <T> List<T> getListFromIterator(Iterator<T> iterator) {
        // convert iterator to iterable
        Iterable<T> iterable = () -> iterator;

        // create list from iterable
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    //returns a 2D array
    public List<List<String>> getTableValuesFromCSV(List<CSVRecord> records) {
        List<List<String>> values = new ArrayList<>();

        for (CSVRecord record: records) {
            List<String> strings = new ArrayList<>(); // each row in csv
            for (String string: getListFromIterator(record.iterator())) {
                strings.add('\'' + string + '\'');
            }
            values.add(strings);
        }
        return values;
    }
}
