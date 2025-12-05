document.addEventListener('DOMContentLoaded', async function() {
    await loadAnalyticsReports();
});

async function loadAnalyticsReports() {
    try {
        const response = await fetch('/web/analytics');
        if (!response.ok) {
            throw new Error('분석 보고서 목록을 불러오는데 실패했습니다');
        }

        const reports = await response.json();

        if (reports.length > 0) {
            const reportContainer = document.querySelector('.analytics-report-container');
            const collectingState = document.querySelector('.ai-collecting-state');
            const reportSelect = document.getElementById('reportSelect');
            const deleteBtn = document.getElementById('deleteReportBtn');

            reportContainer.style.display = 'block';
            collectingState.style.display = 'none';

            reports.forEach(report => {
                const option = document.createElement('option');
                option.value = report.id;
                option.textContent = `${report.searchStartDate} ~ ${report.searchEndDate}`;
                reportSelect.appendChild(option);
            });

            reportSelect.addEventListener('change', function() {
                if (this.value) {
                    loadReportHtml(this.value);
                    deleteBtn.style.display = 'block';
                } else {
                    document.getElementById('reportViewer').style.display = 'none';
                    deleteBtn.style.display = 'none';
                }
            });

            reportSelect.value = reports[0].id;
            loadReportHtml(reports[0].id);
            deleteBtn.style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function loadReportHtml(reportId) {
    try {
        const response = await fetch(`/web/analytics/${reportId}/html`);
        if (!response.ok) {
            throw new Error('분석 보고서를 불러오는데 실패했습니다');
        }

        const html = await response.text();
        const reportViewer = document.getElementById('reportViewer');
        const iframe = document.getElementById('reportIframe');

        iframe.srcdoc = html;
        reportViewer.style.display = 'block';

        iframe.onload = function() {
            try {
                const iframeDocument = iframe.contentDocument || iframe.contentWindow.document;
                const contentHeight = iframeDocument.documentElement.scrollHeight;
                iframe.style.height = contentHeight + 'px';
            } catch (e) {
                iframe.style.height = '800px';
            }
        };

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ ' + error.message);
    }
}

async function deleteCurrentReport() {
    const reportSelect = document.getElementById('reportSelect');
    const reportId = reportSelect.value;

    if (!reportId) {
        showToast('✗ 삭제할 보고서를 선택해주세요');
        return;
    }

    if (!confirm('이 분석 보고서를 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`/web/analytics/${reportId}`, {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error('분석 보고서 삭제에 실패했습니다');
        }

        showToast('✓ 분석 보고서가 삭제되었습니다');
        setTimeout(() => {
            window.location.reload();
        }, 1000);

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ ' + error.message);
    }
}
