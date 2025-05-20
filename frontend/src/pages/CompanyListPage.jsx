// src/pages/CompanyListPage.jsx
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

const CompanyListPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [companies, setCompanies] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedCategory, setSelectedCategory] = useState('');
    const [sortOption, setSortOption] = useState('recent');
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const itemsPerPage = 12;

    // URL 쿼리 파라미터 파싱
    const queryParams = new URLSearchParams(location.search);
    const searchQuery = queryParams.get('query');
    const categoryParam = queryParams.get('category');

    useEffect(() => {
        if (categoryParam) {
            setSelectedCategory(categoryParam);
        }

        fetchCategories();
        fetchCompanies();
    }, [categoryParam, searchQuery, selectedCategory, sortOption, currentPage]);

    const fetchCategories = async () => {
        try {
            // 카테고리 목록 가져오기 (API가 있다고 가정)
            const res = await fetch('/api/categories');
            if (res.ok) {
                const data = await res.json();
                setCategories(data);
            }
        } catch (error) {
            console.error('카테고리 로딩 중 오류:', error);
        }
    };

    const fetchCompanies = async () => {
        try {
            setLoading(true);

            // 쿼리 파라미터 구성
            let url = `/api/users?page=${currentPage}&limit=${itemsPerPage}`;

            if (searchQuery) {
                url += `&query=${encodeURIComponent(searchQuery)}`;
            }

            if (selectedCategory) {
                url += `&categoryId=${encodeURIComponent(selectedCategory)}`;
            }

            // 정렬 옵션 적용
            switch (sortOption) {
                case 'rating':
                    url += '&sort=rating&direction=desc';
                    break;
                case 'review':
                    url += '&sort=reviews&direction=desc';
                    break;
                default:
                    url += '&sort=createdAt&direction=desc';
                    break;
            }

            const res = await fetch(url);
            if (!res.ok) throw new Error('회사 정보를 불러오는데 실패했습니다');

            const data = await res.json();

            // 페이지네이션 정보가 포함된 응답인 경우
            if (data.items && data.totalItems) {
                setCompanies(data.items);
                setTotalPages(Math.ceil(data.totalItems / itemsPerPage));
            } else {
                // 단순 배열 응답인 경우
                setCompanies(data);
                setTotalPages(Math.ceil(data.length / itemsPerPage));
            }

            setLoading(false);
        } catch (error) {
            console.error('회사 로딩 중 오류:', error);
            setLoading(false);
        }
    };

    const handleCategoryClick = (categoryId) => {
        setSelectedCategory(categoryId === selectedCategory ? '' : categoryId);
        setCurrentPage(1);
    };

    const handleSortChange = (e) => {
        setSortOption(e.target.value);
        setCurrentPage(1);
    };

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
        window.scrollTo(0, 0);
    };

    const handleSearch = (e) => {
        e.preventDefault();
        const searchInput = e.target.elements.search.value;
        navigate(`/companies?query=${encodeURIComponent(searchInput)}`);
    };

    // 백엔드 연동이 안 되는 경우를 위한 임시 데이터 생성
    const createDummyCompanies = () => {
        if (companies.length === 0 && !loading) {
            const dummyCompanies = Array.from({ length: 12 }, (_, i) => ({
                id: `dummy-${i}`,
                companyName: `샘플 회사 ${i + 1}`,
                nickname: `샘플 닉네임 ${i + 1}`,
                email: `sample${i + 1}@example.com`,
                categoryId: '샘플 카테고리',
                profileAttachmentUrl: null,
                rating: Math.random() * 5,
                reviewCount: Math.floor(Math.random() * 50),
                createdAt: new Date().toISOString()
            }));

            setCompanies(dummyCompanies);
            setTotalPages(1);
        }
    };

    // 로딩이 완료되었지만 회사가 없는 경우 더미 데이터 생성 (개발용)
    // 실제 배포 시에는 이 부분을 제거해야 함
    useEffect(() => {
        if (!loading && companies.length === 0) {
            createDummyCompanies();
        }
    }, [loading, companies.length]);

    return (
        <div className="bg-gray-50 min-h-screen">
            <Navbar />

            <div className="container mx-auto px-4 pt-8 pb-16">
                <div className="flex flex-col md:flex-row gap-6">
                    {/* 좌측 필터 영역 */}
                    <div className="md:w-1/4 lg:w-1/5">
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <h3 className="text-lg font-bold mb-4">검색</h3>
                            <form onSubmit={handleSearch}>
                                <div className="relative">
                                    <input
                                        type="text"
                                        name="search"
                                        defaultValue={searchQuery || ''}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        placeholder="회사명 입력..."
                                    />
                                    <button
                                        type="submit"
                                        className="absolute right-2 top-2 text-gray-500"
                                    >
                                        <i className="fas fa-search"></i>
                                    </button>
                                </div>
                            </form>
                        </div>

                        <div className="bg-white rounded-lg shadow-sm p-6">
                            <h3 className="text-lg font-bold mb-4">카테고리</h3>
                            <ul className="space-y-2">
                                {categories.length > 0 ? (
                                    categories.map(category => (
                                        <li key={category.id}>
                                            <button
                                                className={`w-full text-left py-2 px-3 rounded-md transition-colors ${selectedCategory === category.id ? 'bg-blue-50 text-blue-600' : 'hover:bg-gray-100'}`}
                                                onClick={() => handleCategoryClick(category.id)}
                                            >
                                                {category.name}
                                            </button>
                                        </li>
                                    ))
                                ) : (
                                    // 샘플 카테고리
                                    ['IT 개발', '디자인', '마케팅', '번역', '콘텐츠 제작', '비즈니스 컨설팅'].map((cat, idx) => (
                                        <li key={idx}>
                                            <button
                                                className={`w-full text-left py-2 px-3 rounded-md transition-colors ${selectedCategory === cat ? 'bg-blue-50 text-blue-600' : 'hover:bg-gray-100'}`}
                                                onClick={() => handleCategoryClick(cat)}
                                            >
                                                {cat}
                                            </button>
                                        </li>
                                    ))
                                )}
                            </ul>
                        </div>
                    </div>

                    {/* 우측 회사 목록 영역 */}
                    <div className="md:w-3/4 lg:w-4/5">
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
                                <h1 className="text-2xl font-bold">
                                    {searchQuery
                                        ? `'${searchQuery}' 검색 결과`
                                        : (selectedCategory
                                            ? `${selectedCategory} 카테고리`
                                            : '모든 기업')}
                                </h1>

                                <div className="mt-3 sm:mt-0">
                                    <select
                                        value={sortOption}
                                        onChange={handleSortChange}
                                        className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    >
                                        <option value="recent">최신순</option>
                                        <option value="rating">평점순</option>
                                        <option value="review">리뷰 많은순</option>
                                    </select>
                                </div>
                            </div>

                            {loading ? (
                                <div className="py-20 text-center">
                                    <p className="text-gray-500">기업을 불러오는 중입니다...</p>
                                </div>
                            ) : (
                                <>
                                    {companies.length > 0 ? (
                                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                            {companies.map(company => (
                                                <div key={company.id} className="bg-white rounded-lg overflow-hidden shadow-sm border border-gray-100 hover:shadow-md transition duration-300">
                                                    <a href={`/companies/${company.id}`}>
                                                        <div className="h-48 bg-gray-200">
                                                            <img
                                                                src={company.profileAttachmentUrl || "/assets/default-company.png"}
                                                                alt={company.companyName}
                                                                className="w-full h-full object-cover"
                                                            />
                                                        </div>
                                                        <div className="p-4">
                                                            <h3 className="font-medium text-gray-800 mb-1">
                                                                {company.companyName}
                                                            </h3>
                                                            <p className="text-sm text-gray-500 mb-2">
                                                                {company.nickname || company.companyName}
                                                            </p>
                                                            <div className="flex items-center mb-2">
                                                                <div className="text-yellow-400 flex items-center text-sm">
                                                                    <i className="fas fa-star mr-1"></i>
                                                                    <span>{company.rating?.toFixed(1) || '0.0'}</span>
                                                                </div>
                                                                <span className="text-gray-400 text-xs ml-2">
                                                                    ({company.reviewCount || 0}개 리뷰)
                                                                </span>
                                                            </div>
                                                            {company.categoryId && (
                                                                <div className="mt-2">
                                                                    <span className="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full">
                                                                        {company.categoryId}
                                                                    </span>
                                                                </div>
                                                            )}
                                                        </div>
                                                    </a>
                                                </div>
                                            ))}
                                        </div>
                                    ) : (
                                        <div className="py-20 text-center">
                                            <p className="text-gray-500">검색 결과가 없습니다.</p>
                                        </div>
                                    )}

                                    {/* 페이지네이션 */}
                                    {totalPages > 1 && (
                                        <div className="flex justify-center mt-10">
                                            <div className="flex space-x-2">
                                                <button
                                                    onClick={() => handlePageChange(1)}
                                                    disabled={currentPage === 1}
                                                    className={`px-3 py-1 rounded ${currentPage === 1 ? 'text-gray-400' : 'text-gray-700 hover:bg-gray-100'}`}
                                                >
                                                    <i className="fas fa-angle-double-left"></i>
                                                </button>
                                                <button
                                                    onClick={() => handlePageChange(currentPage - 1)}
                                                    disabled={currentPage === 1}
                                                    className={`px-3 py-1 rounded ${currentPage === 1 ? 'text-gray-400' : 'text-gray-700 hover:bg-gray-100'}`}
                                                >
                                                    <i className="fas fa-angle-left"></i>
                                                </button>

                                                {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                                                    const pageNum = currentPage <= 3
                                                        ? i + 1
                                                        : currentPage >= totalPages - 2
                                                            ? totalPages - 4 + i
                                                            : currentPage - 2 + i;

                                                    if (pageNum > 0 && pageNum <= totalPages) {
                                                        return (
                                                            <button
                                                                key={pageNum}
                                                                onClick={() => handlePageChange(pageNum)}
                                                                className={`w-8 h-8 rounded ${pageNum === currentPage ? 'bg-blue-600 text-white' : 'text-gray-700 hover:bg-gray-100'}`}
                                                            >
                                                                {pageNum}
                                                            </button>
                                                        );
                                                    }
                                                    return null;
                                                })}

                                                <button
                                                    onClick={() => handlePageChange(currentPage + 1)}
                                                    disabled={currentPage === totalPages}
                                                    className={`px-3 py-1 rounded ${currentPage === totalPages ? 'text-gray-400' : 'text-gray-700 hover:bg-gray-100'}`}
                                                >
                                                    <i className="fas fa-angle-right"></i>
                                                </button>
                                                <button
                                                    onClick={() => handlePageChange(totalPages)}
                                                    disabled={currentPage === totalPages}
                                                    className={`px-3 py-1 rounded ${currentPage === totalPages ? 'text-gray-400' : 'text-gray-700 hover:bg-gray-100'}`}
                                                >
                                                    <i className="fas fa-angle-double-right"></i>
                                                </button>
                                            </div>
                                        </div>
                                    )}
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default CompanyListPage;