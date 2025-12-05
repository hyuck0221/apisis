package com.hshim.apisis.web.repository

import com.hshim.apisis.web.entity.Analytics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalyticsRepository : JpaRepository<Analytics, String> {
}
