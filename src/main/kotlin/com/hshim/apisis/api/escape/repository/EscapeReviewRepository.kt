package com.hshim.apisis.api.escape.repository

import com.hshim.apisis.api.escape.entity.EscapeReview
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EscapeReviewRepository : JpaRepository<EscapeReview, String> {
    fun findTopBy(): EscapeReview?
}