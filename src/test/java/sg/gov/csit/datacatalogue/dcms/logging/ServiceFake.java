package sg.gov.csit.datacatalogue.dcms.logging;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ServiceFake {
    public String testing(){
        return "Hello World";
    }
}
