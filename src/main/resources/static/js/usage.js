// 사용량 통계 관리
let currentPeriod = 'today';
let customStartDate = null;
let customEndDate = null;
let callTrendChart = null;
let responseTimeChart = null;

// 페이지 로드시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializePeriodSelector();
    initializeCharts();
    loadUsageData();
    generateTrafficHeatmap();
});

// 기간 선택 초기화
function initializePeriodSelector() {
    const periodButtons = document.querySelectorAll('.period-btn');
    const customDateRange = document.getElementById('customDateRange');

    periodButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            periodButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            currentPeriod = this.dataset.period;

            // 사용자 지정 선택 시 날짜 입력 표시
            if (currentPeriod === 'custom') {
                customDateRange.style.display = 'block';
                initializeDateInputs();
            } else {
                customDateRange.style.display = 'none';
                customStartDate = null;
                customEndDate = null;
                loadUsageData();
            }
        });
    });
}

// 날짜 입력 초기화
function initializeDateInputs() {
    const today = new Date();
    const sevenDaysAgo = new Date(today);
    sevenDaysAgo.setDate(today.getDate() - 7);

    // 기본값: 오늘부터 7일 전
    document.getElementById('startDate').value = formatDateForInput(sevenDaysAgo);
    document.getElementById('endDate').value = formatDateForInput(today);

    // 최대 날짜는 오늘
    document.getElementById('startDate').max = formatDateForInput(today);
    document.getElementById('endDate').max = formatDateForInput(today);
}

// 날짜 포맷 (YYYY-MM-DD)
function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 사용자 지정 날짜 적용
function applyCustomDate() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    if (!startDate || !endDate) {
        showToast('⚠️ 시작일과 종료일을 모두 선택해주세요');
        return;
    }

    const start = new Date(startDate);
    const end = new Date(endDate);

    if (start > end) {
        showToast('⚠️ 시작일은 종료일보다 이전이어야 합니다');
        return;
    }

    // 최대 90일 제한
    const daysDiff = Math.floor((end - start) / (1000 * 60 * 60 * 24));
    if (daysDiff > 90) {
        showToast('⚠️ 최대 90일까지만 조회 가능합니다');
        return;
    }

    customStartDate = startDate;
    customEndDate = endDate;
    loadUsageData();
}

// 차트 초기화
function initializeCharts() {
    // 호출 추이 차트
    const callTrendCtx = document.getElementById('callTrendChart');
    if (callTrendCtx) {
        callTrendChart = new Chart(callTrendCtx, {
            type: 'line',
            data: {
                labels: generateTimeLabels(),
                datasets: [
                    {
                        label: '성공',
                        data: Array(24).fill(0),
                        borderColor: '#40E0D0',
                        backgroundColor: 'rgba(64, 224, 208, 0.1)',
                        tension: 0.4,
                        fill: true
                    },
                    {
                        label: '실패',
                        data: Array(24).fill(0),
                        borderColor: '#f5576c',
                        backgroundColor: 'rgba(245, 87, 108, 0.1)',
                        tension: 0.4,
                        fill: true
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#f0f0f0'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    // 응답시간 차트
    const responseTimeCtx = document.getElementById('responseTimeChart');
    if (responseTimeCtx) {
        responseTimeChart = new Chart(responseTimeCtx, {
            type: 'bar',
            data: {
                labels: generateTimeLabels(),
                datasets: [
                    {
                        label: '응답시간 (ms)',
                        data: Array(24).fill(0),
                        backgroundColor: '#667eea',
                        borderRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#f0f0f0'
                        },
                        ticks: {
                            callback: function(value) {
                                return value + 'ms';
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }
}

// 사용량 데이터 로드
async function loadUsageData() {
    try {
        let url = `/auth/keys/stats/usage?period=${currentPeriod}`;

        // 사용자 지정 기간인 경우 날짜 파라미터 추가
        if (currentPeriod === 'custom' && customStartDate && customEndDate) {
            url += `&startDate=${customStartDate}&endDate=${customEndDate}`;
        }

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error('사용량 데이터를 불러오는데 실패했습니다');
        }

        const data = await response.json();

        // 통계 업데이트
        updateStatistics(data.statistics);

        // API 사용량 테이블 업데이트
        updateAPIUsageTable(data.apiStatistics);

        // 차트 업데이트
        updateCharts(data);

        // 트래픽 히트맵 업데이트
        updateTrafficHeatmap(data.hourlyTraffic);

    } catch (error) {
        console.error('Error:', error);
        // 에러 발생시 기본값으로 표시
        document.getElementById('totalCalls').textContent = '-';
        document.getElementById('successCalls').textContent = '-';
        document.getElementById('failureCalls').textContent = '-';
        document.getElementById('avgResponseTime').textContent = '-';

        const emptyData = {
            statistics: {
                totalCalls: 0,
                successCalls: 0,
                failureCalls: 0,
                successRate: 0,
                avgResponseTime: 0,
            },
            apiStatistics: [],
            hourlyTraffic: []
        };
        updateAPIUsageTable(emptyData.apiStatistics);
        updateCharts(emptyData);
        updateTrafficHeatmap(emptyData.hourlyTraffic);
    }
}

// 통계 업데이트
function updateStatistics(stats) {
    document.getElementById('totalCalls').textContent = formatNumber(stats.totalCalls);
    document.getElementById('successCalls').textContent = formatNumber(stats.successCalls);
    document.getElementById('failureCalls').textContent = formatNumber(stats.failureCalls);
    document.getElementById('avgResponseTime').textContent = stats.avgResponseTime + 'ms';

    // 성공률/실패율 업데이트
    const successRateEl = document.getElementById('successRate');
    const failureRateEl = document.getElementById('failureRate');

    if (successRateEl) {
        const successRateSpan = successRateEl.querySelector('span:first-child');
        if (successRateSpan) {
            successRateSpan.textContent = stats.successRate.toFixed(1) + '%';
        }
        successRateEl.className = 'stat-change ' + (stats.successRate >= 95 ? 'positive' : stats.successRate >= 80 ? 'neutral' : 'negative');
    }

    if (failureRateEl) {
        const failureRate = stats.totalCalls > 0 ? ((stats.failureCalls / stats.totalCalls) * 100).toFixed(1) : 0;
        const failureRateSpan = failureRateEl.querySelector('span:first-child');
        if (failureRateSpan) {
            failureRateSpan.textContent = failureRate + '%';
        }
        failureRateEl.className = 'stat-change ' + (failureRate < 5 ? 'positive' : failureRate < 20 ? 'neutral' : 'negative');
    }
}

// 차트 업데이트
function updateCharts(data) {
    // 시간별 데이터 생성
    const hourlyData = Array(24).fill(0);
    const hourlySuccess = Array(24).fill(0);
    const hourlyFailure = Array(24).fill(0);
    const hourlyResponseTime = Array(24).fill(0);

    data.hourlyTraffic.forEach(traffic => {
        hourlyData[traffic.hour] = traffic.calls;
    });

    data.apiStatistics.forEach(api => {
        // 각 API의 통계를 시간별로 분배 (단순화)
        data.hourlyTraffic.forEach(traffic => {
            const ratio = traffic.calls / data.statistics.totalCalls || 0;
            hourlySuccess[traffic.hour] += Math.round(api.success * ratio);
            hourlyFailure[traffic.hour] += Math.round(api.failure * ratio);
            hourlyResponseTime[traffic.hour] = Math.max(hourlyResponseTime[traffic.hour], api.avgResponseTime);
        });
    });

    // 호출 추이 차트 업데이트
    if (callTrendChart) {
        callTrendChart.data.datasets[0].data = hourlySuccess;
        callTrendChart.data.datasets[1].data = hourlyFailure;
        callTrendChart.update();
    }

    // 응답시간 차트 업데이트
    if (responseTimeChart) {
        responseTimeChart.data.datasets[0].data = hourlyResponseTime;
        responseTimeChart.update();
    }
}

// API 사용량 테이블 업데이트
function updateAPIUsageTable(data) {
    const tbody = document.getElementById('apiUsageTableBody');

    if (data.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-table-cell">
                    <div class="empty-state">
                        <div class="empty-icon">📊</div>
                        <p>아직 사용량 데이터가 없습니다</p>
                        <small>API를 호출하면 통계가 여기에 표시됩니다</small>
                    </div>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = data.map(api => `
        <tr>
            <td>
                <div class="api-name">${escapeHtml(api.apiPath)}</div>
            </td>
            <td>
                <span class="method-badge ${api.method}">${api.method}</span>
            </td>
            <td>${formatNumber(api.calls)}</td>
            <td style="color: #2e7d32; font-weight: 600;">${formatNumber(api.success)}</td>
            <td style="color: #c62828; font-weight: 600;">${formatNumber(api.failure)}</td>
            <td>
                <span class="success-rate ${getSuccessRateClass(api.successRate)}">
                    ${api.successRate.toFixed(1)}%
                </span>
            </td>
            <td>${api.avgResponseTime}ms</td>
        </tr>
    `).join('');
}

// 트래픽 히트맵 생성
function generateTrafficHeatmap() {
    updateTrafficHeatmap([]);
}

// 트래픽 히트맵 업데이트
function updateTrafficHeatmap(hourlyTraffic) {
    const heatmapContainer = document.getElementById('trafficHeatmap');

    // 24시간 데이터 배열 생성
    const hourlyData = Array(24).fill(0);
    hourlyTraffic.forEach(traffic => {
        hourlyData[traffic.hour] = traffic.calls;
    });

    const maxValue = Math.max(...hourlyData, 1);

    const hours = [];
    for (let i = 0; i < 24; i++) {
        const value = hourlyData[i];
        const level = getTrafficLevel(value, maxValue);

        hours.push(`
            <div class="traffic-hour level-${level}" title="${i}시: ${value}회">
                <div class="traffic-hour-label">${i}시</div>
                <div class="traffic-hour-value">${value}</div>
            </div>
        `);
    }

    heatmapContainer.innerHTML = hours.join('');
}

// 트래픽 레벨 계산
function getTrafficLevel(value, maxValue) {
    if (value === 0) return 0;
    const ratio = value / maxValue;
    if (ratio < 0.2) return 1;
    if (ratio < 0.4) return 2;
    if (ratio < 0.6) return 3;
    if (ratio < 0.8) return 4;
    return 5;
}

// 성공률 클래스
function getSuccessRateClass(rate) {
    if (rate >= 95) return 'high';
    if (rate >= 80) return 'medium';
    return 'low';
}

// 시간 레이블 생성
function generateTimeLabels() {
    const labels = [];
    for (let i = 0; i < 24; i++) {
        labels.push(`${i}시`);
    }
    return labels;
}

// 임시 데이터 생성
function generateMockData(count, min, max) {
    const data = [];
    for (let i = 0; i < count; i++) {
        data.push(Math.floor(Math.random() * (max - min + 1)) + min);
    }
    return data;
}

// 숫자 포맷팅
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// API 정렬 변경
document.getElementById('apiSortBy')?.addEventListener('change', function() {
    // TODO: 정렬 로직 구현
    loadUsageData();
});
