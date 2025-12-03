package com.hshim.apisis.api.escape.repository

import com.hshim.apisis.api.escape.entity.EscapeCafe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EscapeCafeRepository : JpaRepository<EscapeCafe, String> {
    @Query(
        """
            select ec from EscapeCafe ec
            group by ec.location 
            order by count(ec.id) desc 
        """
    )
    fun findAllByGroupByLocation(): List<EscapeCafe>

    fun findByNameAndLocationAndArea(name: String, location: String, area: String): EscapeCafe?
    fun findAllByNameIn(names: List<String>): List<EscapeCafe>

    @Query(
        """
            select ec from EscapeCafe ec
            where (:onlyOpen = false or ec.isOpen = :onlyOpen)
            and (
                ec.name like concat('%', :search, '%')
                or ec.location like concat('%', :search, '%')
                or ec.area like concat('%', :search, '%')
                or ec.address like concat('%', :search, '%')
            )
            and ec.location like concat('%', :search, '%')
            and ec.area like concat('%', :search, '%')
        """
    )
    fun findAllBySearch(
        search: String,
        onlyOpen: Boolean,
        location: String,
        area: String,
        pageable: Pageable,
    ): Page<EscapeCafe>

    @Query(
        """
            select ec from EscapeCafe ec
            where (:onlyOpen = false or ec.isOpen = :onlyOpen)
            and ec.lat between :minLat and :maxLat
            and ec.lng between :minLng and :maxLng
        """
    )
    fun findAllByBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double,
        onlyOpen: Boolean
    ): List<EscapeCafe>
}