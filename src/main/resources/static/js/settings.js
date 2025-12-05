// 설정 관리

// 분석 설정 초기화
document.addEventListener('DOMContentLoaded', function() {
    const analyticsEnabled = document.getElementById('analyticsEnabled');
    const analyticsRangeItem = document.getElementById('analyticsRangeItem');
    const nextAnalyticsDateItem = document.getElementById('nextAnalyticsDateItem');
    const analyticsRange = document.getElementById('analyticsRange');

    function updateAnalyticsUI() {
        const isEnabled = analyticsEnabled.checked;
        analyticsRangeItem.style.opacity = isEnabled ? '1' : '0.5';
        nextAnalyticsDateItem.style.opacity = isEnabled ? '1' : '0.5';
        analyticsRange.disabled = !isEnabled;
    }

    analyticsEnabled.addEventListener('change', updateAnalyticsUI);
    updateAnalyticsUI();
});

async function saveAnalyticsSettings() {
    const analyticsEnabled = document.getElementById('analyticsEnabled').checked;
    const analyticsRange = document.getElementById('analyticsRange').value;

    try {
        const response = await fetch('/web/analytics-setting', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                enabled: analyticsEnabled,
                range: analyticsRange
            })
        });

        if (!response.ok) {
            throw new Error('설정 저장에 실패했습니다');
        }

        showToast('✓ 설정이 저장되었습니다');
        setTimeout(() => {
            window.location.reload();
        }, 1000);

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ ' + error.message);
    }
}

// 계정 탈퇴 모달 표시
function showDeleteAccountModal() {
    document.getElementById('deleteAccountModal').style.display = 'flex';
}

// 계정 탈퇴 모달 닫기
function closeDeleteAccountModal() {
    document.getElementById('deleteAccountModal').style.display = 'none';
}

// 계정 탈퇴
async function deleteAccount() {
    try {
        const response = await fetch('/web/user', {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error('계정 탈퇴에 실패했습니다');
        }

        // 탈퇴 성공
        showToast('✓ 계정이 탈퇴되었습니다');

        // 모달 닫기
        closeDeleteAccountModal();

        // 1초 후 로그아웃 및 홈으로 이동
        setTimeout(() => {
            window.location.href = '/logout';
        }, 1000);

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ ' + error.message);
    }
}

// 모달 외부 클릭시 닫기
window.addEventListener('click', function(event) {
    const modal = document.getElementById('deleteAccountModal');
    if (event.target === modal) {
        closeDeleteAccountModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeDeleteAccountModal();
    }
});
