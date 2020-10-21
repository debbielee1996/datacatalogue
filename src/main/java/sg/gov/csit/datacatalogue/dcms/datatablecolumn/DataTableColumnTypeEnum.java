package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

public enum DataTableColumnTypeEnum {
    Text("Text"),
    Number("Number"),
    Date("Date");

    private String type;
    DataTableColumnTypeEnum(String type) { this.type=type; }
    public String getValue() { return this.type; }
}
