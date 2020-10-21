package sg.gov.csit.datacatalogue.dcms.datasetaccess;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class DatasetAccessService {
    @Autowired
    private DatasetAccessRepository datasetAccessRepository;

    public boolean IsDatasetInDatabase(long id){
        return datasetAccessRepository.findById(id).isPresent();
    }
}
