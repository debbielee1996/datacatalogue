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
import sg.gov.csit.datacatalogue.dcms.ddcs.Ddcs;
import sg.gov.csit.datacatalogue.dcms.ddcs.DdcsRepository;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

import java.util.ArrayList;
import java.util.List;

@Profile("development")
@AllArgsConstructor
@Component
public class Dataseeder {
    private OfficerRepository officerRepository;
    private DdcsRepository ddcsRepository;
    private DatasetRepository datasetRepository;
    private DatasetAccessRepository datasetAccessRepository;
    private DataTableRepository dataTableRepository;

    List<String> officerIdList = new ArrayList<>();
    List<Integer> ddcsIdList = new ArrayList<>();
    List<Long> aclIdList = new ArrayList<>();
    List<Long> datasetIdList = new ArrayList<>();
    List<Integer> datasetAccessIdList = new ArrayList<>();
    List<Long> dataTableIdList = new ArrayList<>();

    @EventListener
    public void seed(ContextRefreshedEvent event){
        seedDdcs();
        seedOfficer();
        seedDataset();
        seedDatasetAccess();
        seedDataTable();;
    }

    private void seedDdcs(){
        Ddcs ddcs1 = new Ddcs("CSIT","IT","ES","FPS");
        Ddcs ddcs2 = new Ddcs("CSIT","IT","ES","FPS");
        Ddcs ddcs3 = new Ddcs("CSIT","IT","ES","HCS");
        Ddcs ddcs4 = new Ddcs("CSIT","IT","IS","FPS");
        Ddcs ddcs5 = new Ddcs("A","HR","EQ","FIN");
        Ddcs ddcs6 = new Ddcs("K","SEN","RX","TROP");

        ddcsRepository.save(ddcs1);
        ddcsRepository.save(ddcs2);
        ddcsRepository.save(ddcs3);
        ddcsRepository.save(ddcs4);
        ddcsRepository.save(ddcs5);
        ddcsRepository.save(ddcs6);

        ddcsIdList.add(ddcs1.getId());
        ddcsIdList.add(ddcs2.getId());
        ddcsIdList.add(ddcs3.getId());
        ddcsIdList.add(ddcs4.getId());
        ddcsIdList.add(ddcs5.getId());
        ddcsIdList.add(ddcs6.getId());
    }

    private void seedOfficer(){
        Officer officer1 = new Officer("1001","dlsy","dsly@dev.gov.sg","Public");
        Officer officer2 = new Officer("1002","lyf","lyf@dev.gov.sg","Public");
        Officer officer3 = new Officer("1003","slwh","slwh@dev.gov.sg","Public");
        Officer officer4 = new Officer("1004","gjq","gjq@dev.gov.sg","System Admin");
        Officer officer5 = new Officer("1005","fcy","fcy@dev.gov.sg","System Admin");

        // add ddcs
        Ddcs ddcs1 = ddcsRepository.getOne(ddcsIdList.get(0));
        Ddcs ddcs2 = ddcsRepository.getOne(ddcsIdList.get(1));
        Ddcs ddcs3 = ddcsRepository.getOne(ddcsIdList.get(2));
        Ddcs ddcs4 = ddcsRepository.getOne(ddcsIdList.get(3));
        Ddcs ddcs5 = ddcsRepository.getOne(ddcsIdList.get(4));
        Ddcs ddcs6 = ddcsRepository.getOne(ddcsIdList.get(5));
        officer1.addDdcs(ddcs1);
        officer1.addDdcs(ddcs2);
        officer2.addDdcs(ddcs3);
        officer3.addDdcs(ddcs4);
        officer4.addDdcs(ddcs5);
        officer5.addDdcs(ddcs6);

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
        Dataset dataset1 = new Dataset("dataset1", "this is dataset1");
        Dataset dataset2 = new Dataset("dataset2", "this is dataset2");
        Dataset dataset3 = new Dataset("dataset3", "this is dataset3");
        Dataset dataset4 = new Dataset("dataset4", "this is dataset4");
        Dataset dataset5 = new Dataset("dataset5", "this is dataset5");

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
        DatasetAccess datasetAccess3 = new DatasetAccess(dataset2, "Pf", "1003");
        DatasetAccess datasetAccess4 = new DatasetAccess(dataset2, "Pf", "1004");
        DatasetAccess datasetAccess5 = new DatasetAccess(dataset2, "Ddcs", "1");
        DatasetAccess datasetAccess6 = new DatasetAccess(dataset3, "Ddcs", "5");
        DatasetAccess datasetAccess7 = new DatasetAccess(dataset3, "Ddcs", "3");
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
        // get all datasets
        Dataset dataset1 = datasetRepository.getOne(datasetIdList.get(0));
        Dataset dataset2 = datasetRepository.getOne(datasetIdList.get(1));
        Dataset dataset3 = datasetRepository.getOne(datasetIdList.get(2));
        Dataset dataset4 = datasetRepository.getOne(datasetIdList.get(3));
        Dataset dataset5 = datasetRepository.getOne(datasetIdList.get(4));

        DataTable dataTable1 = new DataTable("datatable1", "Furniture", dataset1);
        DataTable dataTable2 = new DataTable("datatable2", "Outings", dataset2);
        DataTable dataTable3 = new DataTable("datatable3", "Salary", dataset3);
        DataTable dataTable4 = new DataTable("datatable4", "Expenses", dataset4);
        DataTable dataTable5 = new DataTable("datatable5", "Operations", dataset5);
        DataTable dataTable6 = new DataTable("datatable6", "Transport", dataset1);

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
}
