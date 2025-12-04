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

    // API 문서 서브메뉴 로드 (모든 페이지에서 로드)
    loadAPICategories();

    // 저장된 네비게이션 상태가 있으면 먼저 복원
    restoreNavigationState();
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

// 서브메뉴 토글
function toggleSubmenu(event) {
    event.preventDefault();
    event.stopPropagation();

    const navItem = event.currentTarget;
    const submenu = navItem.nextElementSibling;
    const page = navItem.getAttribute('data-page');

    navItem.classList.toggle('expanded');
    if (submenu) {
        submenu.classList.toggle('expanded');
    }

    // localStorage에 상태 저장
    if (page) {
        const isExpanded = navItem.classList.contains('expanded');
        localStorage.setItem(`nav-${page}-expanded`, isExpanded);
    }

    // 서브메뉴가 비어있으면 API 목록 로드 시도
    if (submenu && submenu.innerHTML.trim() === '') {
        loadAPICategories();
    }
}

// API 카테고리 목록 로드 및 서브메뉴 생성
async function loadAPICategories() {
    try {
        const response = await fetch('/web/docs/list');
        if (!response.ok) return;

        const apis = await response.json();

        // 카테고리별로 그룹화
        const categorizedAPIs = apis.reduce((acc, api) => {
            if (!acc[api.category]) {
                acc[api.category] = [];
            }
            acc[api.category].push(api);
            return acc;
        }, {});

        const submenuContainer = document.getElementById('docsSubmenu');
        if (submenuContainer && Object.keys(categorizedAPIs).length > 0) {
            submenuContainer.innerHTML = Object.entries(categorizedAPIs).map(([category, categoryAPIs]) => {
                const categoryId = category.toLowerCase().replace(/\s+/g, '-');

                return `
                    <div class="nav-submenu-category">
                        <div class="nav-submenu-category-title" onclick="toggleSubCategory(event, '${categoryId}')">
                            <svg class="submenu-category-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6 9 12 15 18 9"></polyline>
                            </svg>
                            <span>${escapeHtml(category)}</span>
                        </div>
                        <div class="nav-submenu-apis" id="subcategory-${categoryId}">
                            ${categoryAPIs.map(api => {
                                const apiId = getAPIId(api);
                                return `
                                    <a href="/docs#${apiId}" class="nav-submenu-api-item" onclick="navigateToAPI('${apiId}')">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <circle cx="12" cy="12" r="1"></circle>
                                        </svg>
                                        ${escapeHtml(api.title)}
                                    </a>
                                `;
                            }).join('')}
                        </div>
                    </div>
                `;
            }).join('');

            // 저장된 상태 복원
            restoreNavigationState();
        }
    } catch (error) {
        console.error('Failed to load API categories:', error);
    }
}

// 네비게이션 상태 복원
function restoreNavigationState() {
    // 메인 서브메뉴 상태 복원
    const navItems = document.querySelectorAll('.nav-item.has-submenu');
    navItems.forEach(navItem => {
        const page = navItem.getAttribute('data-page');
        if (page) {
            const isExpanded = localStorage.getItem(`nav-${page}-expanded`) === 'true';
            const submenu = navItem.nextElementSibling;

            if (isExpanded) {
                navItem.classList.add('expanded');
                if (submenu) {
                    submenu.classList.add('expanded');
                }
            }
        }
    });

    // 서브카테고리 상태 복원
    const subCategories = document.querySelectorAll('.nav-submenu-category-title');
    subCategories.forEach(categoryTitle => {
        const apisContainer = categoryTitle.nextElementSibling;
        if (apisContainer && apisContainer.id) {
            const categoryId = apisContainer.id.replace('subcategory-', '');
            const isExpanded = localStorage.getItem(`subcategory-${categoryId}-expanded`) === 'true';

            if (isExpanded) {
                categoryTitle.classList.add('expanded');
                apisContainer.classList.add('expanded');
            }
        }
    });
}

// 서브카테고리 토글
function toggleSubCategory(event, categoryId) {
    event.preventDefault();
    event.stopPropagation();

    const categoryTitle = event.currentTarget;
    const apisContainer = document.getElementById(`subcategory-${categoryId}`);

    categoryTitle.classList.toggle('expanded');
    if (apisContainer) {
        apisContainer.classList.toggle('expanded');
    }

    // localStorage에 상태 저장
    const isExpanded = categoryTitle.classList.contains('expanded');
    localStorage.setItem(`subcategory-${categoryId}-expanded`, isExpanded);
}

// API ID 생성 (layout.js에서도 필요)
function getAPIId(api) {
    return `${api.method.toLowerCase()}-${api.url.replace(/[^a-z0-9]/gi, '-')}`;
}

// API로 네비게이션
function navigateToAPI(apiId) {
    // 펼쳐진 카테고리 상태 저장
    const expandedCategories = Array.from(document.querySelectorAll('.nav-submenu-category-title.expanded'))
        .map(el => el.parentElement.querySelector('.nav-submenu-apis').id.replace('subcategory-', ''));
    sessionStorage.setItem('expandedCategories', JSON.stringify(expandedCategories));

    // 타겟 API 저장
    sessionStorage.setItem('targetAPI', apiId);

    if (window.location.pathname !== '/docs') {
        window.location.href = `/docs#${apiId}`;
    } else {
        window.location.hash = apiId;
        setTimeout(() => {
            const element = document.getElementById(apiId);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        }, 100);
    }
}

// 카테고리로 네비게이션
function navigateToCategory(categoryId) {
    if (window.location.pathname !== '/docs') {
        window.location.href = `/docs#${categoryId}`;
    } else {
        window.location.hash = categoryId;
        // API 문서 페이지의 scrollToAPI 함수 호출 (있다면)
        if (typeof scrollToAPI === 'function') {
            setTimeout(() => {
                const element = document.querySelector(`[data-category="${categoryId}"]`);
                if (element) {
                    element.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            }, 100);
        }
    }
}
