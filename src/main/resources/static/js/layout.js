// 레이아웃 관련 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 요소 가져오기
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const sidebarToggle = document.getElementById('sidebarToggle');

    // 모바일 메뉴 토글
    if (mobileMenuBtn) {
        mobileMenuBtn.addEventListener('click', function() {
            toggleSidebar();
        });
    }

    // 사이드바 닫기 버튼
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            closeSidebar();
        });
    }

    // 오버레이 클릭 시 사이드바 닫기
    if (overlay) {
        overlay.addEventListener('click', function() {
            closeSidebar();
        });
    }

    // 사이드바 토글 함수
    function toggleSidebar() {
        if (sidebar && overlay) {
            sidebar.classList.toggle('active');
            overlay.classList.toggle('active');

            // body 스크롤 방지
            if (sidebar.classList.contains('active')) {
                document.body.style.overflow = 'hidden';
            } else {
                document.body.style.overflow = '';
            }
        }
    }

    // 사이드바 닫기 함수
    function closeSidebar() {
        if (sidebar && overlay) {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    }

    // ESC 키로 사이드바 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeSidebar();
        }
    });

    // 윈도우 리사이즈 처리
    let resizeTimer;
    window.addEventListener('resize', function() {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function() {
            // 데스크톱 크기로 변경 시 사이드바 상태 초기화
            if (window.innerWidth > 768) {
                closeSidebar();
            }
        }, 250);
    });

    // 현재 페이지 네비게이션 활성화 (추가 보강)
    highlightCurrentNav();
});

// 현재 페이지 네비게이션 하이라이트
function highlightCurrentNav() {
    const currentPath = window.location.pathname;
    const navItems = document.querySelectorAll('.nav-item');

    navItems.forEach(item => {
        const page = item.getAttribute('data-page');
        const href = item.getAttribute('href');

        // 정확한 경로 매칭
        if (currentPath === href) {
            item.classList.add('active');
        }
        // data-page 속성으로 매칭
        else if (page && currentPath.includes(page)) {
            item.classList.add('active');
        }
    });
}

// 페이지 로드 애니메이션
function animatePageLoad() {
    const pageContainer = document.querySelector('.page-container');
    if (pageContainer) {
        pageContainer.style.opacity = '0';
        pageContainer.style.transform = 'translateY(20px)';

        setTimeout(() => {
            pageContainer.style.transition = 'all 0.5s ease';
            pageContainer.style.opacity = '1';
            pageContainer.style.transform = 'translateY(0)';
        }, 100);
    }
}

// 페이지 로드 시 애니메이션 실행
document.addEventListener('DOMContentLoaded', animatePageLoad);
