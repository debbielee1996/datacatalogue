package sg.gov.csit.datacatalogue.dcms.logging;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class ServiceFake {
    public String testing(){
        return "Hello World";
    }
    public String testingParameter(int number, String word){
        return "Parsed: " + Integer.toString(number) + " " + word;
    }

    public <T> String testingParameterList(List<T> mockList){
        return mockList.toString();
    }
}
