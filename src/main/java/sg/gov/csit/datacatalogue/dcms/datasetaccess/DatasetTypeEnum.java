package sg.gov.csit.datacatalogue.dcms.datasetaccess;

public enum DatasetTypeEnum {
    Pf("Pf"), Ddcs("Ddcs");

    private String value;
    DatasetTypeEnum(String value) { this.value=value; }
    public String getValue() { return this.value; }
}
