package sg.gov.csit.datacatalogue.dcms.datatableaccess;

public enum DataTableAccessTypeEnum {
    Pf("Pf");

    private String value;
    DataTableAccessTypeEnum(String value) { this.value=value; }
    public String getValue() { return this.value; }
}
