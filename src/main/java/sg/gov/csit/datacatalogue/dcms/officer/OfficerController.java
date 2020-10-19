package sg.gov.csit.datacatalogue.dcms.officer;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/officer")
@RestController
public class OfficerController {
    @Autowired
    private OfficerService officerService;

}
