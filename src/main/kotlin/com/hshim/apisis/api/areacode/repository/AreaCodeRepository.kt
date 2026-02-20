package com.hshim.apisis.api.areacode.repository

import com.hshim.apisis.api.areacode.entity.AreaCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AreaCodeRepository : JpaRepository<AreaCode, String> {

    @Query(
        """
            select ac from AreaCode ac
            where ac.code like concat('%', :code, '%') 
            and ac.address like concat('%', :address, '%')
            and ac.city like concat('%', :city, '%')
            and ac.town like concat('%', :town, '%')
            and ac.village like concat('%', :village, '%')
        """
    )
    fun findAllBySearch(
        code: String?,
        address: String?,
        city: String?,
        town: String?,
        village: String?,
        pageable: Pageable
    ): Page<AreaCode>

    fun findTopBy(): AreaCode?
}