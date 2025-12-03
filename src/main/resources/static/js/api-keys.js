// API 키 생성 모달
function showCreateModal() {
    document.getElementById('createModal').classList.add('active');
    document.getElementById('keyName').value = '';
}

function closeCreateModal() {
    document.getElementById('createModal').classList.remove('active');
}

// API 키 이름 수정 모달
function showEditNameModal(keyValue, currentName) {
    document.getElementById('editNameModal').classList.add('active');
    document.getElementById('editKeyValue').value = keyValue;
    document.getElementById('editKeyName').value = currentName;
    document.getElementById('editKeyName').focus();
}

function closeEditNameModal() {
    document.getElementById('editNameModal').classList.remove('active');
}

// API 키 생성
async function createApiKey() {
    const keyName = document.getElementById('keyName').value.trim();

    if (!keyName) {
        alert('키 이름을 입력해주세요');
        return;
    }

    try {
        const response = await fetch('/auth/keys', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name: keyName })
        });

        if (!response.ok) {
            throw new Error('API 키 생성에 실패했습니다');
        }

        closeCreateModal();
        showToast('✓ API 키가 생성되었습니다!');

        // 목록 새로고침
        loadApiKeyStats();
    } catch (error) {
        console.error('Error:', error);
        alert('API 키 생성 중 오류가 발생했습니다');
    }
}

// API 키 이름 수정
async function updateKeyName() {
    const keyValue = document.getElementById('editKeyValue').value;
    const newName = document.getElementById('editKeyName').value.trim();

    if (!newName) {
        alert('키 이름을 입력해주세요');
        return;
    }

    try {
        const response = await fetch(`/auth/keys/${keyValue}/name?name=${encodeURIComponent(newName)}`, {
            method: 'PUT'
        });

        if (!response.ok) {
            throw new Error('API 키 이름 수정에 실패했습니다');
        }

        closeEditNameModal();
        showToast('✓ API 키 이름이 수정되었습니다');
        loadApiKeyStats();
    } catch (error) {
        console.error('Error:', error);
        showToast('✗ API 키 이름 수정 중 오류가 발생했습니다');
    }
}

// API 키 표시/숨김 토글
function toggleKeyVisibility(keyValue) {
    const keyElement = document.getElementById(`key-${keyValue}`);
    const iconElement = document.getElementById(`icon-${keyValue}`);

    if (keyElement.classList.contains('masked')) {
        keyElement.textContent = keyValue;
        keyElement.classList.remove('masked');
        iconElement.innerHTML = `
            <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
            <line x1="1" y1="1" x2="23" y2="23"></line>
        `;
    } else {
        keyElement.textContent = maskApiKey(keyValue);
        keyElement.classList.add('masked');
        iconElement.innerHTML = `
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
            <circle cx="12" cy="12" r="3"></circle>
        `;
    }
}

// API 키 복사
function copyKeyValue(keyValue) {
    navigator.clipboard.writeText(keyValue).then(() => {
        showToast('✓ API 키가 복사되었습니다');
    }).catch(err => {
        console.error('복사 실패:', err);
        showToast('✗ 클립보드 복사에 실패했습니다');
    });
}

// API 키 활성화
async function activateKey(keyValue) {
    if (!confirm('이 API 키를 활성화하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`/auth/keys/${keyValue}/activate`, {
            method: 'PUT'
        });

        if (!response.ok) {
            throw new Error('API 키 활성화에 실패했습니다');
        }

        showToast('✓ API 키가 활성화되었습니다');
        loadApiKeyStats();
    } catch (error) {
        console.error('Error:', error);
        showToast('✗ API 키 활성화 중 오류가 발생했습니다');
    }
}

// API 키 비활성화
async function deactivateKey(keyValue) {
    if (!confirm('이 API 키를 비활성화하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`/auth/keys/${keyValue}/deactivate`, {
            method: 'PUT'
        });

        if (!response.ok) {
            throw new Error('API 키 비활성화에 실패했습니다');
        }

        showToast('✓ API 키가 비활성화되었습니다');
        loadApiKeyStats();
    } catch (error) {
        console.error('Error:', error);
        showToast('✗ API 키 비활성화 중 오류가 발생했습니다');
    }
}

// API 키 삭제
async function deleteKey(keyValue) {
    if (!confirm('이 API 키를 영구적으로 삭제하시겠습니까?\n삭제된 키는 복구할 수 없습니다.')) {
        return;
    }

    try {
        const response = await fetch(`/auth/keys/${keyValue}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('API 키 삭제에 실패했습니다');
        }

        showToast('✓ API 키가 삭제되었습니다');
        loadApiKeyStats();
    } catch (error) {
        console.error('Error:', error);
        showToast('✗ API 키 삭제 중 오류가 발생했습니다');
    }
}

// 모달 외부 클릭시 닫기
setupModalCloseOnClickOutside('createModal', closeCreateModal);
setupModalCloseOnClickOutside('editNameModal', closeEditNameModal);

// API 키 통계 목록 로드
async function loadApiKeyStats() {
    try {
        const response = await fetch('/auth/keys/stats');

        if (!response.ok) {
            throw new Error('API 키 통계를 불러오는데 실패했습니다');
        }

        const stats = await response.json();
        const listContainer = document.getElementById('apiKeyStatsList');

        if (stats.length === 0) {
            listContainer.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">🔑</div>
                    <p style="font-size: 18px; font-weight: 600; color: #666; margin-bottom: 8px;">
                        아직 생성된 API 키가 없습니다
                    </p>
                    <p class="empty-subtitle" style="font-size: 14px; color: #999;">
                        첫 번째 API 키를 생성해보세요!
                    </p>
                </div>
            `;
            return;
        }

        listContainer.innerHTML = stats.map(stat => {
            // 퍼센티지 계산 (전체 대비)
            const callsPercentage = stat.totalCallsAllKeys > 0
                ? (stat.totalCalls / stat.totalCallsAllKeys * 100).toFixed(1)
                : 0;
            const successPercentage = stat.successRate.toFixed(1);
            const apiCountPercentage = stat.totalUniqueApisAllKeys > 0
                ? (stat.uniqueApiCount / stat.totalUniqueApisAllKeys * 100).toFixed(1)
                : 0;

            // 막대 색상 결정 (성공률 기준)
            const getBarClass = (value) => {
                if (value < 70) return 'low';
                if (value < 90) return 'medium';
                return 'high';
            };

            return `
                <div class="api-key-stats-card">
                    <div class="stats-card-header">
                        <div class="key-info-header">
                            <div class="key-name-group">
                                <h3 class="key-title">${escapeHtml(stat.name)}</h3>
                                <button class="key-edit-btn" onclick="showEditNameModal('${stat.apiKeyValue}', '${escapeHtml(stat.name)}')" title="이름 수정">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                    </svg>
                                </button>
                            </div>
                            <div class="key-value-container">
                                <code class="key-value-display masked" id="key-${stat.apiKeyValue}">${maskApiKey(stat.apiKeyValue)}</code>
                                <button class="key-icon-btn" onclick="toggleKeyVisibility('${stat.apiKeyValue}')" title="보기/숨기기">
                                    <svg id="icon-${stat.apiKeyValue}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                </button>
                                <button class="key-icon-btn" onclick="copyKeyValue('${stat.apiKeyValue}')" title="복사">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <span class="key-status-badge ${stat.active ? 'active' : 'inactive'}">
                            ${stat.active ? '활성' : '비활성'}
                        </span>
                    </div>

                    <div class="stats-grid">
                        <div class="stat-item">
                            <div class="stat-label">API 호출 수</div>
                            <div class="stat-value">${stat.totalCalls.toLocaleString()}</div>
                            <div class="stat-bar-container">
                                <div class="stat-bar high" style="width: ${callsPercentage}%"></div>
                            </div>
                            <div class="stat-percentage">${callsPercentage}% of total</div>
                        </div>

                        <div class="stat-item">
                            <div class="stat-label">성공률</div>
                            <div class="stat-value">${successPercentage}%</div>
                            <div class="stat-bar-container">
                                <div class="stat-bar ${getBarClass(stat.successRate)}" style="width: ${successPercentage}%"></div>
                            </div>
                            <div class="stat-percentage">${stat.successRate >= 90 ? 'Excellent' : stat.successRate >= 70 ? 'Good' : 'Needs attention'}</div>
                        </div>

                        <div class="stat-item">
                            <div class="stat-label">평균 처리 시간</div>
                            <div class="stat-value">${stat.averageResponseTimeMs}ms</div>
                            <div class="stat-bar-container">
                                <div class="stat-bar ${stat.averageResponseTimeMs < 100 ? 'high' : stat.averageResponseTimeMs < 300 ? 'medium' : 'low'}"
                                     style="width: ${Math.min(100, (500 - stat.averageResponseTimeMs) / 5)}%"></div>
                            </div>
                            <div class="stat-percentage">${stat.averageResponseTimeMs < 100 ? 'Fast' : stat.averageResponseTimeMs < 300 ? 'Normal' : 'Slow'}</div>
                        </div>

                        <div class="stat-item">
                            <div class="stat-label">사용한 API 종류</div>
                            <div class="stat-value">${stat.uniqueApiCount}</div>
                            <div class="stat-bar-container">
                                <div class="stat-bar high" style="width: ${apiCountPercentage}%"></div>
                            </div>
                            <div class="stat-percentage">${apiCountPercentage}% of available</div>
                        </div>
                    </div>

                    <div class="card-actions">
                        ${stat.active ?
                            `<button class="key-action-btn" onclick="deactivateKey('${stat.apiKeyValue}')">비활성화</button>` :
                            `<button class="key-action-btn success" onclick="activateKey('${stat.apiKeyValue}')">활성화</button>`
                        }
                        <button class="key-action-btn danger" onclick="deleteKey('${stat.apiKeyValue}')">삭제</button>
                    </div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Error:', error);
        document.getElementById('apiKeyStatsList').innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">⚠️</div>
                <p>API 키 통계를 불러오는데 실패했습니다</p>
            </div>
        `;
    }
}

// 마스킹된 키 표시 (앞 4자리만 표시)
function maskApiKey(keyValue) {
    if (keyValue.length <= 4) return keyValue;
    return keyValue.substring(0, 4) + '****************************************'.substring(0, keyValue.length - 4);
}

// 자동 새로고침 관련 변수
let refreshInterval = null;
let progressInterval = null;
let currentProgress = 0;
const REFRESH_DURATION = 10000; // 10초
const PROGRESS_UPDATE_INTERVAL = 100; // 0.1초마다 업데이트

// 프로그레스바 시작
function startProgressBar() {
    currentProgress = 0;
    const progressBar = document.getElementById('refreshProgressBar');
    const increment = (PROGRESS_UPDATE_INTERVAL / REFRESH_DURATION) * 100;

    // 기존 인터벌 정리
    if (progressInterval) {
        clearInterval(progressInterval);
    }

    progressInterval = setInterval(() => {
        currentProgress += increment;
        if (currentProgress >= 100) {
            currentProgress = 100;
        }
        progressBar.style.width = currentProgress + '%';
    }, PROGRESS_UPDATE_INTERVAL);
}

// 프로그레스바 리셋
function resetProgressBar() {
    currentProgress = 0;
    const progressBar = document.getElementById('refreshProgressBar');
    progressBar.style.width = '0%';

    if (progressInterval) {
        clearInterval(progressInterval);
    }

    startProgressBar();
}

// 통계 새로고침
function refreshStats() {
    loadApiKeyStats();
    resetProgressBar();
}

// 자동 새로고침 시작
function startAutoRefresh() {
    // 기존 인터벌 정리
    if (refreshInterval) {
        clearInterval(refreshInterval);
    }

    // 10초마다 자동 새로고침
    refreshInterval = setInterval(() => {
        loadApiKeyStats();
        resetProgressBar();
    }, REFRESH_DURATION);

    // 프로그레스바 시작
    startProgressBar();
}

// 자동 새로고침 중지
function stopAutoRefresh() {
    if (refreshInterval) {
        clearInterval(refreshInterval);
        refreshInterval = null;
    }
    if (progressInterval) {
        clearInterval(progressInterval);
        progressInterval = null;
    }
}

// 페이지 로드시 API 키 통계 로드 및 자동 새로고침 시작
document.addEventListener('DOMContentLoaded', function() {
    loadApiKeyStats();
    startAutoRefresh();
});

// 페이지 언로드시 인터벌 정리
window.addEventListener('beforeunload', function() {
    stopAutoRefresh();
});
