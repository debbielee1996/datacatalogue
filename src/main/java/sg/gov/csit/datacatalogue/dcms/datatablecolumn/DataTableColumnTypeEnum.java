package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

public enum DataTableColumnTypeEnum {
    Text("Text"),
    Date("Date"),
    Number_0dp("Whole number (0 decimal places)"),
    Number_2dp("Number (2 decimal places)"),
    Number_5dp("Number (5 decimal places)");

    private String type;
    DataTableColumnTypeEnum(String type) { this.type=type; }
    public String getValue() { return this.type; }
}
