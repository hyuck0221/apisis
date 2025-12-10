package com.hshim.apisis.web.repository

import com.hshim.apisis.user.enums.PaymentType
import com.hshim.apisis.web.entity.ApiKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ApiKeyRepository : JpaRepository<ApiKey, String> {
    fun findByKeyValue(keyValue: String): ApiKey?
    fun findByKeyValueAndIsActive(keyValue: String, isActive: Boolean): ApiKey?
    fun deleteByKeyValue(keyValue: String)
    fun deleteAllByUserId(userId: String)
    fun findAllByUserId(userId: String): List<ApiKey>

    @Query(
        """
            select l.paymentType from ApiKey ak
            inner join User u on u.id = ak.user.id 
            inner join License l on l.id = u.license.id 
            where ak.keyValue = :keyValue 
            and l.expiredDate >= now()
        """
    )
    fun findPaymentTypeByKeyValue(keyValue: String): PaymentType?
}
