package sg.gov.csit.datacatalogue.dcms.dataset;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DatasetService {
    @Autowired
    private DatasetRepository datasetRepository;

    public boolean IsDatasetInDatabase(long id){
        return datasetRepository.findById(id).isPresent();
    }

    public Optional<Dataset> getDatasetById(long datasetId) {
        return datasetRepository.findById(datasetId);
    }

    public String createNewDataset(@NotNull String name, String description) {
        if (datasetRepository.findByName(name) == null) {
            datasetRepository.save(new Dataset(name, description));
            DatabaseActions databaseActions = new DatabaseActions();
            try {
                System.out.println(databaseActions.createDatabase(name));
            } catch (Exception e) {
                System.out.println(e);
            }
            return "Dataset created";
        } else {
            throw new DatasetExistsException(name);
        }
    }

    public List<Dataset> getAllDatasets() { return datasetRepository.findAll(); }
}
