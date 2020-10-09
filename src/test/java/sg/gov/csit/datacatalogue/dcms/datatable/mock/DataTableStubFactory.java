package sg.gov.csit.datacatalogue.dcms.datatable.mock;

import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class DataTableStubFactory {
    public static FileInputStream FILESTREAM_CSVFILE() throws FileNotFoundException {
        String userDirectory = Paths.get("").toAbsolutePath().toString();
        return new FileInputStream(userDirectory+"\\src\\test\\java\\sg\\gov\\csit\\datacatalogue\\dcms\\datatable\\testfiles\\test.csv");
    }

    public static FileInputStream FILESTREAM_PDFFILE() throws FileNotFoundException {
        String userDirectory = Paths.get("").toAbsolutePath().toString();
        return new FileInputStream(userDirectory+"\\src\\test\\java\\sg\\gov\\csit\\datacatalogue\\dcms\\datatable\\testfiles\\test.pdf");
    }

    public static Dataset DATASET() {
        return new Dataset("dataset1", "mock dataset");
    }
}
