package com.hshim.apisis.api.lotto.repository

import com.hshim.apisis.api.lotto.entity.Lotto
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LottoRepository : JpaRepository<Lotto, Int> {
    fun findTopByOrderByTimesAsc(): Lotto?
    fun findTopByOrderByTimesDesc(): Lotto?
    fun findAllByTimesBefore(
        times: Int,
        pageable: Pageable
    ): List<Lotto>
}