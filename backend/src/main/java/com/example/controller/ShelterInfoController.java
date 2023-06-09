package com.example.controller;

import com.example.model.*;
import com.example.repository.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class ShelterInfoController {

    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private ShelterRepository shelterRepository;
    @Autowired
    private DonateRecordRepository donateRecordRepository;

    @GetMapping("/getShelterInfo/{shelterId}")
    public Map<String, Object> getShelterInfo(@PathVariable Long shelterId) {

        Map<String, Object> shelterInfo = new HashMap<String, Object>();

        Optional<Shelter> shelterOp = shelterRepository.findById(shelterId);

        // if shelter not exist, return empty map (shelterInfo)
        if (shelterOp.isPresent()) {

            Shelter shelter = shelterOp.get();

            shelterInfo = Map.of(
                    "id", shelter.getId(),
                    "name", shelter.getName(),
                    "address", shelter.getAddress(),
                    "contact_phone", shelter.getContactPhone(),
                    "website", shelter.getWebsite());

        }

        // const shelter = { id: 1, name: 'name1', address: 'myAddress', contact_phone:
        // '0987654321', website: 'https://www.tcapo.gov.taipei/Content_List.aspx?n=8A474D4AA59E06B7' }

        return shelterInfo;

    }

    @GetMapping("/getShelterAnimal/{shelterId}")
    public List<Map<String, Object>> getShelterAnimal(@PathVariable Long shelterId) {

        List<Map<String, Object>> shelterAnimal = new ArrayList<Map<String, Object>>();

        // if member/animalIds not exist, return empty list (adopterAnimal)
        List<Animal> animals = Optional
                .ofNullable(animalRepository.findAnimalsByShelterId(shelterId)).orElse(new ArrayList<Animal>());
        for (Animal animal : animals) {

            Long animalId = animal.getId();
            Integer currentAdopterNum = Optional
                    .ofNullable(donateRecordRepository.countCurrentAdopterNumByAnimalId(animalId))
                    .orElse(0);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime shelteredDate = animal.getShelteredDate();
            String formattedShelteredDate = shelteredDate.format(formatter);

            shelterAnimal.add(Map.of(
                    "id", animalId,
                    "name", animal.getName(),
                    "sex", animal.getSex(),
                    "type", animal.getType(),
                    "birth_year", animal.getBirthYear(),
                    "sheltered_date", shelteredDate,
                    "formatted_sheltered_date", formattedShelteredDate,
                    "shelter", animal.getShelter().getName(),
                    "area", animal.getShelter().getArea(),
                    "numMember", currentAdopterNum));

        }
        // const animals = [{ id: 1, name: 'name1', type: '狗', sex: '公', birth_year:
        // '2020', area: '北部', shelter: '臺北市動物之家', sheltered_date: '2020-04-06
        // 00:00:00.000000', numMember: '1' },];

        return shelterAnimal;

    }

}
