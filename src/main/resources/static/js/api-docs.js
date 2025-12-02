// API 문서 로드
async function loadAPIDocs() {
    try {
        const response = await fetch('/auth/docs/list');

        if (!response.ok) {
            throw new Error('API 문서를 불러오는데 실패했습니다');
        }

        const apis = await response.json();
        const contentContainer = document.getElementById('apiDocsContent');

        if (apis.length === 0) {
            contentContainer.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">📚</div>
                    <p style="font-size: 18px; font-weight: 600; color: #666; margin-bottom: 8px;">
                        등록된 API가 없습니다
                    </p>
                    <p style="font-size: 14px; color: #999;">
                        @Information 어노테이션을 추가하여 API를 등록하세요
                    </p>
                </div>
            `;
            return;
        }

        // 카테고리별로 그룹화
        const categorizedAPIs = apis.reduce((acc, api) => {
            if (!acc[api.category]) {
                acc[api.category] = [];
            }
            acc[api.category].push(api);
            return acc;
        }, {});

        // 카테고리별 HTML 생성
        const html = Object.entries(categorizedAPIs).map(([category, categoryAPIs]) => {
            const categoryId = getCategoryId(category);

            return `
                <div class="category-section" data-category="${categoryId}">
                    <div class="category-header" onclick="toggleCategory('${categoryId}')">
                        <div class="category-header-content">
                            <div class="category-title-group">
                                <span class="category-icon">${getCategoryIcon(category)}</span>
                                <h2 class="category-title">${escapeHtml(category)}</h2>
                                <span class="category-count">${categoryAPIs.length}개</span>
                            </div>
                            <div class="category-toggle" id="toggle-${categoryId}">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="6 9 12 15 18 9"></polyline>
                                </svg>
                            </div>
                        </div>
                    </div>
                    <div class="api-list" id="list-${categoryId}">
                        ${categoryAPIs.map(api => generateAPIItem(api)).join('')}
                    </div>
                </div>
            `;
        }).join('');

        contentContainer.innerHTML = html;

        // URL 해시가 있으면 해당 API로 스크롤
        if (window.location.hash) {
            setTimeout(() => {
                scrollToAPI(window.location.hash.substring(1));
            }, 100);
        }

    } catch (error) {
        console.error('Error:', error);
        document.getElementById('apiDocsContent').innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">⚠️</div>
                <p>API 문서를 불러오는데 실패했습니다</p>
            </div>
        `;
    }
}

// API 아이템 HTML 생성
function generateAPIItem(api) {
    const apiId = getAPIId(api);
    const hasRequest = api.requestSchema && Object.keys(api.requestSchema).length > 0;

    return `
        <div class="api-item" id="${apiId}">
            <div class="api-header">
                <div class="api-title-group">
                    <div class="api-title-with-version">
                        <span class="api-version">v${escapeHtml(api.version)}</span>
                        <h3 class="api-title">${escapeHtml(api.title)}</h3>
                    </div>
                    <p class="api-description">${escapeHtml(api.description)}</p>
                    <div class="api-endpoint">
                        <span class="method-badge ${api.method}">${api.method}</span>
                        <div class="api-url-container">
                            <code class="api-url">${escapeHtml(api.url)}</code>
                            <button class="copy-url-btn" onclick="copyURL('${escapeHtml(api.url)}')" title="URL 복사">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                                    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                                </svg>
                            </button>
                        </div>
                    </div>
                    ${api.callLimit > 0 ? `<div class="api-meta"><span class="api-limit">호출 제한: ${api.callLimit}회/일</span></div>` : ''}
                </div>
                <a href="#${apiId}" class="api-link" onclick="copyAPILink('${apiId}')">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path>
                        <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path>
                    </svg>
                    링크
                </a>
            </div>

            ${hasRequest ? `
                <div class="schema-section">
                    <div class="schema-title">Request</div>
                    <div class="schema-content">
                        <pre>${renderJSONSchema(api.requestSchema)}</pre>
                    </div>
                </div>
            ` : ''}

            <div class="schema-section">
                <div class="schema-title">Response</div>
                <div class="schema-content">
                    <pre>${renderJSONSchema(api.responseSchema)}</pre>
                </div>
            </div>
        </div>
    `;
}

// JSON 스키마를 코드 하이라이트와 함께 렌더링
function renderJSONSchema(schema, indent = 0) {
    if (!schema || Object.keys(schema).length === 0) {
        return '<span class="schema-empty">No schema available</span>';
    }

    const indentStr = '  '.repeat(indent);
    const lines = [];

    lines.push('{');

    const entries = Object.entries(schema);
    entries.forEach(([key, value], index) => {
        const isLast = index === entries.length - 1;
        const comma = isLast ? '' : ',';

        if (typeof value === 'object' && !Array.isArray(value) && value !== null) {
            lines.push(`${indentStr}  <span class="json-key">"${escapeHtml(key)}"</span>: ${renderJSONSchema(value, indent + 1)}${comma}`);
        } else {
            const valueStr = formatJSONValue(value);
            lines.push(`${indentStr}  <span class="json-key">"${escapeHtml(key)}"</span>: ${valueStr}${comma}`);
        }
    });

    lines.push(`${indentStr}}`);

    return lines.join('\n');
}

// JSON 값 포맷팅 (타입별 색상)
function formatJSONValue(value) {
    if (value === null) {
        return '<span class="json-null">null</span>';
    }
    if (typeof value === 'boolean') {
        return `<span class="json-boolean">${value}</span>`;
    }
    if (typeof value === 'number') {
        return `<span class="json-number">${value}</span>`;
    }
    return `<span class="json-string">"${escapeHtml(String(value))}"</span>`;
}

// 카테고리 토글
function toggleCategory(categoryId) {
    const list = document.getElementById(`list-${categoryId}`);
    const toggle = document.getElementById(`toggle-${categoryId}`);

    if (list.classList.contains('expanded')) {
        list.classList.remove('expanded');
        toggle.classList.remove('expanded');
    } else {
        list.classList.add('expanded');
        toggle.classList.add('expanded');
    }
}

// 특정 API로 스크롤
function scrollToAPI(apiId) {
    const element = document.getElementById(apiId);
    if (element) {
        // 해당 카테고리 펼치기
        const categorySection = element.closest('.category-section');
        if (categorySection) {
            const categoryId = categorySection.getAttribute('data-category');
            const list = document.getElementById(`list-${categoryId}`);
            const toggle = document.getElementById(`toggle-${categoryId}`);

            if (!list.classList.contains('expanded')) {
                list.classList.add('expanded');
                toggle.classList.add('expanded');
            }
        }

        // 스크롤
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}

// API 링크 복사
function copyAPILink(apiId) {
    const url = `${window.location.origin}${window.location.pathname}#${apiId}`;
    navigator.clipboard.writeText(url).then(() => {
        showToast('✓ 링크가 복사되었습니다');
    }).catch(err => {
        console.error('복사 실패:', err);
        showToast('✗ 링크 복사에 실패했습니다');
    });
}

// URL 복사
function copyURL(url) {
    navigator.clipboard.writeText(url).then(() => {
        showToast('✓ URL이 복사되었습니다');
    }).catch(err => {
        console.error('복사 실패:', err);
        showToast('✗ URL 복사에 실패했습니다');
    });
}

// 카테고리 ID 생성
function getCategoryId(category) {
    return category.toLowerCase().replace(/\s+/g, '-');
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

// 해시 변경 이벤트 리스너
window.addEventListener('hashchange', function() {
    if (window.location.hash) {
        scrollToAPI(window.location.hash.substring(1));
    }
});

// 페이지 로드시 API 문서 로드
document.addEventListener('DOMContentLoaded', function() {
    loadAPIDocs();
});
