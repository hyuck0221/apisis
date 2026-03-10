package com.hshim.apisis.api.lotto.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.hshim.apisis.api.lotto.entity.Lotto
import util.DateUtil.stringToDate

data class LottoOpenAPIResponse(
    val resultCode: String?,
    val resultMessage: String?,
    val data: Data,
) {
    data class Data(
        val list: List<Detail>
    )

    data class Detail(
        val winType0: Int,
        val winType1: Int,
        val winType2: Int,
        val winType3: Int,
        @get:JsonProperty("gmSqNo")
        val gameSeqNo: Int,
        @get:JsonProperty("ltEpsd")
        val times: Int,
        @get:JsonProperty("tm1WnNo")
        val number1: Int,
        @get:JsonProperty("tm2WnNo")
        val number2: Int,
        @get:JsonProperty("tm3WnNo")
        val number3: Int,
        @get:JsonProperty("tm4WnNo")
        val number4: Int,
        @get:JsonProperty("tm5WnNo")
        val number5: Int,
        @get:JsonProperty("tm6WnNo")
        val number6: Int,
        @get:JsonProperty("bnsWnNo")
        val bonusNumber: Int,
        @get:JsonProperty("ltRflYmd")
        val openDate: String,
        @get:JsonProperty("rnk1WnNope")
        val winCnt: Int,
        @get:JsonProperty("rnk1WnAmt")
        val firstWinnerPrizePerPerson: Long,
        @get:JsonProperty("rnk1SumWnAmt")
        val firstWinnerPrize: Long,
        @get:JsonProperty("rnk2WnNope")
        val rank2WinCnt: Int,
        @get:JsonProperty("rnk2WnAmt")
        val rank2PrizePerPerson: Long,
        @get:JsonProperty("rnk2SumWnAmt")
        val rank2TotalPrize: Long,
        @get:JsonProperty("rnk3WnNope")
        val rank3WinCnt: Int,
        @get:JsonProperty("rnk3WnAmt")
        val rank3PrizePerPerson: Long,
        @get:JsonProperty("rnk3SumWnAmt")
        val rank3TotalPrize: Long,
        @get:JsonProperty("rnk4WnNope")
        val rank4WinCnt: Int,
        @get:JsonProperty("rnk4WnAmt")
        val rank4PrizePerPerson: Long,
        @get:JsonProperty("rnk4SumWnAmt")
        val rank4TotalPrize: Long,
        @get:JsonProperty("rnk5WnNope")
        val rank5WinCnt: Int,
        @get:JsonProperty("rnk5WnAmt")
        val rank5PrizePerPerson: Long,
        @get:JsonProperty("rnk5SumWnAmt")
        val rank5TotalPrize: Long,
        @get:JsonProperty("sumWnNope")
        val totalWinCnt: Int,
        @get:JsonProperty("rlvtEpsdSumNtslAmt")
        val totalPrize: Long,
        @get:JsonProperty("wholEpsdSumNtslAmt")
        val wholeTotalPrize: Long,
        @get:JsonProperty("excelRnk")
        val excelRank: String,
    ) {
        val numbers = listOf(number1, number2, number3, number4, number5, number6)

        fun toEntity() = Lotto(
            times = times,
            openDate = openDate.stringToDate("yyyyMMdd"),
            totalPrize = totalPrize,
            firstWinnerPrize = firstWinnerPrize,
            bonusNumber = bonusNumber,
            winCnt = winCnt,
            numbers = numbers,
        )
    }

    fun toEntity(times: Int) = data.list.find { it.times == times }?.toEntity()
}