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

    analyticsEnabled.addEventListener('change', function() {
        updateAnalyticsUI();
        saveAnalyticsSettings();
    });

    analyticsRange.addEventListener('change', function() {
        saveAnalyticsSettings();
    });

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

async function requestAnalytics() {
    try {
        const response = await fetch('/web/analytics-setting/request', {
            method: 'PUT',
        });

        if (!response.ok) {
            throw new Error('분석 요청에 실패했습니다');
        }

        showToast('✓ 분석 요청이 완료되었습니다');
        setTimeout(() => {
            window.location.reload();
        }, 1000);

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ ' + error.message);
    }
}

// API 키 전체 삭제 모달
function showDeleteAllApiKeysModal() {
    document.getElementById('deleteAllApiKeysModal').style.display = 'flex';
}

function closeDeleteAllApiKeysModal() {
    document.getElementById('deleteAllApiKeysModal').style.display = 'none';
}

async function deleteAllApiKeys() {
    try {
        const response = await fetch('/web/keys', {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error('API 키 전체 삭제에 실패했습니다');
        }

        showToast('✓ 모든 API 키가 삭제되었습니다');
        closeDeleteAllApiKeysModal();

        setTimeout(() => {
            window.location.reload();
        }, 1000);

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ ' + error.message);
    }
}

// 분석 보고서 전체 삭제 모달
function showDeleteAllAnalyticsModal() {
    document.getElementById('deleteAllAnalyticsModal').style.display = 'flex';
}

function closeDeleteAllAnalyticsModal() {
    document.getElementById('deleteAllAnalyticsModal').style.display = 'none';
}

async function deleteAllAnalytics() {
    try {
        const response = await fetch('/web/analytics', {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error('분석 보고서 전체 삭제에 실패했습니다');
        }

        showToast('✓ 모든 분석 보고서가 삭제되었습니다');
        closeDeleteAllAnalyticsModal();

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

// 라이센스 등록 모달
function showRegisterLicenseModal() {
    document.getElementById('registerLicenseModal').style.display = 'flex';
    document.getElementById('licenseKeyInput').value = '';
    document.getElementById('licenseError').style.display = 'none';
}

function closeRegisterLicenseModal() {
    document.getElementById('registerLicenseModal').style.display = 'none';
}

// 라이센스 키 입력 포맷팅
document.addEventListener('DOMContentLoaded', function() {
    const licenseInput = document.getElementById('licenseKeyInput');
    if (licenseInput) {
        licenseInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/-/g, '');
            if (value.length > 16) value = value.substr(0, 16);
            const formatted = value.match(/.{1,4}/g)?.join('-') || value;
            e.target.value = formatted;
        });
    }
});

async function registerLicense() {
    const licenseKey = document.getElementById('licenseKeyInput').value.trim();
    const errorElement = document.getElementById('licenseError');

    if (!licenseKey || licenseKey.length !== 19) {
        errorElement.textContent = '올바른 라이센스 키 형식이 아닙니다 (xxxx-xxxx-xxxx-xxxx)';
        errorElement.style.display = 'block';
        return;
    }

    try {
        const response = await fetch('/web/license/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                licenseKey: licenseKey
            })
        });

        if (!response.ok) {
            throw new Error('라이센스 등록에 실패했습니다');
        }

        showToast('✓ 라이센스가 등록되었습니다');
        closeRegisterLicenseModal();

        setTimeout(() => {
            window.location.reload();
        }, 1000);

    } catch (error) {
        console.error('Error:', error);
        errorElement.textContent = error.message;
        errorElement.style.display = 'block';
    }
}

// 라이센스 해제 모달
function showUnregisterLicenseModal() {
    document.getElementById('unregisterLicenseModal').style.display = 'flex';
}

function closeUnregisterLicenseModal() {
    document.getElementById('unregisterLicenseModal').style.display = 'none';
}

async function unregisterLicense() {
    try {
        const response = await fetch('/web/license', {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error('라이센스 해제에 실패했습니다');
        }

        showToast('✓ 라이센스가 해제되었습니다');
        closeUnregisterLicenseModal();

        setTimeout(() => {
            window.location.reload();
        }, 1000);

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ ' + error.message);
    }
}

// 모달 외부 클릭시 닫기
window.addEventListener('click', function(event) {
    const deleteAccountModal = document.getElementById('deleteAccountModal');
    const deleteAllApiKeysModal = document.getElementById('deleteAllApiKeysModal');
    const deleteAllAnalyticsModal = document.getElementById('deleteAllAnalyticsModal');
    const registerLicenseModal = document.getElementById('registerLicenseModal');
    const unregisterLicenseModal = document.getElementById('unregisterLicenseModal');

    if (event.target === deleteAccountModal) {
        closeDeleteAccountModal();
    } else if (event.target === deleteAllApiKeysModal) {
        closeDeleteAllApiKeysModal();
    } else if (event.target === deleteAllAnalyticsModal) {
        closeDeleteAllAnalyticsModal();
    } else if (event.target === registerLicenseModal) {
        closeRegisterLicenseModal();
    } else if (event.target === unregisterLicenseModal) {
        closeUnregisterLicenseModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeDeleteAccountModal();
        closeDeleteAllApiKeysModal();
        closeDeleteAllAnalyticsModal();
        closeRegisterLicenseModal();
        closeUnregisterLicenseModal();
    }
});
