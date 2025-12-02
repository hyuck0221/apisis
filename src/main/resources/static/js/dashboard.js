// API 키 생성 모달
function showCreateModal() {
    document.getElementById('createModal').classList.add('active');
    document.getElementById('keyName').value = '';
}

function closeCreateModal() {
    document.getElementById('createModal').classList.remove('active');
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

        // 목록 및 통계 새로고침
        loadApiKeys();
        loadDashboardStats();
    } catch (error) {
        console.error('Error:', error);
        alert('API 키 생성 중 오류가 발생했습니다');
    }
}

// 마스킹된 키 표시 (앞 4자리만 표시)
function maskApiKey(keyValue) {
    if (keyValue.length <= 4) return keyValue;
    return keyValue.substring(0, 4) + '****************************************'.substring(0, keyValue.length - 4);
}

// API 키 목록 로드
async function loadApiKeys() {
    try {
        const response = await fetch('/auth/keys');

        if (!response.ok) {
            throw new Error('API 키 목록을 불러오는데 실패했습니다');
        }

        const keys = await response.json();
        const listContainer = document.getElementById('apiKeyList');

        if (keys.length === 0) {
            listContainer.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">🔑</div>
                    <p>아직 생성된 API 키가 없습니다</p>
                    <p class="empty-subtitle">첫 번째 API 키를 생성해보세요!</p>
                </div>
            `;
            return;
        }

        listContainer.innerHTML = keys.map(key => `
            <div class="api-key-item">
                <div class="key-row">
                    <div class="key-name">${escapeHtml(key.name)}</div>
                    <div class="key-value-container">
                        <code class="api-key-value masked" id="key-${key.keyValue}">${maskApiKey(key.keyValue)}</code>
                        <button class="key-icon-btn" onclick="toggleKeyVisibility('${key.keyValue}')" title="보기/숨기기">
                            <svg id="icon-${key.keyValue}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                <circle cx="12" cy="12" r="3"></circle>
                            </svg>
                        </button>
                        <button class="key-icon-btn" onclick="copyKeyValue('${key.keyValue}')" title="복사">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                                <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                            </svg>
                        </button>
                    </div>
                </div>
                <div class="key-actions-row">
                    <span class="key-info-text">${formatDate(key.createdDate)}</span>
                    <span class="key-info-text">·</span>
                    <span class="key-info-text">${key.active ? '활성' : '비활성'}</span>
                    ${key.active ?
                        `<button class="key-action-btn" onclick="deactivateKey('${key.keyValue}')">비활성화</button>` :
                        `<button class="key-action-btn" onclick="activateKey('${key.keyValue}')">활성화</button>`
                    }
                    <button class="key-action-btn danger" onclick="deleteKey('${key.keyValue}')">삭제</button>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('apiKeyList').innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">⚠️</div>
                <p>API 키 목록을 불러오는데 실패했습니다</p>
            </div>
        `;
    }
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
        loadApiKeys();
        loadDashboardStats();
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
        loadApiKeys();
        loadDashboardStats();
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
        loadApiKeys();
        loadDashboardStats();
    } catch (error) {
        console.error('Error:', error);
        showToast('✗ API 키 삭제 중 오류가 발생했습니다');
    }
}

// API 키 값 복사
function copyKeyValue(keyValue) {
    navigator.clipboard.writeText(keyValue).then(() => {
        showToast('✓ API 키가 복사되었습니다');
    }).catch(err => {
        console.error('복사 실패:', err);
        showToast('✗ 클립보드 복사에 실패했습니다');
    });
}

// 모달 외부 클릭시 닫기
setupModalCloseOnClickOutside('createModal', closeCreateModal);

// 대시보드 통계 로드
async function loadDashboardStats() {
    try {
        const response = await fetch('/auth/stats');

        if (!response.ok) {
            throw new Error('통계를 불러오는데 실패했습니다');
        }

        const stats = await response.json();

        // API 키 개수
        document.querySelector('.grid-cols-4 .card:nth-child(1) div:nth-child(2) div:first-child').textContent = stats.apiKeyCount;

        // API 호출 수
        document.querySelector('.grid-cols-4 .card:nth-child(2) div:nth-child(2) div:first-child').textContent = stats.totalApiCalls.toLocaleString();

        // 평균 처리 시간
        document.querySelector('.grid-cols-4 .card:nth-child(3) div:nth-child(2) div:first-child').textContent = stats.averageResponseTimeMs + 'ms';

        // 성공률
        document.querySelector('.grid-cols-4 .card:nth-child(4) div:nth-child(2) div:first-child').textContent = stats.successRate.toFixed(1) + '%';
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

// 페이지 로드시 API 키 목록 및 통계 로드
document.addEventListener('DOMContentLoaded', function() {
    loadApiKeys();
    loadDashboardStats();

    // 30초마다 통계 갱신
    setInterval(loadDashboardStats, 30000);
});
