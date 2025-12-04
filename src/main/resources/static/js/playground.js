// API 놀이터 로직

let apiKeys = [];
let apis = [];
let selectedAPI = null;

// 페이지 로드시 초기화
document.addEventListener('DOMContentLoaded', function() {
    loadAPIKeys();
    loadAPIs();
    setupEventListeners();

    // URL 파라미터에서 API 정보 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const apiUrl = urlParams.get('api');
    const apiMethod = urlParams.get('method');

    if (apiUrl && apiMethod) {
        // API가 로드된 후 자동 선택
        setTimeout(() => {
            selectAPIFromParams(apiMethod, apiUrl);
        }, 500);
    }
});

// 이벤트 리스너 설정
function setupEventListeners() {
    document.getElementById('apiKeySelect').addEventListener('change', handleAPIKeyChange);
    document.getElementById('apiSelect').addEventListener('change', handleAPIChange);
    document.getElementById('executeBtn').addEventListener('click', executeAPI);
    document.getElementById('copyResponseBtn').addEventListener('click', copyResponse);
}

// API Keys 로드
async function loadAPIKeys() {
    try {
        const response = await fetch('/web/keys');

        if (!response.ok) {
            throw new Error('API Keys를 불러오는데 실패했습니다');
        }

        apiKeys = await response.json();
        const select = document.getElementById('apiKeySelect');

        apiKeys.forEach(key => {
            const option = document.createElement('option');
            option.value = key.keyValue;
            const maskedKey = key.keyValue.substring(0, 4) + '*'.repeat(key.keyValue.length - 4);
            option.textContent = `${key.name} (${maskedKey})`;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ API Keys를 불러오는데 실패했습니다');
    }
}

// APIs 로드
async function loadAPIs() {
    try {
        const response = await fetch('/web/docs/list');

        if (!response.ok) {
            throw new Error('API 목록을 불러오는데 실패했습니다');
        }

        apis = await response.json();
        populateAPISelect();

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ API 목록을 불러오는데 실패했습니다');
    }
}

// API 선택 목록 채우기
function populateAPISelect() {
    const apiSelect = document.getElementById('apiSelect');
    apiSelect.innerHTML = '<option value="">API를 선택하세요...</option>';

    // 카테고리별로 그룹화
    const categorizedAPIs = apis.reduce((acc, api) => {
        if (!acc[api.category]) {
            acc[api.category] = [];
        }
        acc[api.category].push(api);
        return acc;
    }, {});

    // 옵션 그룹으로 추가
    Object.entries(categorizedAPIs).forEach(([category, categoryAPIs]) => {
        const optgroup = document.createElement('optgroup');
        optgroup.label = category;

        categoryAPIs.forEach(api => {
            const option = document.createElement('option');
            option.value = JSON.stringify({url: api.url, method: api.method});
            option.textContent = `[${api.method}] ${api.title}`;
            optgroup.appendChild(option);
        });

        apiSelect.appendChild(optgroup);
    });
}

// API Key 선택 변경
function handleAPIKeyChange(e) {
    // API Key 변경 시 실행 버튼 상태 업데이트
    updateExecuteButton();
}

// API 선택 변경
function handleAPIChange(e) {
    const value = e.target.value;

    if (!value) {
        hideAPIInfo();
        hideRequestSection();
        hideExecuteSection();
        hideResponseSection();
        return;
    }

    const {url, method} = JSON.parse(value);
    selectedAPI = apis.find(api => api.url === url && api.method === method);

    if (selectedAPI) {
        showAPIInfo();
        showRequestSection();
        showExecuteSection();
        hideResponseSection();
        updateExecuteButton();
    }
}

// 실행 버튼 상태 업데이트
function updateExecuteButton() {
    const apiKey = document.getElementById('apiKeySelect').value;
    const executeBtn = document.getElementById('executeBtn');
    const apiKeyGuide = document.getElementById('apiKeyGuide');

    if (!apiKey) {
        executeBtn.disabled = true;
        if (apiKeyGuide) {
            apiKeyGuide.style.display = 'block';
        }
    } else {
        executeBtn.disabled = false;
        if (apiKeyGuide) {
            apiKeyGuide.style.display = 'none';
        }
    }
}

// URL 파라미터로 API 선택
function selectAPIFromParams(method, url) {
    const api = apis.find(a => a.url === url && a.method === method);

    if (!api) {
        return;
    }

    // API 선택 (API Key는 자동 선택하지 않음)
    const apiSelect = document.getElementById('apiSelect');
    const optionValue = JSON.stringify({url: api.url, method: api.method});
    apiSelect.value = optionValue;
    apiSelect.dispatchEvent(new Event('change'));
}

// API 정보 표시
function showAPIInfo() {
    const apiInfo = document.getElementById('apiInfo');
    const methodBadge = document.getElementById('apiMethod');
    const apiUrl = document.getElementById('apiUrl');
    const apiDescription = document.getElementById('apiDescription');

    apiInfo.classList.remove('hidden');
    methodBadge.textContent = selectedAPI.method;
    methodBadge.className = `method-badge ${selectedAPI.method}`;
    apiUrl.textContent = selectedAPI.url;
    apiDescription.textContent = selectedAPI.description;
}

function hideAPIInfo() {
    document.getElementById('apiInfo').classList.add('hidden');
}

// Request 섹션 표시
function showRequestSection() {
    const requestSection = document.getElementById('requestSection');
    const requestFields = document.getElementById('requestFields');

    requestFields.innerHTML = '';

    if (selectedAPI.requestInfos && selectedAPI.requestInfos.length > 0) {
        selectedAPI.requestInfos.forEach(field => {
            const fieldGroup = createFieldGroup(field);
            requestFields.appendChild(fieldGroup);
        });
        requestSection.style.display = 'block';
    } else {
        requestSection.style.display = 'none';
    }
}

function hideRequestSection() {
    document.getElementById('requestSection').style.display = 'none';
}

// 필드 그룹 생성
function createFieldGroup(field) {
    const fieldGroup = document.createElement('div');
    fieldGroup.className = 'field-group';

    // 라벨
    const label = document.createElement('div');
    label.className = 'field-label';

    const pathSpan = document.createElement('span');
    pathSpan.className = 'field-path';
    pathSpan.textContent = field.path;

    const typeSpan = document.createElement('span');
    typeSpan.className = 'field-type';
    typeSpan.textContent = field.type;

    const badges = document.createElement('div');
    badges.className = 'field-badges';

    if (field.parameterType) {
        const paramBadge = document.createElement('span');
        paramBadge.className = `param-type-badge ${field.parameterType.toLowerCase()}`;
        paramBadge.textContent = field.parameterType;
        badges.appendChild(paramBadge);
    }

    const nullableBadge = document.createElement('span');
    nullableBadge.className = field.nullable ? 'nullable-badge' : 'required-badge';
    nullableBadge.textContent = field.nullable ? 'nullable' : 'required';
    badges.appendChild(nullableBadge);

    label.appendChild(pathSpan);
    label.appendChild(typeSpan);
    label.appendChild(badges);

    fieldGroup.appendChild(label);

    // 설명
    if (field.description) {
        const description = document.createElement('div');
        description.className = 'field-description';
        description.textContent = field.description;
        fieldGroup.appendChild(description);
    }

    // 입력 필드
    const input = document.createElement('input');
    input.type = 'text';
    input.className = 'field-input';
    input.placeholder = `${field.type} 값을 입력하세요${field.nullable ? ' (선택)' : ''}`;
    input.dataset.path = field.path;
    input.dataset.type = field.type;
    input.dataset.paramType = field.parameterType;
    input.dataset.nullable = field.nullable;

    fieldGroup.appendChild(input);

    return fieldGroup;
}

function showExecuteSection() {
    document.getElementById('executeSection').style.display = 'block';
}

function hideExecuteSection() {
    document.getElementById('executeSection').style.display = 'none';
}

function showResponseSection() {
    document.getElementById('responseSection').style.display = 'block';
}

function hideResponseSection() {
    document.getElementById('responseSection').style.display = 'none';
}

// API 실행
async function executeAPI() {
    const apiKey = document.getElementById('apiKeySelect').value;

    if (!apiKey || !selectedAPI) {
        showToast('✗ API Key와 API를 선택해주세요');
        return;
    }

    // Request 데이터 수집
    const requestData = collectRequestData();

    // 필수 필드 검증
    const missingFields = validateRequiredFields(requestData);
    if (missingFields.length > 0) {
        showToast(`✗ 필수 필드를 입력해주세요: ${missingFields.join(', ')}`);
        return;
    }

    // 버튼 비활성화
    const executeBtn = document.getElementById('executeBtn');
    executeBtn.disabled = true;
    executeBtn.textContent = '호출 중...';

    try {
        // URL 생성
        let url = selectedAPI.url;
        const queryParams = [];
        const pathParams = {};
        const headers = {
            'X-API-Key': apiKey
        };
        let body = null;

        // 파라미터 분류
        Object.entries(requestData).forEach(([path, value]) => {
            const field = selectedAPI.requestInfos.find(f => f.path === path);
            if (!field || !value) return;

            switch (field.parameterType) {
                case 'PATH':
                    pathParams[path] = value;
                    break;
                case 'QUERY':
                    queryParams.push(`${path}=${encodeURIComponent(value)}`);
                    break;
                case 'HEADER':
                    headers[path] = value;
                    break;
                case 'BODY':
                    if (!body) body = {};
                    body[path] = parseValue(value, field.type);
                    break;
            }
        });

        // Path 파라미터 치환
        Object.entries(pathParams).forEach(([key, value]) => {
            url = url.replace(`{${key}}`, value);
        });

        // Query 파라미터 추가
        if (queryParams.length > 0) {
            url += '?' + queryParams.join('&');
        }

        // Fetch 옵션
        const fetchOptions = {
            method: selectedAPI.method,
            headers: headers
        };

        if (body && (selectedAPI.method === 'POST' || selectedAPI.method === 'PUT')) {
            fetchOptions.headers['Content-Type'] = 'application/json';
            fetchOptions.body = JSON.stringify(body);
        }

        // API 호출
        const response = await fetch(url, fetchOptions);
        const responseData = await response.json();

        // 응답 표시
        displayResponse(response.status, responseData);

    } catch (error) {
        console.error('Error:', error);
        displayResponse(0, {error: error.message});
    } finally {
        executeBtn.disabled = false;
        executeBtn.innerHTML = `
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polygon points="5 3 19 12 5 21 5 3"></polygon>
            </svg>
            API 호출하기
        `;
    }
}

// Request 데이터 수집
function collectRequestData() {
    const data = {};
    const inputs = document.querySelectorAll('.field-input');

    inputs.forEach(input => {
        const path = input.dataset.path;
        const value = input.value.trim();
        if (value) {
            data[path] = value;
        }
    });

    return data;
}

// 필수 필드 검증
function validateRequiredFields(requestData) {
    const missing = [];

    selectedAPI.requestInfos.forEach(field => {
        if (!field.nullable && !requestData[field.path]) {
            missing.push(field.path);
        }
    });

    return missing;
}

// 값 파싱 (타입에 맞게)
function parseValue(value, type) {
    const lowerType = type.toLowerCase();

    if (lowerType.includes('int') || lowerType.includes('long')) {
        return parseInt(value);
    }
    if (lowerType.includes('double') || lowerType.includes('float')) {
        return parseFloat(value);
    }
    if (lowerType.includes('boolean')) {
        return value.toLowerCase() === 'true';
    }

    return value;
}

// 응답 표시
function displayResponse(status, data) {
    showResponseSection();

    const statusElement = document.getElementById('responseStatus');
    const contentElement = document.getElementById('responseContent');

    // 상태 표시
    if (status >= 200 && status < 300) {
        statusElement.textContent = `${status} OK`;
        statusElement.className = 'response-status success';
    } else {
        statusElement.textContent = status > 0 ? `${status} Error` : 'Network Error';
        statusElement.className = 'response-status error';
    }

    // 응답 내용 표시
    contentElement.textContent = JSON.stringify(data, null, 2);

    // 스크롤
    document.getElementById('responseSection').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// 응답 복사
function copyResponse() {
    const content = document.getElementById('responseContent').textContent;

    navigator.clipboard.writeText(content).then(() => {
        showToast('✓ 응답이 복사되었습니다');
    }).catch(err => {
        console.error('복사 실패:', err);
        showToast('✗ 복사에 실패했습니다');
    });
}
