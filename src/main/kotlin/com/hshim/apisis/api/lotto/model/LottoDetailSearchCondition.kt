package com.hshim.apisis.api.lotto.model

import com.hshim.apisis.common.annotation.FieldDescription

class LottoDetailSearchCondition (
    @FieldDescription("회차 (기본값: 최근)")
    val times: Int?
)