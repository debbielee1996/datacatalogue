package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetRepository;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTableRepository;
import sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess.DataTableColumnAccess;
import sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess.DataTableColumnAccessTypeEnum;
import sg.gov.csit.datacatalogue.dcms.exception.DataTableColumnNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DataTableNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetAccessNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DataTableColumnService {
    @Autowired
    DataTableColumnRepository dataTableColumnRepository;

    @Autowired
    DataTableRepository dataTableRepository;

    @Autowired
    DatasetRepository datasetRepository;

    @Autowired
    OfficerRepository officerRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<DataTableColumnDto> getAllColumnDtos(String pf, String dataTableId) {
        List<DataTableColumn> dataTableColumnList = dataTableColumnRepository.findByDataTableId(Long.parseLong(dataTableId));
        List<DataTableColumn> filteredDataTableColumnList = dataTableColumnList.stream()
                                                            .filter(d -> ValidateOfficerDataTableColumnAccess(pf, d.getId()))
                                                            .collect(Collectors.toList());

        return filteredDataTableColumnList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DataTableColumnDto> getAllPublicColumnDtos(String dataTableId) {
        List<DataTableColumn> dataTableColumnList = dataTableColumnRepository.findByDataTableId(Long.parseLong(dataTableId));
        List<DataTableColumn> filteredDataTableColumnList = dataTableColumnList.stream()
                .filter(d -> d.getIsPublic()==true)
                .collect(Collectors.toList());

        return filteredDataTableColumnList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
       }

    public DataTableColumnDto convertToDto(DataTableColumn dataTableColumn) {
        return modelMapper.map(dataTableColumn, DataTableColumnDto.class);
    }

    public boolean ValidateOfficerDataTableColumnAccess(String pf, long dataTableColumnId) {
        if(officerRepository.findByPf(pf).isPresent()) {
            Optional<DataTableColumn> dataTableColumn = dataTableColumnRepository.findById(dataTableColumnId);
            if(dataTableColumn.isPresent()) {
                List<DataTableColumnAccess> dataTableColumnAccessList = dataTableColumn.get().getDataTableColumnAccessList();
                return officerHasAccessForDataTableColumn(pf, dataTableColumnAccessList);
            } else {
                throw new DataTableColumnNotFoundException(dataTableColumnId);
            }
        } else {
            throw new OfficerNotFoundException(pf);
        }
    }

    private boolean officerHasAccessForDataTableColumn(String pf, List<DataTableColumnAccess> dataTableColumnAccessList) {
        for (DataTableColumnAccess dtca:dataTableColumnAccessList) {
            // DataTableColumnAccess check
            // check if value is officer("Pf")
            if (dtca.getTypeInString().equals("Pf") & dtca.getValue().equals(pf)) { // if value is 'pf' check pf = pf (this officer's)
                return true;
            }
        }
        return false;
    }

    public boolean addOfficerDataTableColumnAccess(String officerPf, String dataTableColumnId) {
        Optional<DataTableColumn> dataTableColumn = dataTableColumnRepository.findById(Long.parseLong(dataTableColumnId));
        if (dataTableColumn.isPresent()) {
            List<DataTableColumnAccess> dataTableColumnAccessList = dataTableColumn.get().getDataTableColumnAccessList();
            if (!officerHasAccessForDataTableColumn(officerPf, dataTableColumnAccessList)) {
                dataTableColumnAccessList.add(new DataTableColumnAccess(dataTableColumn.get(),"Pf", officerPf));
                dataTableColumnRepository.save(dataTableColumn.get());
            }
            return true;
        } else {
            throw new DataTableColumnNotFoundException(Long.parseLong(dataTableColumnId));
        }
    }

    public boolean removeOfficerDataTableColumnAccess(String officerPf, String dataTableColumnId) {
        Optional<DataTableColumn> dataTableColumn = dataTableColumnRepository.findById(Long.parseLong(dataTableColumnId));
        if(dataTableColumn.isPresent()) {
            List<DataTableColumnAccess> dataTableColumnAccessList = dataTableColumn.get().getDataTableColumnAccessList();
            if (officerHasAccessForDataTableColumn(officerPf, dataTableColumnAccessList)) {
                dataTableColumnAccessList.removeIf(dtca -> dtca.getType() == DataTableColumnAccessTypeEnum.Pf && dtca.getValue().equals(officerPf));
                dataTableColumnRepository.save(dataTableColumn.get());
            }
            return true;
        } else {
            throw new DataTableColumnNotFoundException(Long.parseLong(dataTableColumnId));
        }
    }

    public boolean editDataTableColumnDescription(String description, long dataTableColumnId, String pf) {
        // verify if datatablecolumn exists
        Optional<DataTableColumn> dataTableColumn = dataTableColumnRepository.findById(dataTableColumnId);
        if (dataTableColumn.isEmpty()) {
            throw new DataTableColumnNotFoundException(dataTableColumnId);
        }

        // verify officer exists
        Optional<Officer> officer = officerRepository.findByPf(pf);
        if (officer.isEmpty()) {
            throw new OfficerNotFoundException(pf);
        }

        DataTableColumn actualDataTableColumn = dataTableColumn.get();
        Dataset dataset = actualDataTableColumn.getDataTable().getDataset(); // get parent dataset

        // verify if officer is custodian/owner
        if (!dataset.getOfficer().getPf().equals(officer.get().getPf()) && // check ownership
                (dataset.getOfficerCustodianList().stream().filter(custodianOfficer -> custodianOfficer.getPf().equals(officer.get().getPf())).count()==0)) { // check custodianship
            throw new DatasetAccessNotFoundException(pf, dataset.getId());
        }

        actualDataTableColumn.setDescription(description);
        dataTableColumnRepository.save(actualDataTableColumn);
        return true;
    }
    public boolean editDataTableColumnPrivacy(List<String> dataTableColumnPrivacyList, String pf) {
        System.out.println(pf);
        System.out.println(dataTableColumnPrivacyList);
        for(var i=0 ; i<dataTableColumnPrivacyList.toArray().length;i++){
            if(i%2==0){
                //even (if even it is taking out the id of the datatable column)
                var dataTableColumnId=Long.parseLong(dataTableColumnPrivacyList.get(i));
                // verify if datatablecolumn exists
            Optional<DataTableColumn> dataTableColumn = dataTableColumnRepository.findById(dataTableColumnId);

            if (dataTableColumn.isEmpty()) {
                throw new DataTableColumnNotFoundException(dataTableColumnId);
            }

            // verify officer exists
            Optional<Officer> officer = officerRepository.findByPf(pf);
            if (officer.isEmpty()) {
                throw new OfficerNotFoundException(pf);
            }

            DataTableColumn actualDataTableColumn = dataTableColumn.get();

            DataTable dataTable=actualDataTableColumn.getDataTable();

                Dataset dataset = actualDataTableColumn.getDataTable().getDataset(); // get parent dataset

            // verify if officer is custodian/owner
            if (!dataset.getOfficer().getPf().equals(officer.get().getPf()) && // check ownership
                    (dataset.getOfficerCustodianList().stream().filter(custodianOfficer -> custodianOfficer.getPf().equals(officer.get().getPf())).count()==0)) { // check custodianship
                throw new DatasetAccessNotFoundException(pf, dataset.getId());
            }
            var value=Boolean.parseBoolean(dataTableColumnPrivacyList.get(i+1));
            if(value==true){
//                if datatable column set to public, dataset, datatable and datatablecoloumn will be public
                dataset.setIsPublic(value);
                datasetRepository.save(dataset);

                  dataTable.setIsPublic(value);
                  dataTableRepository.save(dataTable);

                actualDataTableColumn.setIsPublic(value);
                dataTableColumnRepository.save(actualDataTableColumn);
            }
            else{
//                if datatable column set to private,only datatable column set to private.
                actualDataTableColumn.setIsPublic(value);
                dataTableColumnRepository.save(actualDataTableColumn);
            }


        }


        }
//        var jsonObject=dataTableColumnPrivacyList.get(0);

//        System.out.println(dataTableColumnPrivacyList[0].id);
            // verify if datatablecolumn exists
//            Optional<DataTableColumn> dataTableColumn = dataTableColumnRepository.findById(dataTableColumnId);
//            if (dataTableColumn.isEmpty()) {
//                throw new DataTableColumnNotFoundException(dataTableColumnId);
//            }
//
//            // verify officer exists
//            Optional<Officer> officer = officerRepository.findByPf(pf);
//            if (officer.isEmpty()) {
//                throw new OfficerNotFoundException(pf);
//            }
//
//            DataTableColumn actualDataTableColumn = dataTableColumn.get();
//            Dataset dataset = actualDataTableColumn.getDataTable().getDataset(); // get parent dataset
//
//            // verify if officer is custodian/owner
//            if (!dataset.getOfficer().getPf().equals(officer.get().getPf()) && // check ownership
//                    (dataset.getOfficerCustodianList().stream().filter(custodianOfficer -> custodianOfficer.getPf().equals(officer.get().getPf())).count()==0)) { // check custodianship
//                throw new DatasetAccessNotFoundException(pf, dataset.getId());
//            }

//        for(int i=0;i<dataTableColumnPrivacyList.length();i++)
//        {
//            System.out.println("hii");
//            System.out.println(dataTableColumnPrivacyList);
//            JSONObject jsonObject1 = dataTableColumnPrivacyList.getJSONObject(i);
//            String value = dataTableColumnPrivacyList.optString(Long.toString(dataTableColumnId));
//            actualDataTableColumn.setIsPublic(Boolean.parseBoolean(value));
//            dataTableColumnRepository.save(actualDataTableColumn);
//        }


//            if(type=="Public") {
//                actualDataTableColumn.setIsPublic(true);
//                dataTableColumnRepository.save(actualDataTableColumn);
//            }
//            else{
//                actualDataTableColumn.setIsPublic(false);
//                dataTableColumnRepository.save(actualDataTableColumn);
//            }



        return true;
    }


}
