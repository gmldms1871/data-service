// src/pages/CompanyDetailPage.jsx - 수정본
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

const CompanyDetailPage = () => {
    const { companyId } = useParams();
    const [company, setCompany] = useState(null);
    const [products, setProducts] = useState([]);
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('products');
    const [error, setError] = useState(null);

    useEffect(() => {
        // 회사 정보 가져오기
        const fetchCompanyData = async () => {
            try {
                setLoading(true);
                setError(null);

                // 회사 정보 API 호출
                const companyRes = await fetch(`/api/users/${companyId}`);
                if (!companyRes.ok) throw new Error('회사 정보를 불러오는데 실패했습니다');
                const companyData = await companyRes.json();
                setCompany(companyData);

                // 회사의 상품 목록 API 호출
                const productsRes = await fetch(`/api/products?companyId=${companyId}`);
                if (productsRes.ok) {
                    const productsData = await productsRes.json();
                    setProducts(productsData);
                }

                // 회사의 리뷰 목록 API 호출
                const reviewsRes = await fetch(`/api/reviews?companyId=${companyId}`);
                if (reviewsRes.ok) {
                    const reviewsData = await reviewsRes.json();
                    setReviews(reviewsData);
                }

                setLoading(false);
            } catch (error) {
                console.error('데이터 로딩 중 오류 발생:', error);
                setError(error.message);
                setLoading(false);
            }
        };

        fetchCompanyData();
    }, [companyId]);

    const handleInquiry = () => {
        window.location.href = `/inquiry?companyId=${companyId}`;
    };

    const handleAddToWishlist = async () => {
        try {
            // 관심 기업 추가 API 호출 (백엔드에서 해당 API 구현 필요)
            const res = await fetch('/api/wishlist', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({ companyId })
            });

            if (res.ok) {
                alert('관심 기업에 추가되었습니다.');
            } else {
                alert('관심 기업 추가에 실패했습니다.');
            }
        } catch (error) {
            console.error('관심 기업 추가 중 오류 발생:', error);
            alert('관심 기업 추가 중 오류가 발생했습니다.');
        }
    };

    if (loading) {
        return (
            <div className="bg-gray-50 font-sans min-h-screen">
                <Navbar />
                <div className="container mx-auto px-4 pt-24 flex justify-center">
                    <p className="text-xl">로딩 중...</p>
                </div>
                <Footer />
            </div>
        );
    }

    if (error) {
        return (
            <div className="bg-gray-50 font-sans min-h-screen">
                <Navbar />
                <div className="container mx-auto px-4 pt-24 flex justify-center">
                    <p className="text-xl text-red-500">오류: {error}</p>
                </div>
                <Footer />
            </div>
        );
    }

    if (!company) {
        return (
            <div className="bg-gray-50 font-sans min-h-screen">
                <Navbar />
                <div className="container mx-auto px-4 pt-24 flex justify-center">
                    <p className="text-xl">회사 정보를 찾을 수 없습니다.</p>
                </div>
                <Footer />
            </div>
        );
    }

    // rating 계산
    const averageRating = reviews.length > 0
        ? (reviews.reduce((sum, review) => sum + review.rating, 0) / reviews.length).toFixed(1)
        : 0;

    return (
        <div className="bg-gray-50 font-sans min-h-screen">
            <Navbar />

            <div className="company-header bg-white shadow-md">
                <div className="container mx-auto px-4 py-10">
                    <div className="flex flex-col md:flex-row items-center md:items-start gap-8">
                        <img
                            src={company.profileAttachmentUrl || "/img/default-company.png"}
                            alt={company.companyName}
                            className="w-24 h-24 rounded-lg object-cover border border-gray-200"
                        />
                        <div>
                            <h1 className="text-3xl font-bold mb-2">{company.companyName}</h1>
                            <div className="flex items-center mb-3">
                                <div className="text-yellow-400 flex items-center">
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <span key={star}>
                                            {star <= Math.floor(averageRating) ? (
                                                <i className="fas fa-star"></i>
                                            ) : star - 0.5 <= averageRating ? (
                                                <i className="fas fa-star-half-alt"></i>
                                            ) : (
                                                <i className="far fa-star"></i>
                                            )}
                                        </span>
                                    ))}
                                    <span className="ml-1 text-gray-700">{averageRating}</span>
                                </div>
                                <span className="text-gray-500 ml-2">({reviews.length}개 리뷰)</span>
                            </div>
                            <p className="text-gray-600 mb-4">{company.nickname || company.companyName}</p>
                            <div className="flex flex-wrap gap-2">
                                <button
                                    onClick={handleInquiry}
                                    className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition duration-300"
                                >
                                    문의하기
                                </button>
                                <button
                                    onClick={handleAddToWishlist}
                                    className="bg-white text-blue-600 border border-blue-600 px-4 py-2 rounded-md hover:bg-blue-50 transition duration-300"
                                >
                                    관심 기업 추가
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="container mx-auto px-4 py-6">
                <div className="border-b border-gray-200 mb-6">
                    <div className="flex">
                        <button
                            className={`px-4 py-3 font-medium text-sm ${activeTab === 'products' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'}`}
                            onClick={() => setActiveTab('products')}
                        >
                            상품
                        </button>
                        <button
                            className={`px-4 py-3 font-medium text-sm ${activeTab === 'about' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'}`}
                            onClick={() => setActiveTab('about')}
                        >
                            회사 소개
                        </button>
                        <button
                            className={`px-4 py-3 font-medium text-sm ${activeTab === 'reviews' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'}`}
                            onClick={() => setActiveTab('reviews')}
                        >
                            리뷰
                        </button>
                        <button
                            className={`px-4 py-3 font-medium text-sm ${activeTab === 'inquiry' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'}`}
                            onClick={() => setActiveTab('inquiry')}
                        >
                            문의하기
                        </button>
                    </div>
                </div>

                {/* 상품 탭 */}
                {activeTab === 'products' && (
                    <div>
                        <h2 className="text-xl font-bold mb-4">상품 목록</h2>
                        {products.length > 0 ? (
                            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                                {products.map(product => (
                                    <div key={product.id} className="bg-white rounded-lg overflow-hidden shadow-sm transition duration-300 hover:shadow-md">
                                        <a href={`/products/${product.id}`}>
                                            <img
                                                src={product.mainImage || "/img/default-product.png"}
                                                alt={product.name}
                                                className="w-full h-48 object-cover"
                                            />
                                            <div className="p-4">
                                                <h3 className="font-medium text-gray-800 mb-2 line-clamp-2 h-12">{product.name}</h3>
                                                <div className="text-sm text-gray-500 mb-4">카테고리: {product.categoryId}</div>
                                                <div className="flex justify-between">
                                                    <a href={`/products/${product.id}`} className="text-blue-600 text-sm">자세히 보기</a>
                                                </div>
                                            </div>
                                        </a>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p className="text-gray-500 text-center py-8">등록된 상품이 없습니다.</p>
                        )}
                    </div>
                )}

                {/* 회사 소개 탭 */}
                {activeTab === 'about' && (
                    <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                        <h2 className="text-xl font-bold mb-4">회사 소개</h2>
                        <div className="mb-6">
                            <p className="text-gray-600 leading-relaxed">
                                {company.description || `${company.companyName}은 전문 기업입니다.`}
                            </p>
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div className="flex">
                                <div className="w-32 text-gray-500">대표자</div>
                                <div>{company.userName}</div>
                            </div>
                            <div className="flex">
                                <div className="w-32 text-gray-500">설립일</div>
                                <div>{new Date(company.createdAt).toLocaleDateString()}</div>
                            </div>
                            <div className="flex">
                                <div className="w-32 text-gray-500">연락처</div>
                                <div>{company.userPhone}</div>
                            </div>
                            <div className="flex">
                                <div className="w-32 text-gray-500">이메일</div>
                                <div>{company.email}</div>
                            </div>
                            {company.homepage && (
                                <div className="flex">
                                    <div className="w-32 text-gray-500">웹사이트</div>
                                    <div><a href={company.homepage} target="_blank" rel="noopener noreferrer" className="text-blue-600">{company.homepage}</a></div>
                                </div>
                            )}
                            <div className="flex">
                                <div className="w-32 text-gray-500">사업자번호</div>
                                <div>{company.businessNumber}</div>
                            </div>
                        </div>
                    </div>
                )}

                {/* 리뷰 탭 */}
                {activeTab === 'reviews' && (
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <div className="flex justify-between items-center mb-6">
                            <h2 className="text-xl font-bold">고객 리뷰</h2>
                            <div className="flex items-center">
                                <span className="text-2xl font-bold mr-2">{averageRating}</span>
                                <div className="text-yellow-400">
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <span key={star}>
                                            {star <= Math.floor(averageRating) ? (
                                                <i className="fas fa-star"></i>
                                            ) : star - 0.5 <= averageRating ? (
                                                <i className="fas fa-star-half-alt"></i>
                                            ) : (
                                                <i className="far fa-star"></i>
                                            )}
                                        </span>
                                    ))}
                                </div>
                            </div>
                        </div>

                        {reviews.length > 0 ? (
                            <div className="space-y-6">
                                {reviews.map(review => (
                                    <div key={review.id} className="border-b border-gray-200 pb-6 last:border-0">
                                        <div className="flex justify-between items-start mb-2">
                                            <div className="font-medium">{review.userName || '익명'}</div>
                                            <div className="text-sm text-gray-500">{new Date(review.createdAt).toLocaleDateString()}</div>
                                        </div>
                                        <div className="text-yellow-400 mb-2">
                                            {[...Array(5)].map((_, index) => (
                                                <i key={index} className={`${index < review.rating ? 'fas' : 'far'} fa-star`}></i>
                                            ))}
                                            <span className="ml-1">{review.rating}.0</span>
                                        </div>
                                        <div className="text-gray-700 leading-relaxed">
                                            {review.content}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p className="text-gray-500 text-center py-8">아직 작성된 리뷰가 없습니다.</p>
                        )}
                    </div>
                )}

                {/* 문의하기 탭 */}
                {activeTab === 'inquiry' && (
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <h2 className="text-xl font-bold mb-6">문의하기</h2>
                        <form onSubmit={(e) => {
                            e.preventDefault();
                            // 폼 제출 로직은 inquiry 페이지에서 구현
                            window.location.href = `/inquiry?companyId=${companyId}`;
                        }}>
                            <div className="mb-4">
                                <label htmlFor="inquiry-subject" className="block text-gray-700 font-medium mb-2">문의 제목</label>
                                <input type="text" id="inquiry-subject" className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="문의 제목을 입력해주세요" />
                            </div>

                            <div className="mb-4">
                                <label htmlFor="inquiry-budget" className="block text-gray-700 font-medium mb-2">예산</label>
                                <input type="number" id="inquiry-budget" className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="예산을 입력해주세요" />
                            </div>

                            <div className="mb-4">
                                <label htmlFor="inquiry-message" className="block text-gray-700 font-medium mb-2">문의 내용</label>
                                <textarea id="inquiry-message" className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 min-h-[150px]" placeholder="문의하실 내용을 상세히 적어주세요."></textarea>
                            </div>

                            <button type="submit" className="w-full bg-blue-600 text-white py-3 px-4 rounded-md font-semibold hover:bg-blue-700 transition duration-300">문의 페이지로 이동</button>
                        </form>
                    </div>
                )}
            </div>

            <Footer />
        </div>
    );
};

export default CompanyDetailPage;