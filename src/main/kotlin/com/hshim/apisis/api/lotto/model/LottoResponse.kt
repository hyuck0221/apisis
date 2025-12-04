package com.hshim.apisis.api.lotto.model

import com.hshim.apisis.api.lotto.entity.Lotto
import util.DateUtil.dateToString

class LottoResponse(
    val times: Int,
    val openDate: String,
    val numbers: List<Int>,
    val bonusNumber: Int,
    val totalPrize: Long,
    val firstWinnerPrize: Long,
    val winCnt: Int,
) {
    constructor(lotto: Lotto) : this(
        times = lotto.times,
        openDate = lotto.openDate.dateToString(),
        numbers = lotto.numbers,
        bonusNumber = lotto.bonusNumber,
        totalPrize = lotto.totalPrize,
        firstWinnerPrize = lotto.firstWinnerPrize,
        winCnt = lotto.winCnt,
    )
}