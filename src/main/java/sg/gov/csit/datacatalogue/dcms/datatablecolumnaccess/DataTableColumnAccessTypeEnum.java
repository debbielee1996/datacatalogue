package sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess;

public enum DataTableColumnAccessTypeEnum {
    Pf("Pf");

    String value;
    DataTableColumnAccessTypeEnum(String value) { this.value=value; }
    public String getValue() { return this.value; }
}
