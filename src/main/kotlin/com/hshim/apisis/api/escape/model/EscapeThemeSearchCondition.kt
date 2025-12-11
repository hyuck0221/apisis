package com.hshim.apisis.api.escape.model

import com.hshim.apisis.common.annotation.FieldDescription

class EscapeThemeSearchCondition(
    @FieldDescription("검색어")
    val search: String?,

    @FieldDescription("영업중만 표시 여부")
    val onlyOpen: Boolean?,

    @FieldDescription("지역 (예: 서울)")
    val areas: List<String>?,

    @FieldDescription("구역 (예: 홍대)")
    val locations: List<String>?,

    @FieldDescription("플레이타임 검색시작시간")
    val startPlaytime: Int?,

    @FieldDescription("플레이타임 검색종료시간")
    val endPlaytime: Int?,

    @FieldDescription("가격 검색시작시간")
    val startPrice: Int?,

    @FieldDescription("가격 검색종료시간")
    val endPrice: Int?,

    @FieldDescription("난이도 점수 검색시작시간 (0~5)")
    val startDifficulty: Double?,

    @FieldDescription("난이도 점수 검색종료시간 (0~5)")
    val endDifficulty: Double?,

    @FieldDescription("공포도 점수 검색시작시간 (0~5)")
    val startFear: Double?,

    @FieldDescription("공포도 점수 검색종료시간 (0~5)")
    val endFear: Double?,

    @FieldDescription("활동성 점수 검색시작시간 (0~5)")
    val startActivity: Double?,

    @FieldDescription("활동성 점수 검색종료시간 (0~5)")
    val endActivity: Double?,

    @FieldDescription("만족도 점수 검색시작시간 (0~5)")
    val startSatisfy: Double?,

    @FieldDescription("만족도 점수 검색종료시간 (0~5)")
    val endSatisfy: Double?,

    @FieldDescription("문제수준 점수 검색시작시간 (0~5)")
    val startProblem: Double?,

    @FieldDescription("문제수준 점수 검색종료시간 (0~5)")
    val endProblem: Double?,

    @FieldDescription("스토리 점수 검색시작시간 (0~5)")
    val startStory: Double?,

    @FieldDescription("스토리 점수 검색종료시간 (0~5)")
    val endStory: Double?,

    @FieldDescription("인테리 점수 검색시작시간 (0~5)")
    val startInterior: Double?,

    @FieldDescription("인테리어 점수 검색종료시간 (0~5)")
    val endInterior: Double?,

    @FieldDescription("작품성 점수 검색시작시간 (0~5)")
    val startAct: Double?,

    @FieldDescription("작품성 점수 검색종료시간 (0~5)")
    val endAct: Double?,
)