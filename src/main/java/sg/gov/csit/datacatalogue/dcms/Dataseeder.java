package sg.gov.csit.datacatalogue.dcms;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetRepository;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccessRepository;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTableRepository;
import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccess;
import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccessRepository;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

import java.util.ArrayList;
import java.util.List;

@Profile("development")
@AllArgsConstructor
@Component
public class Dataseeder {
    private OfficerRepository officerRepository;
    private DatasetRepository datasetRepository;
    private DatasetAccessRepository datasetAccessRepository;
    private DataTableRepository dataTableRepository;
    private DataTableAccessRepository dataTableAccessRepository;

    List<String> officerIdList = new ArrayList<>();
    List<Long> aclIdList = new ArrayList<>();
    List<Long> datasetIdList = new ArrayList<>();
    List<Long> datasetAccessIdList = new ArrayList<>();
    List<Long> dataTableIdList = new ArrayList<>();
    List<Long> dataTableAccessIdList = new ArrayList<>();

    @EventListener
    public void seed(ContextRefreshedEvent event){
        seedOfficer();
        seedDataset();
        seedDatasetAccess();
        seedDataTable();
        seedDataTableAccess();
    }


    private void seedOfficer(){
        Officer officer1 = new Officer("1001","dlsy","dsly@dev.gov.sg", "123","Public");
        Officer officer2 = new Officer("1002","lyf","lyf@dev.gov.sg", "123","Public");
        Officer officer3 = new Officer("1003","slwh","slwh@dev.gov.sg", "123","Public");
        Officer officer4 = new Officer("1004","gjq","gjq@dev.gov.sg", "123","System Admin");
        Officer officer5 = new Officer("1005","fcy","fcy@dev.gov.sg", "123","System Admin");

        officerRepository.save(officer1);
        officerRepository.save(officer2);
        officerRepository.save(officer3);
        officerRepository.save(officer4);
        officerRepository.save(officer5);

        officerIdList.add(officer1.getPf());
        officerIdList.add(officer2.getPf());
        officerIdList.add(officer3.getPf());
        officerIdList.add(officer4.getPf());
        officerIdList.add(officer5.getPf());
    }

    private void seedDataset() {
        Officer officer1 = officerRepository.getOne(officerIdList.get(0));
        Officer officer2 = officerRepository.getOne(officerIdList.get(1));
        Officer officer3 = officerRepository.getOne(officerIdList.get(2));
        Officer officer4 = officerRepository.getOne(officerIdList.get(3));
        Officer officer5 = officerRepository.getOne(officerIdList.get(4));

        Dataset dataset1 = new Dataset("dataset1", "this is dataset1", officer1);
        Dataset dataset2 = new Dataset("dataset2", "this is dataset2", officer2);
        Dataset dataset3 = new Dataset("dataset3", "this is dataset3", officer3);
        Dataset dataset4 = new Dataset("dataset4", "this is dataset4", officer4);
        Dataset dataset5 = new Dataset("dataset5", "this is dataset5", officer5);

        datasetRepository.save(dataset1);
        datasetRepository.save(dataset2);
        datasetRepository.save(dataset3);
        datasetRepository.save(dataset4);
        datasetRepository.save(dataset5);

        datasetIdList.add(dataset1.getId());
        datasetIdList.add(dataset2.getId());
        datasetIdList.add(dataset3.getId());
        datasetIdList.add(dataset4.getId());
        datasetIdList.add(dataset5.getId());
    }

    private void seedDatasetAccess() {
        // get all datasets
        Dataset dataset1 = datasetRepository.getOne(datasetIdList.get(0));
        Dataset dataset2 = datasetRepository.getOne(datasetIdList.get(1));
        Dataset dataset3 = datasetRepository.getOne(datasetIdList.get(2));
        Dataset dataset4 = datasetRepository.getOne(datasetIdList.get(3));
        Dataset dataset5 = datasetRepository.getOne(datasetIdList.get(4));

        DatasetAccess datasetAccess1 = new DatasetAccess(dataset1, "Pf", "1001");
        DatasetAccess datasetAccess2 = new DatasetAccess(dataset1, "Pf", "1002");
        DatasetAccess datasetAccess3 = new DatasetAccess(dataset2, "Pf", "1001");
        DatasetAccess datasetAccess4 = new DatasetAccess(dataset2, "Pf", "1003");
        DatasetAccess datasetAccess5 = new DatasetAccess(dataset2, "Pf", "1004");
        DatasetAccess datasetAccess6 = new DatasetAccess(dataset3, "Pf", "1003");
        DatasetAccess datasetAccess7 = new DatasetAccess(dataset3, "Pf", "1004");
        DatasetAccess datasetAccess8 = new DatasetAccess(dataset3, "Pf", "1005");
        DatasetAccess datasetAccess9 = new DatasetAccess(dataset4, "Pf", "1005");

        datasetAccessRepository.save(datasetAccess1);
        datasetAccessRepository.save(datasetAccess2);
        datasetAccessRepository.save(datasetAccess3);
        datasetAccessRepository.save(datasetAccess4);
        datasetAccessRepository.save(datasetAccess5);
        datasetAccessRepository.save(datasetAccess6);
        datasetAccessRepository.save(datasetAccess7);
        datasetAccessRepository.save(datasetAccess8);
        datasetAccessRepository.save(datasetAccess9);

        datasetAccessIdList.add(datasetAccess1.getId());
        datasetAccessIdList.add(datasetAccess2.getId());
        datasetAccessIdList.add(datasetAccess3.getId());
        datasetAccessIdList.add(datasetAccess4.getId());
        datasetAccessIdList.add(datasetAccess5.getId());
        datasetAccessIdList.add(datasetAccess6.getId());
        datasetAccessIdList.add(datasetAccess7.getId());
        datasetAccessIdList.add(datasetAccess8.getId());
        datasetAccessIdList.add(datasetAccess9.getId());
    }

    private void seedDataTable() {
        // get all officers
        Officer officer1 = officerRepository.getOne(officerIdList.get(0));
        Officer officer2 = officerRepository.getOne(officerIdList.get(1));
        Officer officer3 = officerRepository.getOne(officerIdList.get(2));
        Officer officer4 = officerRepository.getOne(officerIdList.get(3));
        Officer officer5 = officerRepository.getOne(officerIdList.get(4));

        // get all datasets
        Dataset dataset1 = datasetRepository.getOne(datasetIdList.get(0));
        Dataset dataset2 = datasetRepository.getOne(datasetIdList.get(1));
        Dataset dataset3 = datasetRepository.getOne(datasetIdList.get(2));
        Dataset dataset4 = datasetRepository.getOne(datasetIdList.get(3));
        Dataset dataset5 = datasetRepository.getOne(datasetIdList.get(4));

        DataTable dataTable1 = new DataTable("datatable1", "Furniture", dataset1, officer1);
        DataTable dataTable2 = new DataTable("datatable2", "Outings", dataset2, officer2);
        DataTable dataTable3 = new DataTable("datatable3", "Salary", dataset3, officer3);
        DataTable dataTable4 = new DataTable("datatable4", "Expenses", dataset4, officer4);
        DataTable dataTable5 = new DataTable("datatable5", "Operations", dataset5, officer5);
        DataTable dataTable6 = new DataTable("datatable6", "Transport", dataset1, officer1);

        dataTableRepository.save(dataTable1);
        dataTableRepository.save(dataTable2);
        dataTableRepository.save(dataTable3);
        dataTableRepository.save(dataTable4);
        dataTableRepository.save(dataTable5);
        dataTableRepository.save(dataTable6);

        dataTableIdList.add(dataTable1.getId());
        dataTableIdList.add(dataTable2.getId());
        dataTableIdList.add(dataTable3.getId());
        dataTableIdList.add(dataTable4.getId());
        dataTableIdList.add(dataTable5.getId());
        dataTableIdList.add(dataTable6.getId());
    }

    private void seedDataTableAccess() {
        // get all dataTables
        DataTable dataTable1 = dataTableRepository.getOne(dataTableIdList.get(0));
        DataTable dataTable2 = dataTableRepository.getOne(dataTableIdList.get(1));
        DataTable dataTable3 = dataTableRepository.getOne(dataTableIdList.get(2));
        DataTable dataTable4 = dataTableRepository.getOne(dataTableIdList.get(3));
        DataTable dataTable5 = dataTableRepository.getOne(dataTableIdList.get(4));
        DataTable dataTable6 = dataTableRepository.getOne(dataTableIdList.get(5));

        DataTableAccess dataTableAccess1 = new DataTableAccess(dataTable1, "Pf", "1001");
        DataTableAccess dataTableAccess2 = new DataTableAccess(dataTable1, "Pf", "1002");
        DataTableAccess dataTableAccess3 = new DataTableAccess(dataTable2, "Pf", "1001");
        DataTableAccess dataTableAccess4 = new DataTableAccess(dataTable2, "Pf", "1003");
        DataTableAccess dataTableAccess5 = new DataTableAccess(dataTable2, "Pf", "1004");
        DataTableAccess dataTableAccess6 = new DataTableAccess(dataTable3, "Pf", "1003");
        DataTableAccess dataTableAccess7 = new DataTableAccess(dataTable3, "Pf", "1004");
        DataTableAccess dataTableAccess8 = new DataTableAccess(dataTable3, "Pf", "1005");
        DataTableAccess dataTableAccess9 = new DataTableAccess(dataTable4, "Pf", "1005");

        dataTableAccessRepository.save(dataTableAccess1);
        dataTableAccessRepository.save(dataTableAccess2);
        dataTableAccessRepository.save(dataTableAccess3);
        dataTableAccessRepository.save(dataTableAccess4);
        dataTableAccessRepository.save(dataTableAccess5);
        dataTableAccessRepository.save(dataTableAccess6);
        dataTableAccessRepository.save(dataTableAccess7);
        dataTableAccessRepository.save(dataTableAccess8);
        dataTableAccessRepository.save(dataTableAccess9);

        dataTableAccessIdList.add(dataTableAccess1.getId());
        dataTableAccessIdList.add(dataTableAccess2.getId());
        dataTableAccessIdList.add(dataTableAccess3.getId());
        dataTableAccessIdList.add(dataTableAccess4.getId());
        dataTableAccessIdList.add(dataTableAccess5.getId());
        dataTableAccessIdList.add(dataTableAccess6.getId());
        dataTableAccessIdList.add(dataTableAccess7.getId());
        dataTableAccessIdList.add(dataTableAccess8.getId());
        dataTableAccessIdList.add(dataTableAccess9.getId());
    }
}
