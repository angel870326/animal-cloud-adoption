package com.example.controller;

import com.example.model.*;
import com.example.repository.*;
import com.example.component.*;

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
public class AnimalInfoController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private DonateRecordRepository donateRecordRepository;

    @GetMapping("/getAnimalInfo/{animalId}")
    public Map<String, Object> getAnimalInfo(@PathVariable Long animalId) {

        Map<String, Object> animalInfo = new HashMap<String, Object>();

        // if animal not exist, return empty map (animalInfo)
        Optional<Animal> animalOp = animalRepository.findById(animalId);
        if (animalOp.isPresent()) {

            Animal animal = animalOp.get();

            animalInfo = Map.of(
                    "id", animal.getId(),
                    "name", animal.getName(),
                    "sex", animal.getSex(),
                    "type", animal.getType(),
                    "brithYear", animal.getBirthYear(),
                    "personality", animal.getPersonality(),
                    "appearance", animal.getAppearance(),
                    "shelter_id", animal.getShelter().getId(),
                    "shelter_name", animal.getShelter().getName());

        }

        // const animal = { id: 22, name: '小黑', sex: "母", type: "巴哥犬", brithYear: 2020,
        // personality: "活潑", appearance: "黑色、垂耳", shelter_id: 1};

        return animalInfo;

    }

    @GetMapping("/getAnimalDonateRecord/{animalId}/{memberId}")
    public List<Map<String, Object>> getAnimalDonateRecord(@PathVariable Long animalId,
            @PathVariable Long memberId) {

        List<Map<String, Object>> animalDonateRecord = new ArrayList<Map<String, Object>>();

        // if donateRecords not exist, return empty list (animalDonateRecord)
        List<DonateRecord> donateRecords = Optional
                .ofNullable(donateRecordRepository.findDonateRecordsByMemberIdAndAnimalId(animalId, memberId))
                .orElse(new ArrayList<DonateRecord>());
        for (DonateRecord donateRecord : donateRecords) {

            UpdateDonateRecordStatus.updateStatus(donateRecord);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime startDate = donateRecord.getDonationStartDate();
            String formattedStartDate = "";
            try {
                formattedStartDate = startDate.format(formatter);
            } catch (NullPointerException e) {
            }
            LocalDateTime endDate = donateRecord.getDonationEndDate();
            String formattedEndDate = "";
            try {
                formattedEndDate = endDate.format(formatter);
            } catch (NullPointerException e) {
            }

            DonatePlan donatePlan = donateRecord.getDonatePlan();

            animalDonateRecord.add(Map.of(
                    "id", donateRecord.getId(),
                    "status", donateRecord.getStatus(),
                    "startDate", formattedStartDate,
                    "endDate", formattedEndDate,
                    "plan", donatePlan.getName(),
                    "amount", donatePlan.getAmount()));

        }

        // const adoptionRecords = [{ id: 1, plan: "方案一", startDate: '2023/5/1',
        // endDate:"2023/6/1", status:"認養中", amount: 100 },];

        return animalDonateRecord;

    }

    @GetMapping("/getAnimalAdopter/{animalId}/{memberId}")
    public List<Map<String, Object>> getAnimalAdopter(@PathVariable Long animalId, @PathVariable Long memberId) {

        List<Map<String, Object>> animalAdopter = new ArrayList<Map<String, Object>>();

        // if donateRecords not exist, return empty list (animalAdopter)
        List<Long> currentAdoptingMemberIds = Optional
                .ofNullable(donateRecordRepository.findCurrentAdopterIdsByAnimalId(animalId,
                        memberId))
                .orElse(new ArrayList<Long>());
        for (Long currentAdoptingMemberId : currentAdoptingMemberIds) {

            Optional<Member> memberOp = memberRepository.findById(currentAdoptingMemberId);
            Member member = memberOp.get();

            if (member.getAnonymous() == false) {
                animalAdopter.add(Map.of(
                        "id", member.getId(),
                        "name", member.getNickName()));
            }

        }

        // const adopters = [{ id: 1, name: "AA" },];

        return animalAdopter;

    }

}
