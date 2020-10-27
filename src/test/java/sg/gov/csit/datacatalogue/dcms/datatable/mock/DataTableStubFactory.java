package sg.gov.csit.datacatalogue.dcms.datatable.mock;

import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataTableStubFactory {
    public static FileInputStream FILESTREAM_CSVFILE() throws FileNotFoundException {
        String userDirectory = Paths.get("").toAbsolutePath().toString();
        return new FileInputStream(userDirectory+"\\src\\test\\java\\sg\\gov\\csit\\datacatalogue\\dcms\\datatable\\testfiles\\test.csv");
    }

    public static List<String> DATATYPES_CSVFILE() {
        List<String> dataTypes = new ArrayList<>();
        dataTypes.add("Text");
        dataTypes.add("Text");
        return dataTypes;
    }

    public static List<String> DATACOLDESC_CSVFILE() {
        List<String> list = new ArrayList<>();
        list.add("name of officer");
        list.add("age of officer");
        return list;
    }

    public static FileInputStream FILESTREAM_XLSXFILE() throws FileNotFoundException {
        String userDirectory = Paths.get("").toAbsolutePath().toString();
        return new FileInputStream(userDirectory+"\\src\\test\\java\\sg\\gov\\csit\\datacatalogue\\dcms\\datatable\\testfiles\\test.xlsx");
    }

    public static List<String> DATATYPES_XLSXFILE() {
        List<String> dataTypes = new ArrayList<>();
        dataTypes.add("Text");
        dataTypes.add("Text");
        dataTypes.add("Text");
        dataTypes.add("Text");
        dataTypes.add("Text");
        dataTypes.add("Text");
        dataTypes.add("Date");
        dataTypes.add("Number");

        return dataTypes;
    }
    
    public static List<String> DATACOLDESC_XLSXFILE() {
        List<String> list = new ArrayList<>();
        list.add("index of row");
        list.add("first name of officer");
        list.add("last name of officer");
        list.add("gender of officer");
        list.add("nationality of officer");
        list.add("age of officer");
        list.add("dob of officer");
        list.add("pf of officer");
        return list;
    }

    public static FileInputStream FILESTREAM_PDFFILE() throws FileNotFoundException {
        String userDirectory = Paths.get("").toAbsolutePath().toString();
        return new FileInputStream(userDirectory+"\\src\\test\\java\\sg\\gov\\csit\\datacatalogue\\dcms\\datatable\\testfiles\\test.pdf");
    }

    public static Officer OFFICER() {
        return new Officer("123","test","testEmail", "123", "System Admin");
    }

    public static Dataset DATASET() {
        return new Dataset("DataTableSericeTest_dataset1", "mock dataset", OFFICER());
    }


}
