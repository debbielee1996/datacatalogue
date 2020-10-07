package sg.gov.csit.datacatalogue.dcms.acl;

public enum AclRoleEnum {
    SYSTEM_ADMIN("System Admin"),
    PUBLIC("Public");

    private String value;
    AclRoleEnum(String value){
        this.value = value;
    }
    public String getValue(){return this.value;}
}
