package sg.gov.csit.datacatalogue.dcms.dataset;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class DatasetService {
    @Autowired
    private DatasetRepository datasetRepository;

    public boolean IsDatasetInDatabase(long id){
        return datasetRepository.findById(id).isPresent();
    }

//    public boolean IsOfficerAccessDataset(String officerPf){
//
//    }
    public Optional<Dataset> getDataset(long datasetId) {
        return datasetRepository.findById(datasetId);
    }
}
