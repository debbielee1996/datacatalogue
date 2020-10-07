package sg.gov.csit.datacatalogue.dcms.logging;

public enum UserActionEnum {
    TEST_ENUM("Hello");

    UserActionEnum(String value){
        this.value = value;
    }

    private String value;
    public String getValue(){return value;}

}
