// pages/MyPage.jsx
import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

const MyPage = () => {
    const [company, setCompany] = useState(null);
    const [loading, setLoading] = useState(true);
    const [activeMenu, setActiveMenu] = useState('profile');
    const [editMode, setEditMode] = useState(false);
    const [form, setForm] = useState({});

    useEffect(() => {
        // 로그인 상태 확인
        const loginCompanyId = sessionStorage.getItem('loginCompanyId');
        if (!loginCompanyId) {
            window.location.href = "/";
            return;
        }

        // API 호출하여 사용자 정보 가져오기
        fetch('/api/me', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        })
            .then((res) => {
                if (!res.ok) {
                    throw new Error('Failed to fetch user data');
                }
                return res.json();
            })
            .then((data) => {
                setCompany(data);
                setForm(data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                alert('사용자 정보를 불러오는데 실패했습니다.');
                window.location.href = "/";
            });
    }, []);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async () => {
        try {
            const res = await fetch('/api/users/me', {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(form),
            });

            if (!res.ok) {
                throw new Error('Failed to update profile');
            }

            const updated = await res.json();
            setCompany(updated);
            setEditMode(false);
            alert('프로필이 성공적으로 업데이트되었습니다.');
        } catch (err) {
            console.error(err);
            alert('프로필 업데이트에 실패했습니다.');
        }
    };

    if (loading) {
        return (
            <div className="bg-gray-50 font-sans min-h-screen">
                <Navbar />
                <div className="pt-20 flex justify-center items-center h-screen">
                    <div className="text-2xl font-semibold text-gray-600">Loading...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="bg-gray-50 font-sans min-h-screen">
            <Navbar />

            <div className="pt-20 container mx-auto px-4 py-10">
                <div className="flex flex-col md:flex-row gap-8">
                    {/* 사이드바 */}
                    <div className="w-full md:w-64 bg-white rounded-lg shadow-sm p-6 h-fit">
                        <div className="flex flex-col items-center mb-8 pb-6 border-b border-gray-200">
                            <div className="w-24 h-24 rounded-full bg-gray-200 mb-4 flex items-center justify-center text-gray-400 overflow-hidden">
                                <i className="fas fa-user text-4xl"></i>
                            </div>
                            <div className="font-semibold text-lg mb-1">{company.userName}</div>
                            <div className="text-gray-500 text-sm mb-4">{company.email}</div>
                            <button
                                onClick={() => {
                                    setActiveMenu('profile');
                                    setEditMode(true);
                                }}
                                className="text-sm bg-blue-50 text-blue-600 px-4 py-2 rounded-md hover:bg-blue-100 transition duration-300"
                            >
                                프로필 편집
                            </button>
                        </div>

                        <ul className="space-y-2">
                            <li>
                                <button
                                    onClick={() => setActiveMenu('profile')}
                                    className={`w-full text-left px-3 py-2 rounded-md flex items-center ${activeMenu === 'profile' ? 'bg-blue-50 text-blue-600' : 'text-gray-700 hover:bg-gray-100'}`}
                                >
                                    <i className="fas fa-user-circle mr-3"></i> 내 정보
                                </button>
                            </li>
                            <li>
                                <button
                                    onClick={() => setActiveMenu('products')}
                                    className={`w-full text-left px-3 py-2 rounded-md flex items-center ${activeMenu === 'products' ? 'bg-blue-50 text-blue-600' : 'text-gray-700 hover:bg-gray-100'}`}
                                >
                                    <i className="fas fa-box mr-3"></i> 내 상품
                                </button>
                            </li>
                            <li>
                                <button
                                    onClick={() => setActiveMenu('transactions')}
                                    className={`w-full text-left px-3 py-2 rounded-md flex items-center ${activeMenu === 'transactions' ? 'bg-blue-50 text-blue-600' : 'text-gray-700 hover:bg-gray-100'}`}
                                >
                                    <i className="fas fa-exchange-alt mr-3"></i> 거래 내역
                                    <span className="ml-auto bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">3</span>
                                </button>
                            </li>
                            <li>
                                <button
                                    onClick={() => setActiveMenu('inquiries')}
                                    className={`w-full text-left px-3 py-2 rounded-md flex items-center ${activeMenu === 'inquiries' ? 'bg-blue-50 text-blue-600' : 'text-gray-700 hover:bg-gray-100'}`}
                                >
                                    <i className="fas fa-comment-alt mr-3"></i> 문의 내역
                                </button>
                            </li>
                            <li>
                                <button
                                    onClick={() => setActiveMenu('ads')}
                                    className={`w-full text-left px-3 py-2 rounded-md flex items-center ${activeMenu === 'ads' ? 'bg-blue-50 text-blue-600' : 'text-gray-700 hover:bg-gray-100'}`}
                                >
                                    <i className="fas fa-ad mr-3"></i> 광고 관리
                                </button>
                            </li>
                            <li>
                                <button
                                    onClick={() => setActiveMenu('reviews')}
                                    className={`w-full text-left px-3 py-2 rounded-md flex items-center ${activeMenu === 'reviews' ? 'bg-blue-50 text-blue-600' : 'text-gray-700 hover:bg-gray-100'}`}
                                >
                                    <i className="fas fa-star mr-3"></i> 리뷰 관리
                                </button>
                            </li>
                            <li>
                                <button
                                    onClick={() => setActiveMenu('settings')}
                                    className={`w-full text-left px-3 py-2 rounded-md flex items-center ${activeMenu === 'settings' ? 'bg-blue-50 text-blue-600' : 'text-gray-700 hover:bg-gray-100'}`}
                                >
                                    <i className="fas fa-cog mr-3"></i> 계정 설정
                                </button>
                            </li>
                        </ul>
                    </div>

                    {/* 메인 컨텐츠 */}
                    <div className="flex-1">
                        {/* 내 정보 섹션 */}
                        {activeMenu === 'profile' && (
                            <div className="bg-white rounded-lg shadow-sm overflow-hidden mb-6">
                                <div className="flex justify-between items-center px-6 py-4 border-b border-gray-200">
                                    <h2 className="text-lg font-semibold">내 정보</h2>
                                    <button
                                        onClick={() => setEditMode(!editMode)}
                                        className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                                    >
                                        {editMode ? '취소' : '수정하기'}
                                    </button>
                                </div>

                                <div className="p-6">
                                    {editMode ? (
                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                            <div>
                                                <label htmlFor="userName" className="block text-sm font-medium text-gray-700 mb-1">이름</label>
                                                <input
                                                    type="text"
                                                    id="userName"
                                                    name="userName"
                                                    value={form.userName || ''}
                                                    onChange={handleChange}
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                />
                                            </div>
                                            <div>
                                                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">이메일</label>
                                                <input
                                                    type="email"
                                                    id="email"
                                                    name="email"
                                                    value={form.email || ''}
                                                    disabled
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100"
                                                />
                                            </div>
                                            <div>
                                                <label htmlFor="userPhone" className="block text-sm font-medium text-gray-700 mb-1">연락처</label>
                                                <input
                                                    type="text"
                                                    id="userPhone"
                                                    name="userPhone"
                                                    value={form.userPhone || ''}
                                                    onChange={handleChange}
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                />
                                            </div>
                                            <div>
                                                <label htmlFor="companyName" className="block text-sm font-medium text-gray-700 mb-1">회사명</label>
                                                <input
                                                    type="text"
                                                    id="companyName"
                                                    name="companyName"
                                                    value={form.companyName || ''}
                                                    onChange={handleChange}
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                />
                                            </div>
                                            <div>
                                                <label htmlFor="businessNumber" className="block text-sm font-medium text-gray-700 mb-1">사업자 번호</label>
                                                <input
                                                    type="text"
                                                    id="businessNumber"
                                                    name="businessNumber"
                                                    value={form.businessNumber || ''}
                                                    onChange={handleChange}
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                />
                                            </div>
                                            <div>
                                                <label htmlFor="nickname" className="block text-sm font-medium text-gray-700 mb-1">닉네임</label>
                                                <input
                                                    type="text"
                                                    id="nickname"
                                                    name="nickname"
                                                    value={form.nickname || ''}
                                                    onChange={handleChange}
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                />
                                            </div>
                                            <div>
                                                <label htmlFor="homepage" className="block text-sm font-medium text-gray-700 mb-1">홈페이지</label>
                                                <input
                                                    type="text"
                                                    id="homepage"
                                                    name="homepage"
                                                    value={form.homepage || ''}
                                                    onChange={handleChange}
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                />
                                            </div>
                                            <div className="md:col-span-2 mt-4">
                                                <button
                                                    onClick={handleSubmit}
                                                    className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition duration-300"
                                                >
                                                    저장하기
                                                </button>
                                            </div>
                                        </div>
                                    ) : (
                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-y-4">
                                            <div>
                                                <div className="text-sm text-gray-500 mb-1">이름</div>
                                                <div>{company.userName}</div>
                                            </div>
                                            <div>
                                                <div className="text-sm text-gray-500 mb-1">이메일</div>
                                                <div>{company.email}</div>
                                            </div>
                                            <div>
                                                <div className="text-sm text-gray-500 mb-1">연락처</div>
                                                <div>{company.userPhone}</div>
                                            </div>
                                            <div>
                                                <div className="text-sm text-gray-500 mb-1">회사명</div>
                                                <div>{company.companyName}</div>
                                            </div>
                                            <div>
                                                <div className="text-sm text-gray-500 mb-1">사업자 번호</div>
                                                <div>{company.businessNumber}</div>
                                            </div>
                                            <div>
                                                <div className="text-sm text-gray-500 mb-1">닉네임</div>
                                                <div>{company.nickname || '-'}</div>
                                            </div>
                                            <div>
                                                <div className="text-sm text-gray-500 mb-1">홈페이지</div>
                                                <div>{company.homepage || '-'}</div>
                                            </div>
                                            <div>
                                                <div className="text-sm text-gray-500 mb-1">가입일</div>
                                                <div>{company.createdDate || "없음"}</div>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>
                        )}

                        {/* 내 상품 섹션 */}
                        {activeMenu === 'products' && (
                            <div className="bg-white rounded-lg shadow-sm overflow-hidden mb-6">
                                <div className="flex justify-between items-center px-6 py-4 border-b border-gray-200">
                                    <h2 className="text-lg font-semibold">내 상품</h2>
                                    <button className="text-blue-600 hover:text-blue-800 text-sm font-medium">
                                        + 상품 등록하기
                                    </button>
                                </div>

                                <div className="p-6">
                                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                                        {/* 상품 카드 */}
                                        <div className="border border-gray-200 rounded-lg overflow-hidden">
                                            <div className="h-40 bg-gray-100 flex items-center justify-center">
                                                <i className="fas fa-image text-gray-400 text-4xl"></i>
                                            </div>
                                            <div className="p-4">
                                                <h3 className="font-medium text-gray-800 mb-2 h-12 overflow-hidden">비즈니스 컨설팅 서비스</h3>
                                                <div className="text-sm text-gray-500 mb-4">카테고리: 컨설팅</div>
                                                <div className="flex justify-between">
                                                    <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">수정</button>
                                                    <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">삭제</button>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="border border-gray-200 rounded-lg overflow-hidden">
                                            <div className="h-40 bg-gray-100 flex items-center justify-center">
                                                <i className="fas fa-image text-gray-400 text-4xl"></i>
                                            </div>
                                            <div className="p-4">
                                                <h3 className="font-medium text-gray-800 mb-2 h-12 overflow-hidden">마케팅 전략 분석 리포트</h3>
                                                <div className="text-sm text-gray-500 mb-4">카테고리: 마케팅</div>
                                                <div className="flex justify-between">
                                                    <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">수정</button>
                                                    <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">삭제</button>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="border border-gray-200 rounded-lg overflow-hidden">
                                            <div className="h-40 bg-gray-100 flex items-center justify-center">
                                                <i className="fas fa-image text-gray-400 text-4xl"></i>
                                            </div>
                                            <div className="p-4">
                                                <h3 className="font-medium text-gray-800 mb-2 h-12 overflow-hidden">시장 조사 데이터 분석</h3>
                                                <div className="text-sm text-gray-500 mb-4">카테고리: 리서치</div>
                                                <div className="flex justify-between">
                                                    <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">수정</button>
                                                    <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">삭제</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* 거래 내역 섹션 */}
                        {activeMenu === 'transactions' && (
                            <div className="bg-white rounded-lg shadow-sm overflow-hidden mb-6">
                                <div className="px-6 py-4 border-b border-gray-200">
                                    <h2 className="text-lg font-semibold">거래 내역</h2>
                                </div>

                                <div className="overflow-x-auto">
                                    <table className="min-w-full divide-y divide-gray-200">
                                        <thead className="bg-gray-50">
                                        <tr>
                                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상품명</th>
                                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">거래상대</th>
                                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">거래일</th>
                                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상태</th>
                                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">관리</th>
                                        </tr>
                                        </thead>
                                        <tbody className="bg-white divide-y divide-gray-200">
                                        <tr>
                                            <td className="px-6 py-4 whitespace-nowrap">비즈니스 컨설팅 서비스</td>
                                            <td className="px-6 py-4 whitespace-nowrap">홍길동</td>
                                            <td className="px-6 py-4 whitespace-nowrap">2025.04.10</td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">진행중</span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <button className="text-xs bg-blue-50 text-blue-600 px-3 py-1 rounded hover:bg-blue-100">거래완료</button>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td className="px-6 py-4 whitespace-nowrap">마케팅 전략 분석 리포트</td>
                                            <td className="px-6 py-4 whitespace-nowrap">박서준</td>
                                            <td className="px-6 py-4 whitespace-nowrap">2025.04.05</td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">완료</span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">상세보기</button>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td className="px-6 py-4 whitespace-nowrap">시장 조사 데이터 분석</td>
                                            <td className="px-6 py-4 whitespace-nowrap">김영희</td>
                                            <td className="px-6 py-4 whitespace-nowrap">2025.03.28</td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">취소됨</span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">상세보기</button>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        )}

                        {/* 문의 내역 섹션 */}
                        {activeMenu === 'inquiries' && (
                            <div className="bg-white rounded-lg shadow-sm overflow-hidden mb-6">
                                <div className="px-6 py-4 border-b border-gray-200">
                                    <h2 className="text-lg font-semibold">문의 내역</h2>
                                </div>

                                <div className="divide-y divide-gray-100">
                                    <div className="p-6">
                                        <div className="flex justify-between items-start mb-2">
                                            <div className="font-medium">디자인스튜디오</div>
                                            <div className="text-sm text-gray-500">2025.04.12</div>
                                        </div>
                                        <div className="text-blue-600 font-medium mb-2">예산: 500,000원</div>
                                        <p className="text-gray-700 mb-4">
                                            웹사이트 리뉴얼 작업에 대해 문의드립니다. 현재 저희 사이트가 모바일 최적화가 되어있지 않아 반응형 디자인으로 리뉴얼하고 싶습니다. 약 10페이지 정도 되는 기업 소개 웹사이트입니다.
                                        </p>
                                        <button className="text-sm bg-blue-50 text-blue-600 px-4 py-1 rounded-md hover:bg-blue-100">답변 확인</button>
                                    </div>

                                    <div className="p-6">
                                        <div className="flex justify-between items-start mb-2">
                                            <div className="font-medium">마케팅에이전시</div>
                                            <div className="text-sm text-gray-500">2025.03.25</div>
                                        </div>
                                        <div className="text-blue-600 font-medium mb-2">예산: 1,000,000원</div>
                                        <p className="text-gray-700 mb-4">
                                            신규 제품 출시에 따른 마케팅 전략 수립 및 SNS 광고 운영 대행 문의드립니다. 5월 중순에 출시 예정인 제품으로, 사전 마케팅부터 출시 후 3개월간의 프로모션까지 포괄적인 마케팅 서비스가 필요합니다.
                                        </p>
                                        <button className="text-sm bg-blue-50 text-blue-600 px-4 py-1 rounded-md hover:bg-blue-100">답변 확인</button>
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* 광고 관리 섹션 */}
                        {activeMenu === 'ads' && (
                            <div className="bg-white rounded-lg shadow-sm overflow-hidden mb-6">
                                <div className="flex justify-between items-center px-6 py-4 border-b border-gray-200">
                                    <h2 className="text-lg font-semibold">광고 관리</h2>
                                    <button className="text-blue-600 hover:text-blue-800 text-sm font-medium">
                                        + 광고 등록하기
                                    </button>
                                </div>

                                <div className="p-6">
                                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                                        <div className="border border-gray-200 rounded-lg overflow-hidden">
                                            <div className="h-32 bg-gray-100 flex items-center justify-center">
                                                <i className="fas fa-image text-gray-400 text-4xl"></i>
                                            </div>
                                            <div className="p-4">
                                                <h3 className="font-medium text-gray-800 mb-2">비즈니스 컨설팅 서비스</h3>
                                                <div className="text-sm text-gray-500 mb-3">2025.04.01 ~ 2025.04.30</div>
                                                <div className="flex justify-between items-center">
                                                    <div className="text-green-600 text-xs font-medium">● 진행중</div>
                                                    <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">수정</button>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="border border-gray-200 rounded-lg overflow-hidden">
                                            <div className="h-32 bg-gray-100 flex items-center justify-center">
                                                <i className="fas fa-image text-gray-400 text-4xl"></i>
                                            </div>
                                            <div className="p-4">
                                                <h3 className="font-medium text-gray-800 mb-2">마케팅 전략 분석 리포트</h3>
                                                <div className="text-sm text-gray-500 mb-3">2025.05.01 ~ 2025.05.31</div>
                                                <div className="flex justify-between items-center">
                                                    <div className="text-gray-500 text-xs font-medium">● 예약됨</div>
                                                    <button className="text-xs bg-gray-100 text-gray-700 px-3 py-1 rounded hover:bg-gray-200">수정</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* 리뷰 관리 섹션 */}
                        {activeMenu === 'reviews' && (
                            <div className="bg-white rounded-lg shadow-sm overflow-hidden mb-6">
                                <div className="px-6 py-4 border-b border-gray-200">
                                    <h2 className="text-lg font-semibold">리뷰 관리</h2>
                                </div>

                                <div className="p-6">
                                    <div className="text-center py-8 text-gray-500">
                                        <div className="text-4xl mb-4">⭐</div>
                                        <p>아직 받은 리뷰가 없습니다.</p>
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* 계정 설정 섹션 */}
                        {activeMenu === 'settings' && (
                            <div className="bg-white rounded-lg shadow-sm overflow-hidden mb-6">
                                <div className="px-6 py-4 border-b border-gray-200">
                                    <h2 className="text-lg font-semibold">계정 설정</h2>
                                </div>

                                <div className="p-6">
                                    <form className="space-y-6">
                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                            <div>
                                                <label htmlFor="nickname" className="block text-sm font-medium text-gray-700 mb-1">닉네임</label>
                                                <input
                                                    type="text"
                                                    id="nickname"
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                    defaultValue={company.nickname || ''}
                                                />
                                            </div>
                                            <div>
                                                <label htmlFor="userPhone" className="block text-sm font-medium text-gray-700 mb-1">연락처</label>
                                                <input
                                                    type="text"
                                                    id="userPhone"
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                    defaultValue={company.userPhone || ''}
                                                />
                                            </div>
                                        </div>

                                        <div className="border-t border-gray-200 pt-6">
                                            <h3 className="text-lg font-medium mb-4">비밀번호 변경</h3>
                                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                                <div>
                                                    <label htmlFor="currentPassword" className="block text-sm font-medium text-gray-700 mb-1">현재 비밀번호</label>
                                                    <input
                                                        type="password"
                                                        id="currentPassword"
                                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                    />
                                                </div>
                                                <div className="md:col-span-2"></div>
                                                <div>
                                                    <label htmlFor="newPassword" className="block text-sm font-medium text-gray-700 mb-1">새 비밀번호</label>
                                                    <input
                                                        type="password"
                                                        id="newPassword"
                                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                    />
                                                </div>
                                                <div>
                                                    <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">새 비밀번호 확인</label>
                                                    <input
                                                        type="password"
                                                        id="confirmPassword"
                                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                    />
                                                </div>
                                            </div>
                                        </div>

                                        <div className="flex justify-end">
                                            <button
                                                type="submit"
                                                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition duration-300"
                                            >
                                                저장하기
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default MyPage;