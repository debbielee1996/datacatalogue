package sg.gov.csit.datacatalogue.dcms.dataset.mock;

import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;

public class DatasetStubFactory {
    public static Officer MOCK_OFFICER() {
        return new Officer("123","test","testEmail", "123", "System Admin");
    }
    public static Officer MOCK_OFFICER2() {
        return new Officer("456","test2","testEmail2", "456", "System Admin");
    }

    public static Dataset MOCK_DATASET_NOACCESSLIST() {
        Dataset dataset = new Dataset("mock", "mock", MOCK_OFFICER(), false);
        dataset.setId(Long.parseLong("123"));
        return dataset;
    }
}
