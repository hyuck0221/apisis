package com.hshim.apisis.license.repository

import com.hshim.apisis.license.entity.License
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LicenseRepository : JpaRepository<License, String> {
}
