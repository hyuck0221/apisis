package com.hshim.apisis.api.escape.repository

import com.hshim.apisis.api.escape.entity.EscapeCafe
import com.hshim.apisis.api.escape.entity.EscapeTheme
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EscapeThemeRepository : JpaRepository<EscapeTheme, Long> {

    fun findAllByEscapeCafeIn(escapeCafes: List<EscapeCafe>): List<EscapeTheme>

    @Query(
        """
            select et from EscapeTheme et 
            where (
                et.name like concat('%', :search, '%') 
                or et.escapeCafe.name like concat('%', :search, '%') 
                or et.escapeCafe.address like concat('%', :search, '%') 
                or et.escapeCafe.location like concat('%', :search, '%') 
                or et.escapeCafe.area like concat('%', :search, '%') 
                or et.description like concat('%', :search, '%') 
            )
            and (:onlyOpen = false or et.isOpen = :onlyOpen) 
            and ((:areas) is null or et.escapeCafe.area in :areas)
            and ((:locations) is null or et.escapeCafe.location in :locations)
            and et.playtime >= :startPlaytime and (:endPlaytime is null or et.playtime <= :endPlaytime) 
            and et.price >= :startPrice and (:endPrice is null or et.price <= :endPrice) 
            and et.fear >= :startFear and (:endFear is null or et.fear <= :endFear) 
            and et.activity >= :startActivity and (:endActivity is null or et.activity <= :endActivity) 
            and et.satisfy >= :startSatisfy and (:endSatisfy is null or et.satisfy <= :endSatisfy) 
            and et.problem >= :startProblem and (:endProblem is null or et.problem <= :endProblem) 
            and et.story >= :startStory and (:endStory is null or et.story <= :endStory) 
            and et.interior >= :startInterior and (:endInterior is null or et.interior <= :endInterior) 
            and et.act >= :startAct and (:endAct is null or et.act <= :endAct)
        """
    )
    fun findAllByCondition(
        search: String,
        onlyOpen: Boolean,
        areas: List<String>?,
        locations: List<String>?,
        startPlaytime: Int,
        endPlaytime: Int?,
        startPrice: Int,
        endPrice: Int?,
        startDifficulty: Double,
        endDifficulty: Double?,
        startFear: Double,
        endFear: Double?,
        startActivity: Double,
        endActivity: Double?,
        startSatisfy: Double,
        endSatisfy: Double?,
        startProblem: Double,
        endProblem: Double?,
        startStory: Double,
        endStory: Double?,
        startInterior: Double,
        endInterior: Double?,
        startAct: Double,
        endAct: Double?,
        pageable: Pageable,
    ): Page<EscapeTheme>

    fun findTopByOrderByRefIdDesc(): EscapeTheme?
}