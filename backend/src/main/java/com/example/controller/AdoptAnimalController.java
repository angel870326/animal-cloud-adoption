package com.example.controller;

import com.example.model.*;
import com.example.repository.*;
import com.example.component.*;
import com.example.form.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class AdoptAnimalController {

        @Autowired
        private MemberRepository memberRepository;
        @Autowired
        private AnimalRepository animalRepository;
        @Autowired
        private DonatePlanRepository donatePlanRepository;
        @Autowired
        private DonateRecordRepository donateRecordRepository;

        @GetMapping("/getJoiningDonatePlan/{animalId}/{memberId}")
        public Map<String, Object> getJoiningDonatePlan(@PathVariable("animalId") Long animalId,
                        @PathVariable Long memberId) {

                Map<String, Object> joiningDonatePlan = new HashMap<String, Object>();
                Map<String, Object> animalData = new HashMap<String, Object>();
                Map<String, Object> userData = new HashMap<String, Object>();
                List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();

                // if animal/member not exist, return empty map in extendingDonatePlan
                // (animalData, userData, options)
                Optional<Animal> animalOp = animalRepository.findById(animalId);
                Optional<Member> memberOp = memberRepository.findById(memberId);
                List<DonatePlan> donatePlans = donatePlanRepository.findAll();
                if (animalOp.isPresent() && memberOp.isPresent()) {

                        Animal animal = animalOp.get();
                        Member member = memberOp.get();

                        userData = Map.of(
                                        "id", member.getId(),
                                        "name", member.getNickName(),
                                        "email", member.getEmail(),
                                        "anonymous", member.getAnonymous());
                        animalData = Map.of(
                                        "id", animal.getId(),
                                        "name", animal.getName());

                        for (DonatePlan donatePlan : donatePlans) {
                                options.add(Map.of(
                                                "id", donatePlan.getId(),
                                                "label", donatePlan.getName(),
                                                "price", donatePlan.getAmount(),
                                                "duration", donatePlan.getDuration()));
                        }

                }

                joiningDonatePlan = Map.of(
                                "animal", animalData,
                                "user", userData,
                                "options", options);

                // const options = [{ id: 1, label: '方案一', price: 100, duration: 1 },];
                // const user = { id: 33, email: 'b08705037@ntu.edu.tw', anonymous: true };
                // const animal = { id: 22, name: '小黑' };

                return joiningDonatePlan;

        }

        @GetMapping("/getExtendingDonatePlan/{animalId}/{memberId}")
        public Map<String, Object> getExtendingDonatePlan(@PathVariable("animalId") Long animalId,
                        @PathVariable Long memberId) {

                Map<String, Object> extendingDonatePlan = new HashMap<String, Object>();
                Map<String, Object> animalData = new HashMap<String, Object>();
                Map<String, Object> userData = new HashMap<String, Object>();
                List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
                Map<String, Object> record = new HashMap<String, Object>();

                // if animal/member not exist, return empty map in extendingDonatePlan
                // (animalData, userData, options)
                Optional<Animal> animalOp = animalRepository.findById(animalId);
                Optional<Member> memberOp = memberRepository.findById(memberId);
                List<DonatePlan> donatePlans = donatePlanRepository.findAll();
                DonateRecord newestDonateRecord = donateRecordRepository
                                .findNewestDonateRecordByAnimalIdAndMemberId(animalId, memberId);
                if (animalOp.isPresent() && memberOp.isPresent()) {

                        Animal animal = animalOp.get();
                        Member member = memberOp.get();

                        userData = Map.of(
                                        "id", member.getId(),
                                        "name", member.getNickName(),
                                        "email", member.getEmail(),
                                        "anonymous", member.getAnonymous());
                        animalData = Map.of(
                                        "id", animal.getId(),
                                        "name", animal.getName());

                        for (DonatePlan donatePlan : donatePlans) {
                                options.add(Map.of(
                                                "id", donatePlan.getId(),
                                                "label", donatePlan.getName(),
                                                "price", donatePlan.getAmount(),
                                                "duration", donatePlan.getDuration()));
                        }

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        LocalDateTime oldEndDate = newestDonateRecord.getDonationEndDate();
                        String formattedOldEndDate = "";
                        LocalDateTime newStartDate = newestDonateRecord.getDonationEndDate();
                        String formattedNewStartDate = "";
                        try {
                                formattedOldEndDate = oldEndDate.format(formatter);
                                newStartDate = oldEndDate.plusDays(1);
                                formattedNewStartDate = newStartDate.format(formatter);
                        } catch (NullPointerException e) {
                        }
                        record = Map.of(
                                        "oldEndDate", formattedOldEndDate,
                                        "newStartDate", formattedNewStartDate);

                }

                extendingDonatePlan = Map.of(
                                "animal", animalData,
                                "user", userData,
                                "options", options,
                                "record", record);

                // const options = [{ id: 1, label: '方案一', price: 100, duration: 1 },];
                // const user = { id: 33, email: 'b08705037@ntu.edu.tw', anonymous: true };
                // const animal = { id: 22, name: '小黑' };
                // const record = { oldEndDate: '2023/5/10', newStartDate: '2023/5/11' };

                return extendingDonatePlan;

        }

        @PostMapping("/addDonation/{memberId}")
        public ResponseEntity<Void> addDonation(@PathVariable("memberId") Long memberId,
                        @RequestBody AddDonationForm request) {

                String donatePlanName = request.getOption();
                Long animalId = request.getAnimalId();

                Optional<Animal> animalOp = animalRepository.findById(animalId);
                Optional<Member> memberOp = memberRepository.findById(memberId);
                Optional<DonatePlan> donatePlanOp = donatePlanRepository.findDonatePlanByName(donatePlanName);
                if (animalOp.isPresent() && memberOp.isPresent() && donatePlanOp.isPresent()) {

                        Animal animal = animalOp.get();
                        Member member = memberOp.get();
                        DonatePlan donatePlan = donatePlanOp.get();

                        DonateRecord donateRecord = new DonateRecord("審核中", LocalDateTime.now(), null, null, member,
                                        animal,
                                        donatePlan);
                        donateRecord.setId(Long.valueOf(donateRecordRepository.findAll().size() + 1));
                        donateRecordRepository.save(donateRecord);

                        // 寄信
                        String memberMail = member.getEmail();
                        String memberName = member.getNickName();
                        String animalName = animal.getName();
                        Integer donatePlanAmount = donatePlan.getAmount();
                        SendMail.sendMail(memberMail, memberName, animalName, donatePlanName, donatePlanAmount);

                        return ResponseEntity.ok().build();

                } else {
                        return ResponseEntity.notFound().build();
                }

        }

}
