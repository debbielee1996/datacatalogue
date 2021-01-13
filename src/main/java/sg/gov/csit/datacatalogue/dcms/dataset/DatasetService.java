package sg.gov.csit.datacatalogue.dcms.dataset;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccessTypeEnum;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTableRepository;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumnRepository;
import sg.gov.csit.datacatalogue.dcms.exception.*;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class DatasetService {
    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private DataTableRepository dataTableRepository;

    @Autowired
    private DataTableColumnRepository dataTableColumnRepository;

    @Autowired
    private OfficerRepository officerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager em;

    public boolean createNewDataset(@NotNull String name, String description, String pf,Boolean isPublic) {
        if (datasetRepository.findByName(name) == null) { // if dataset hasn't exist yet
            Optional<Officer> officer = officerRepository.findByPf(pf);
            if (officer.isEmpty()) {
                throw new OfficerNotFoundException(pf);
            }
            DatabaseActions databaseActions = new DatabaseActions();
            try {
                boolean hasCreatedDataset = databaseActions.createDatabase(name);

                // create dataset JPA entity only if hasCreatedDataset is true
                Dataset dataset = new Dataset(name, description, officer.get(), isPublic);
                DatasetAccess datasetAccess = new DatasetAccess(dataset, "Pf", pf); // add access for creator of dataset
                dataset.getDatasetAccessList().add(datasetAccess);
                datasetRepository.save(dataset);
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

    public List<DatasetDto> getAllPublicDatasetDtos(String pf) {
//filter get dataset that can be view by public and get dataset that user have access to which is private.
        List<Dataset> datasets = datasetRepository.findAll();
        List<Dataset> filteredDatasets = datasets.stream()
                .filter(d -> d.getIsPublic()==true)
                .collect(Collectors.toList());

        return filteredDatasets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()) ;
    }

    // converts Dataset to DatasetDto
    private DatasetDto convertToDto(Dataset dataset) {
        DatasetDto datasetDto = modelMapper.map(dataset, DatasetDto.class);
        return datasetDto;
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

    public boolean editDataSetPrivacy(Boolean isPublic , long datasetId, String pf) {
        // verify dataset exists
        Optional<Dataset> dataset = datasetRepository.findById(datasetId);
        System.out.println(dataset.isEmpty());
        if (dataset.isEmpty()) {
            throw new DatasetNotFoundException(datasetId);
        }
        // verify officer exists
        Optional<Officer> officer = officerRepository.findByPf(pf);
        if (officer.isEmpty()) {
            throw new OfficerNotFoundException(pf);
        }
        Dataset actualDataset = dataset.get(); // get dataset
        List<DataTable> dataTableList = actualDataset.getDataTableList();
        // verify if officer is custodian/owner
        if (!actualDataset.getOfficer().getPf().equals(officer.get().getPf()) && // check ownership
                (actualDataset.getOfficerCustodianList().stream().filter(custodianOfficer -> custodianOfficer.getPf().equals(officer.get().getPf())).count()==0)) { // check custodianship
            throw new DatasetAccessNotFoundException(pf, actualDataset.getId());
        }
//        if it is private, set dataset and its datatable as private

//            set dataset to public/private
            actualDataset.setIsPublic(isPublic);
            datasetRepository.save(actualDataset);
//            set each datatable of the dataset to private
            for (DataTable dt : dataTableList) {
                dt.setIsPublic(isPublic);
                dataTableRepository.save(dt);
                List<DataTableColumn> dataTableColumnList = dt.getDataTableColumnList();

                // set each datatablecolumn to private
                for (DataTableColumn dtc : dataTableColumnList) {
                    dtc.setIsPublic(isPublic);
                    dataTableColumnRepository.save(dtc);
                }
            }


        return true;
    }
}
