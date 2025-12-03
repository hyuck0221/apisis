// API 목록 관리
let allAPIs = [];
let filteredAPIs = [];

// API 목록 로드
async function loadAPIList() {
    try {
        const response = await fetch('/auth/docs/list');

        if (!response.ok) {
            throw new Error('API 목록을 불러오는데 실패했습니다');
        }

        allAPIs = await response.json();
        filteredAPIs = [...allAPIs];

        const contentContainer = document.getElementById('apiListContent');

        if (allAPIs.length === 0) {
            contentContainer.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">📚</div>
                    <p>등록된 API가 없습니다</p>
                    <small>@Information 어노테이션을 추가하여 API를 등록하세요</small>
                </div>
            `;
            return;
        }

        // 카테고리 필터 옵션 생성
        const categories = [...new Set(allAPIs.map(api => api.category))];
        const categoryFilter = document.getElementById('categoryFilter');
        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            categoryFilter.appendChild(option);
        });

        renderAPIList(filteredAPIs);

        // 검색 및 필터 이벤트 리스너
        document.getElementById('searchInput').addEventListener('input', handleFilter);
        document.getElementById('categoryFilter').addEventListener('change', handleFilter);
        document.getElementById('methodFilter').addEventListener('change', handleFilter);

    } catch (error) {
        console.error('Error:', error);
        document.getElementById('apiListContent').innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">⚠️</div>
                <p>API 목록을 불러오는데 실패했습니다</p>
                <small>${error.message}</small>
            </div>
        `;
    }
}

// API 목록 렌더링
function renderAPIList(apis) {
    const contentContainer = document.getElementById('apiListContent');

    if (apis.length === 0) {
        contentContainer.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">🔍</div>
                <p>검색 결과가 없습니다</p>
                <small>다른 검색어나 필터를 시도해보세요</small>
            </div>
        `;
        return;
    }

    const html = `
        <div class="api-grid">
            ${apis.map(api => generateAPICard(api)).join('')}
        </div>
    `;

    contentContainer.innerHTML = html;
}

// API 카드 생성
function generateAPICard(api) {
    const apiId = getAPIId(api);

    return `
        <div class="api-card" onclick="goToAPIDoc('${apiId}')">
            <div class="api-card-header">
                <div class="api-card-title-group">
                    <span class="api-card-version">v${escapeHtml(api.version)}</span>
                    <h3 class="api-card-title">${escapeHtml(api.title)}</h3>
                    <span class="api-card-category">
                        ${getCategoryIcon(api.category)}
                        ${escapeHtml(api.category)}
                    </span>
                </div>
            </div>
            <p class="api-card-description">${escapeHtml(api.description)}</p>
            <div class="api-card-endpoint">
                <span class="api-card-method ${api.method}">${api.method}</span>
                <code class="api-card-url">${escapeHtml(api.url)}</code>
            </div>
            <div class="api-card-footer">
                <div class="api-card-actions">
                    <button class="action-btn" onclick="event.stopPropagation(); copyURL('${escapeHtml(api.url)}')" title="URL 복사">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                            <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                        </svg>
                        복사
                    </button>
                </div>
                <button class="btn-primary-small" onclick="event.stopPropagation(); goToAPIDoc('${apiId}')">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M9 18l6-6-6-6"/>
                    </svg>
                    상세보기
                </button>
            </div>
        </div>
    `;
}

// 검색 및 필터 처리
function handleFilter() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const categoryFilter = document.getElementById('categoryFilter').value;
    const methodFilter = document.getElementById('methodFilter').value;

    filteredAPIs = allAPIs.filter(api => {
        const matchesSearch = !searchTerm ||
            api.title.toLowerCase().includes(searchTerm) ||
            api.description.toLowerCase().includes(searchTerm) ||
            api.url.toLowerCase().includes(searchTerm);

        const matchesCategory = !categoryFilter || api.category === categoryFilter;
        const matchesMethod = !methodFilter || api.method === methodFilter;

        return matchesSearch && matchesCategory && matchesMethod;
    });

    renderAPIList(filteredAPIs);
}

// API 문서로 이동
function goToAPIDoc(apiId) {
    window.location.href = `/docs#${apiId}`;
}

// URL 복사
function copyURL(url) {
    const fullUrl = `${window.location.origin}${url}`;
    navigator.clipboard.writeText(fullUrl).then(() => {
        showToast('✓ URL이 복사되었습니다');
    }).catch(err => {
        console.error('복사 실패:', err);
        showToast('✗ URL 복사에 실패했습니다');
    });
}

// API ID 생성
function getAPIId(api) {
    return `${api.method.toLowerCase()}-${api.url.replace(/[^a-z0-9]/gi, '-')}`;
}

// 카테고리 아이콘 매핑
function getCategoryIcon(category) {
    const icons = {
        '기본': '🏠',
        '인증': '🔐',
        '사용자': '👤',
        '데이터': '💾',
        '분석': '📊',
        '알림': '🔔',
        '검색': '🔍',
        '파일': '📁',
        '결제': '💳',
        '테스트': '🧪',
        '기타': '📚'
    };
    return icons[category] || '📄';
}

// 페이지 로드시 API 목록 로드
document.addEventListener('DOMContentLoaded', function() {
    loadAPIList();
});
