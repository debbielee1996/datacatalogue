package sg.gov.csit.datacatalogue.dcms.datatable;

import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVParser;
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

import org.apache.commons.csv.CSVFormat;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public String uploadFile(MultipartFile file, String tableName, String datasetId) throws Exception {
        // get dataset
        Optional<Dataset> dataset = datasetService.getDatasetById(Long.parseLong(datasetId));

        // verify table
        List<String> headerList = new ArrayList<>();
        List<List<String>> stringRecords = new ArrayList<>();

        String ext = FilenameUtils.getExtension(file.getOriginalFilename());

        if (ext == null) {
            return("Extension is null");
        } else if (ext.equals("csv")) {
            CsvFormat format = getDelimiter(file);
            CSVParser csvParser = CSVFormat.newFormat(format.getDelimiter()).parse(new BufferedReader(new InputStreamReader(file.getInputStream())));
            List<CSVRecord> records = csvParser.getRecords();
            headerList = getListFromIterator(records.get(0).iterator());
            stringRecords = getTableValuesFromCSV(records);

            // remove headers
            stringRecords.remove(0);
            System.out.println("csv operations completed");
        }

        // temp placement until user can choose their own header types
        List<String> headerTypes = new ArrayList<>();
        for (String s: headerList) { headerTypes.add(" varChar(255)"); }

        // create table and insert values
        DatabaseActions databaseActions = new DatabaseActions();
        boolean hasCreatedDatatable = databaseActions.createDatatable(tableName, headerList, headerTypes, stringRecords, dataset.get().getName());

        if (hasCreatedDatatable) {
            return "hiiiiiiiiiiiiiiiiiiiiiiiii";
        } else {
            return "byeeeeeeeeeeeeeeeeeeeeeeee";
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
        Iterable<T> iterable = () -> iterator;

        return StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    //returns a 2D array
    public List<List<String>> getTableValuesFromCSV(List<CSVRecord> records) {
        List<List<String>> values = new ArrayList<>();

        for (CSVRecord record: records) {
            List<String> strings = new ArrayList<>();
            for (String string: getListFromIterator(record.iterator())) {
                strings.add('\'' + string + '\'');
            }
            values.add(strings);
        }

        return values;
    }
}
