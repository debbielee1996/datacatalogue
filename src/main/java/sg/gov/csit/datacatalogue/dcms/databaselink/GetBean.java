package sg.gov.csit.datacatalogue.dcms.databaselink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GetBean {
    public static String currentMavenProfile;
    public static String currentDataBaseDriver;
    public static String currentDataBaseUrl;
    public static String userName;
    public static String password;

    @Autowired
    public GetBean(@Value("${spring.profiles.active}") String currentMavenProfile,
                   @Value("${spring.datasource.driverClassName}") String currentDataBaseDriver,
                   @Value("${spring.datasource.url}") String currentDataBaseUrl,
                   @Value("${spring.datasource.username}") String userName,
                   @Value("${spring.datasource.password}") String password


    ) {
        this.currentMavenProfile = currentMavenProfile;
        this.currentDataBaseDriver = currentDataBaseDriver;
        this.currentDataBaseUrl = currentDataBaseUrl;
        this.userName = userName;
        this.password = password;
    }

}
