package sg.gov.csit.datacatalogue.dcms.dataset;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccessTypeEnum;
import sg.gov.csit.datacatalogue.dcms.exception.*;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class DatasetService {
    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private OfficerRepository officerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager em;

    public boolean createNewDataset(@NotNull String name, String description, String pf, List<String> custodianPfs, String ownerPf) {
        if (datasetRepository.findByName(name) == null) { // if dataset hasn't exist yet
            Optional<Officer> officer = officerRepository.findByPf(pf);
            if (officer.isEmpty()) {
                throw new OfficerNotFoundException(pf);
            }
            // check if owner exists
            Optional<Officer> owner = officerRepository.findByPf(ownerPf);
            if (owner.isEmpty()) {
                throw new OfficerNotFoundException(ownerPf);
            }

            // check if all custodians exists
            for (String custodianPf:custodianPfs) {
                Optional<Officer> custodian = officerRepository.findByPf(custodianPf);
                if (custodian.isEmpty()) {
                    throw new OfficerNotFoundException(custodianPf);
                }
            }

            DatabaseActions databaseActions = new DatabaseActions();
            try {
                boolean hasCreatedDataset = databaseActions.createDatabase(name);

                // create dataset JPA entity only if hasCreatedDataset is true
                Dataset dataset = new Dataset(name, description, owner.get(), false);
                DatasetAccess datasetAccess = new DatasetAccess(dataset, "Pf", pf); // add access for creator of dataset
                dataset.getDatasetAccessList().add(datasetAccess);
                datasetRepository.save(dataset);

                // add custodians
                for (String custodianPf:custodianPfs) {
                    addOfficerToCustodianList(custodianPf, dataset.getId());
                }
                return hasCreatedDataset; // will get here if its true
            } catch (Exception e) {
                System.out.println(e);
                return false; // this means that dataset doesn't exist but the db for this dataset is alr created which is not possible unless you run the test multiple times
            }
        } else {
            throw new DatasetExistsException(name);
        }
    }

    public boolean ValidateOfficerDatasetAccess(String pf, long datasetId) {
        if(officerRepository.findByPf(pf).isPresent()){
            Optional<Dataset> dataset = datasetRepository.findById(datasetId);
            if(dataset.isPresent()){ // if dataset exists
                List<DatasetAccess> datasetAccessList = dataset.get().getDatasetAccessList();
                return officerHasAccessForDataset(pf, datasetAccessList);
            }else{
                throw new DatasetNotFoundException(datasetId);
            }
        }else{
            throw new OfficerNotFoundException(pf);
        }
    }

    public boolean officerHasAccessForDataset(String pf, List<DatasetAccess> datasetAccessList) {
        for (DatasetAccess da:datasetAccessList) {
            // DatasetAccessService check
            // check if value is officer("Pf")
            if (da.getTypeInString().equals("Pf") & da.getValue().equals(pf)) { // if value is 'pf' check pf = pf (this officer's)
                return true;
            }
        }
        return false;
    }

    public List<DatasetDto> getDatasetsCreatedByOfficer(String pf) {
        return datasetRepository.findByOfficerPf(pf).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DatasetDto> getAllDatasetDtos(String pf) {
        List<Dataset> datasets = datasetRepository.findAll();
        List<Dataset> filteredDatasets = datasets.stream()
                .filter(d -> ValidateOfficerDatasetAccess(pf, d.getId()))
                .collect(Collectors.toList());

        return filteredDatasets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // converts Dataset to DatasetDto
    private DatasetDto convertToDto(Dataset dataset) {
        DatasetDto datasetDto = modelMapper.map(dataset, DatasetDto.class);
        return datasetDto;
    }

    public boolean deleteDataset(String datasetId) {
        datasetRepository.deleteById(Long.parseLong(datasetId));
        return true;
    }

    public boolean addOfficerDatasetAccess(String officerPf, String datasetId) {
        Optional<Dataset> dataset = datasetRepository.findById(Long.parseLong(datasetId));
        if (dataset.isPresent()) {
            List<DatasetAccess> datasetAccessList = dataset.get().getDatasetAccessList();
            if (!officerHasAccessForDataset(officerPf, datasetAccessList)) {
                datasetAccessList.add(new DatasetAccess(dataset.get(),"Pf", officerPf));
                datasetRepository.save(dataset.get());
            }
            return true;
        } else {
            throw new DatasetNotFoundException(Long.parseLong(datasetId));
        }
    }

    public boolean removeOfficerDatasetAccess(String officerPf, String datasetId) {
        Optional<Dataset> dataset = datasetRepository.findById(Long.parseLong(datasetId));
        if(dataset.isPresent()) {
            List<DatasetAccess> datasetAccessList = dataset.get().getDatasetAccessList();
            if (officerHasAccessForDataset(officerPf, datasetAccessList)) {
                datasetAccessList.removeIf(da -> da.getType() == DatasetAccessTypeEnum.Pf && da.getValue().equals(officerPf));
                datasetRepository.save(dataset.get());
            }
            return true;
        } else {
            throw new DatasetNotFoundException(Long.parseLong(datasetId));
        }
    }

    public boolean addOfficerToCustodianList(String pf, long datasetId) {
        Optional<Officer> officer = officerRepository.findByPf(pf);
        if (officer.isEmpty()) {
            throw new OfficerNotFoundException(pf);
        }

        Optional<Dataset> dataset = datasetRepository.findById(datasetId);
        if (dataset.isEmpty()) {
            throw new DatasetNotFoundException(datasetId);
        }

        Officer officerQueried = em.createQuery("select officer from Officer officer left join fetch officer.datasetCustodianList where officer = :officer", Officer.class)
                .setParameter("officer", officer.get())
                .getSingleResult();

        officerQueried.addDatasetCustodian(dataset.get());
        officerRepository.save(officerQueried);
        return true;
    }

    public boolean datasetNameIsUnique(String name) {
        return datasetRepository.findByName(name)==null;
    }
}
