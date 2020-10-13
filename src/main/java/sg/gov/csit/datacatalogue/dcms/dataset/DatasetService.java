package sg.gov.csit.datacatalogue.dcms.dataset;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
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

    public boolean createNewDataset(@NotNull String name, String description) {
        if (datasetRepository.findByName(name) == null) { // if dataset hasn't exist yet
            datasetRepository.save(new Dataset(name, description));
            DatabaseActions databaseActions = new DatabaseActions();
            try {
                boolean hasCreatedDataset = databaseActions.createDatabase(name);
                return hasCreatedDataset; // will get here if its true
            } catch (Exception e) {
                System.out.println(e);
                return false; // this means that dataset doesn't exist but the db for this dataset is alr created which is not possible unless you run the test multiple times
            }
        } else {
            throw new DatasetExistsException(name);
        }
    }

    public List<Dataset> getAllDatasets() { return datasetRepository.findAll(); }

    public List<DataTable> getDataTablesOfDataset(String datasetId) {
        return datasetRepository.findById(Long.parseLong(datasetId)).get().getDataTableList();
    }
}
