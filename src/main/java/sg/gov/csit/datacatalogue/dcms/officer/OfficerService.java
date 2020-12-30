package sg.gov.csit.datacatalogue.dcms.officer;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OfficerService {
    @Autowired
    OfficerRepository officerRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<OfficerDto> getAllOfficers() {
        List<Officer> officers = officerRepository.findAll();
        return officers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OfficerDto convertToDto(Officer officer) {
        return modelMapper.map(officer, OfficerDto.class);
    }
}
