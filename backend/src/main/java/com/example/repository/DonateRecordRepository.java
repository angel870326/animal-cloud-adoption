package com.example.repository;

import com.example.model.DonateRecord;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

@Repository
public interface DonateRecordRepository extends JpaRepository<DonateRecord, Long> {

        Optional<DonateRecord> findById(Long id);

        // IndexController

        // 累積總認養金額
        @Query(nativeQuery = true, value = "SELECT SUM(dp.amount) FROM donate_record dr JOIN donate_plan dp ON dr.donate_plan_id = dp.id WHERE dr.status='認養中' or dr.status='認養結束'")
        Integer sumAmountOfDonateRecords();

        // ShelterInfoController & AnimalListController

        // 當前總認養人數量 by animalId
        @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT member_id) FROM donate_record WHERE animal_id = ? and status = '認養中'")
        Integer countCurrentAdopterNumByAnimalId(@Param("animalId") Long animalId);

        // AnimalInfoController

        // 認養紀錄list without審核失敗 by memberId&animalId
        @Query(nativeQuery = true, value = "SELECT id, status, apply_date, donation_start_date, donation_end_date, member_id, animal_id, donate_plan_id FROM donate_record WHERE animal_id = ?1 and member_id = ?2 and status != '審核失敗' ORDER BY apply_date DESC")
        List<DonateRecord> findDonateRecordsByMemberIdAndAnimalId(@Param("animalId") Long animalId,
                        @Param("memberId") Long memberId);

        // 當前認養人IdList without accountMemberId by animalId
        @Query(nativeQuery = true, value = "SELECT DISTINCT member_id FROM donate_record WHERE animal_id = ?1 and member_id != ?2 and status = '認養中'")
        List<Long> findCurrentAdopterIdsByAnimalId(@Param("animalId") Long animalId,
                        @Param("memberId") Long memberId);

        // AdopterListController

        // 當前總認養動物數量 by memberId
        @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT animal_id) FROM donate_record WHERE member_id = ? and status = '認養中'")
        Integer countCurrentAdoptedAnimalNumByMemberId(@Param("memberId") Long memberId);

        // AdopterListController & AdopterInfoController

        // 當前認養動物IdList by memberId
        @Query(nativeQuery = true, value = "SELECT DISTINCT animal_id FROM donate_record WHERE member_id = ? and status = '認養中'")
        List<Long> findCurrentAdoptedAnimalIdsByMemberId(@Param("memberId") Long memberId);

        // AdopterListController & AdopterInfoController & AccountInfoController

        // 累積總認養動物數量 by memberId
        @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT animal_id) FROM donate_record WHERE member_id = ? and (status = '認養中' or status = '認養結束')")
        Integer countAdoptedAnimalNumByMemberId(@Param("memberId") Long memberId);

        // AdopterInfoController & AccountInfoController

        // 累積認養金額 by memberId
        @Query(nativeQuery = true, value = "SELECT SUM(dp.amount) FROM donate_record dr JOIN donate_plan dp ON dr.donate_plan_id = dp.id WHERE dr.member_id = ? and (dr.status = '認養中' or dr.status = '認養結束')")
        Integer sumAdoptedAmountByMemberId(@Param("memberId") Long memberId);

        // AccountInfoController

        // 累積認養動物IdList by memberId
        @Query(nativeQuery = true, value = "SELECT DISTINCT animal_id FROM donate_record WHERE member_id = ? and status != '審核失敗'")
        List<Long> findAdoptedAnimalIdsByMemberId(@Param("memberId") Long memberId);

        // AccountInfoController & AdoptAnimalController

        // newest動物認養紀錄 without 審核失敗 by animalId&memberId
        @Query(nativeQuery = true, value = "SELECT id, status, apply_date, donation_start_date, donation_end_date, member_id, animal_id, donate_plan_id FROM donate_record as t1 WHERE animal_id = ?1 and member_id = ?2 and status != '審核失敗' ORDER BY apply_date DESC LIMIT 1;")
        DonateRecord findNewestDonateRecordByAnimalIdAndMemberId(@Param("animalId") Long animalId,
                        @Param("memberId") Long memberId);

}
