// 호출 제한 유틸리티

// PaymentType에 따라 적절한 callLimit 선택
function getCallLimitForUser(api, paymentType) {
    // ENTERPRISE나 EXTRA는 무제한
    if (paymentType === 'ENTERPRISE_MONTH' || paymentType === 'ENTERPRISE_YEAR' || paymentType === 'EXTRA') {
        return '무제한';
    }

    // BASIC
    if (paymentType === 'BASIC_MONTH' || paymentType === 'BASIC_YEAR') {
        return api.callLimitBasic;
    }

    // PRO
    if (paymentType === 'PRO_MONTH' || paymentType === 'PRO_YEAR') {
        return api.callLimitPro;
    }

    // FREE (기본값)
    return api.callLimitFree;
}

// 숫자를 천 단위 쉼표 포맷으로 변환
function formatNumberWithComma(value) {
    if (value === '무제한' || value === '-1') {
        return '무제한';
    }

    const num = parseInt(value);
    if (isNaN(num) || num === -1) {
        return '무제한';
    }

    return num.toLocaleString('ko-KR');
}

// 호출 제한 텍스트 생성 (예: "1,000회/일")
function formatCallLimitText(api, paymentType) {
    const limit = getCallLimitForUser(api, paymentType);
    const formatted = formatNumberWithComma(limit);

    if (formatted === '무제한') {
        return '무제한';
    }

    return `${formatted}회/일`;
}
