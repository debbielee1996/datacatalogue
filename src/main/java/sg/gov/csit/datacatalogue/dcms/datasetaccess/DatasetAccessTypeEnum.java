package sg.gov.csit.datacatalogue.dcms.datasetaccess;

public enum DatasetAccessTypeEnum {
    Pf("Pf");

    private String value;
    DatasetAccessTypeEnum(String value) { this.value=value; }
    public String getValue() { return this.value; }
}
