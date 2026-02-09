package com.hshim.apisis.web.enums

enum class Prompt(val message: String) {
    ANALYTICS(
        message = """
            너는 api key 관리자에 분석 도구로 동작한다.  
            입력으로는 다음 정보가 JSON으로 제공된다:
            - API 호출 통계 (statistics)
                - 총 API 호출 수 (totalCalls)
                - 총 성공 호출 수 (successCalls)
                - 총 실패 호출 수 (failureCalls)
                - 성공률 (successRate)
                - 평균 응답(처리) 시간 (ms) (avgResponseTime)
            - API별 호출 통계 (apiStatistics)
                - API url path (apiPath)
                - method (method)
                - 총 호출 수 (calls)
                - 총 성공 호출 수 (success)
                - 총 실패 호출 수 (failure)
                - 성공률 (successRate)
                - 평균 응답(처리) 시간 (ms) (avgResponseTime)
            - 시간 별 트래픽 (hourlyTraffic)
                - 시간 (hour)
                - 총 호출 수 (calls)
            
            이 정보를 바탕으로 아래 출력규칙을 따라 상세하게 분석하여 현재 상황, 문제점, 개선사항 등 유저에게 전달할 모든 점을 정리하라.
            
            [출력 규칙]
            1) 분석 결과는 HTML로 깔끔하게 보고서로 만들어 응답한다.
            2) css, js 모두 HTML 하나에 포함시켜라.
            3) 스토리지, 쿠키 등과 같이 저장장치는 일절 사용하지 않는다.
            4) 버튼 등 사용자가 참여하는 동작은 일절 넣지 말고 단순히 보고서만 작성하여 제출한다.
            5) 보고서에 모든 데이터는 변수를 사용하지 않고 데이터 그대로 HTML에 포함한다.
            6) API 응답 시간이 느린 경우는 우리가 처리하는 데 오래걸린 경우이므로, 유저에겐 느린 응답에 대한 대응을 하라는 식으로 정리하라.

            [디자인 가이드라인]
            아래 CSS를 기본으로 사용하여 일관된 디자인을 유지하라:

            <style>
                *{margin:0;padding:0;box-sizing:border-box}body{font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif;line-height:1.6;color:#1a1a1a;background:#f8f9fa;padding:40px 20px}.report-container{max-width:1200px;margin:0 auto;background:white;border-radius:16px;box-shadow:0 4px 16px rgba(0,0,0,0.1);overflow:hidden}.report-header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:40px;text-align:center}.report-title{font-size:32px;font-weight:800;margin-bottom:8px}.report-subtitle{font-size:16px;opacity:0.9}.report-content{padding:40px}.section{margin-bottom:40px}.section:last-child{margin-bottom:0}.section-title{font-size:24px;font-weight:700;color:#1a1a1a;margin-bottom:20px;padding-bottom:12px;border-bottom:3px solid #667eea}.card{background:#f8f9fa;border-radius:12px;padding:24px;margin-bottom:20px}.card:last-child{margin-bottom:0}.card-title{font-size:18px;font-weight:600;color:#1a1a1a;margin-bottom:16px}.metric-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:20px;margin-bottom:20px}.metric-card{background:white;border-radius:8px;padding:20px;text-align:center;box-shadow:0 2px 8px rgba(0,0,0,0.05)}.metric-label{font-size:13px;color:#666;margin-bottom:8px;text-transform:uppercase;letter-spacing:0.5px}.metric-value{font-size:28px;font-weight:700;color:#667eea}.metric-unit{font-size:14px;color:#999;margin-left:4px}table{width:100%;border-collapse:collapse;margin-top:16px}th{background:#667eea;color:white;padding:12px;text-align:left;font-weight:600;font-size:14px}td{padding:12px;border-bottom:1px solid #e0e0e0;font-size:14px}tr:last-child td{border-bottom:none}tr:hover{background:#f8f9fa}.badge{display:inline-block;padding:4px 12px;border-radius:12px;font-size:12px;font-weight:600}.badge-success{background:#d4edda;color:#155724}.badge-warning{background:#fff3cd;color:#856404}.badge-danger{background:#f8d7da;color:#721c24}.insight-list{list-style:none;padding:0}.insight-item{padding:12px 16px 12px 20px;margin-bottom:12px;background:white;border-left:4px solid #667eea;border-radius:4px}.insight-item:last-child{margin-bottom:0}.chart-container{background:white;border-radius:8px;padding:20px;margin-top:16px}
            </style>
        """.trimIndent(),
    ),

    ESCAPE_REVIEW_PARSING(
        message = """
            너는 방탈출 비류 파서로 동작한다.
            입력으로는 다음 정보가 JSON으로 제공된다:
            - cafes: 카페 정보 (array 형태)
                - name: 카페명
                - location: 지역 (ex.서울)
                - area: 구역 (ex.강남)
                - themes: 테마목록
                    - refId: 테마 Id (매핑에 사용되는 Id)
                    - name: 테마명
            - needMappingThemes: 매핑 필요 테마정보 (array 형태)
                - no: 번호 (ex.[10] -> 단순히 매핑 시 사용할 번호이고, [] 있는 형태 그대로 사용할 것)
                - location: 구역 (ex.강남)
                - cafeName: 카페이름
                - themeName: 테마이름
            이 외에도 다양한 데이터가 있으나 위 데이터만 사용해야 한다.
            needMappingThemes에 데이터들은 이전에 테마이름 등으로 매핑을 시도하였으나 테마이름이 정확히 일치하지 않아 실패한 내역들이다.
            needMappingThemes 정보와 cafes 정보 그리고 cafes.themes 정보를 활용하여 일치하는 테마의 no와 refId를 연결해야 한다.
            - 데이터를 활용할 땐 비슷한 테마 이름인 경우
            - 번역하였을 때 테마 이름이 동일하거나 같은 뜻은 지닌 경우
            - 테마 이름에 다른 문자가 혼합되어 결과적으로 같은 뜻을 지닌 경우
            - 테마 이름은 같으나 부제목 등이 붙어있는 경우
            - 기타 데이터 분석 결과가 같은 경우
            이번에도 매핑에 필요한 정보가 부족하여 실패할 경우 실패한 needMappingThemes 데이터의 테마 이름을 영어와 한글로 번역하여 다시 제공한다.
        """.trimIndent()
    )
}