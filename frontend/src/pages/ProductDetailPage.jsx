// src/pages/ProductDetailPage.jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

const ProductDetailPage = () => {
    const { productId } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [company, setCompany] = useState(null);
    const [reviews, setReviews] = useState([]);
    const [relatedProducts, setRelatedProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);

    useEffect(() => {
        const fetchProductData = async () => {
            try {
                setLoading(true);

                // 상품 정보 가져오기
                const productRes = await fetch(`/api/products/${productId}`);
                if (!productRes.ok) throw new Error('상품 정보를 불러오는데 실패했습니다');
                const productData = await productRes.json();
                setProduct(productData);

                // 회사 정보 가져오기
                if (productData.companyId) {
                    const companyRes = await fetch(`/api/users/${productData.companyId}`);
                    if (companyRes.ok) {
                        const companyData = await companyRes.json();
                        setCompany(companyData);
                    }
                }

                // 리뷰 가져오기
                const reviewsRes = await fetch(`/api/reviews?productId=${productId}`);
                if (reviewsRes.ok) {
                    const reviewsData = await reviewsRes.json();
                    setReviews(reviewsData);
                }

                // 관련 상품 가져오기 (같은 회사의 다른 상품 또는 같은 카테고리의 상품)
                if (productData.categoryId) {
                    const relatedRes = await fetch(`/api/products?categoryId=${productData.categoryId}&limit=4`);
                    if (relatedRes.ok) {
                        const relatedData = await relatedRes.json();
                        // 현재 상품 제외
                        setRelatedProducts(relatedData.filter(p => p.id !== productId));
                    }
                }

                setLoading(false);
            } catch (error) {
                console.error('데이터 로딩 중 오류 발생:', error);
                setLoading(false);
            }
        };

        fetchProductData();
    }, [productId]);

    const handleInquiry = () => {
        navigate(`/inquiry?productId=${productId}`);
    };

    const handleAddToWishlist = async () => {
        const isLoggedIn = sessionStorage.getItem('loginCompanyId');
        if (!isLoggedIn) {
            alert('로그인이 필요한 서비스입니다.');
            navigate('/');
            return;
        }

        try {
            // 관심 상품 추가 API 호출 (백엔드에서 해당 API 구현 필요)
            const res = await fetch('/api/wishlist/product', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({ productId })
            });

            if (res.ok) {
                alert('관심 상품에 추가되었습니다.');
            } else {
                alert('관심 상품 추가에 실패했습니다.');
            }
        } catch (error) {
            console.error('관심 상품 추가 중 오류 발생:', error);
            alert('관심 상품 추가 중 오류가 발생했습니다.');
        }
    };

    // 상품 이미지 목록 생성
    const getProductImages = () => {
        const images = [];

        if (product.mainImage) images.push(product.mainImage);
        if (product.descriptionImage) images.push(product.descriptionImage);

        // 이미지가 없을 경우 기본 이미지 추가
        if (images.length === 0) {
            images.push('/assets/default-product.png');
        }

        return images;
    };

    // 평균 평점 계산
    const calculateAverageRating = () => {
        if (reviews.length === 0) return 0;
        const sum = reviews.reduce((total, review) => total + review.rating, 0);
        return (sum / reviews.length).toFixed(1);
    };

    if (loading) {
        return (
            <div className="bg-gray-50 min-h-screen">
                <Navbar />
                <div className="container mx-auto px-4 pt-24 flex justify-center">
                    <p className="text-xl">로딩 중...</p>
                </div>
                <Footer />
            </div>
        );
    }

    if (!product) {
        return (
            <div className="bg-gray-50 min-h-screen">
                <Navbar />
                <div className="container mx-auto px-4 pt-24 flex justify-center">
                    <p className="text-xl">상품 정보를 찾을 수 없습니다.</p>
                </div>
                <Footer />
            </div>
        );
    }

    const productImages = getProductImages();
    const averageRating = calculateAverageRating();

    return (
        <div className="bg-gray-50 min-h-screen">
            <Navbar />

            <div className="container mx-auto px-4 py-8">
                <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                    <div className="flex flex-col md:flex-row gap-8">
                        {/* 상품 이미지 섹션 */}
                        <div className="md:w-1/2">
                            <div className="relative h-[400px] bg-gray-100 rounded-lg overflow-hidden mb-4">
                                <img
                                    src={productImages[currentImageIndex]}
                                    alt={product.name}
                                    className="w-full h-full object-contain"
                                />
                            </div>

                            {/* 썸네일 이미지 */}
                            {productImages.length > 1 && (
                                <div className="flex gap-2">
                                    {productImages.map((img, idx) => (
                                        <div
                                            key={idx}
                                            className={`w-20 h-20 rounded-md overflow-hidden border-2 cursor-pointer ${idx === currentImageIndex ? 'border-blue-500' : 'border-gray-200'}`}
                                            onClick={() => setCurrentImageIndex(idx)}
                                        >
                                            <img src={img} alt="" className="w-full h-full object-cover" />
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>

                        {/* 상품 정보 섹션 */}
                        <div className="md:w-1/2">
                            {product.categoryId && (
                                <div className="inline-block bg-blue-100 text-blue-800 text-xs px-3 py-1 rounded-full mb-3">
                                    {product.categoryId}
                                </div>
                            )}

                            <h1 className="text-2xl font-bold mb-4">{product.name}</h1>

                            {company && (
                                <div className="flex items-center mb-4">
                                    <img
                                        src={company.profileAttachmentUrl || "/assets/default-company.png"}
                                        alt={company.companyName}
                                        className="w-12 h-12 rounded-full object-cover mr-3"
                                    />
                                    <div>
                                        <h3 className="font-medium">{company.companyName}</h3>
                                        <p className="text-sm text-gray-500">{company.nickname || ''}</p>
                                    </div>
                                </div>
                            )}

                            <div className="flex items-center mb-5">
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

                            <div className="bg-gray-50 p-4 rounded-lg mb-6">
                                <p className="text-sm text-gray-500 mb-1">서비스 가격</p>
                                <h2 className="text-3xl font-bold">
                                    {/* 가격이 없을 경우 "문의 필요" 표시 */}
                                    {product.price ? `${product.price.toLocaleString()}원` : "문의 필요"}
                                </h2>
                            </div>

                            <div className="flex gap-3 mb-6">
                                <button
                                    onClick={handleInquiry}
                                    className="flex-grow bg-blue-600 text-white py-3 px-4 rounded-md font-semibold hover:bg-blue-700 transition duration-300"
                                >
                                    상담 신청하기
                                </button>
                                <button
                                    onClick={handleAddToWishlist}
                                    className="bg-gray-100 text-gray-700 py-3 px-4 rounded-md hover:bg-gray-200 transition duration-300"
                                >
                                    <i className="far fa-heart"></i> 찜하기
                                </button>
                            </div>

                            {product.extensionList && (
                                <div className="mb-6">
                                    {product.extensionList.split(',').map((ext, idx) => (
                                        <span
                                            key={idx}
                                            className="inline-block bg-gray-100 text-gray-800 px-3 py-1 rounded-md text-sm mr-2 mb-2"
                                        >
                                            {ext.trim()}
                                        </span>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {/* 상품 설명 섹션 */}
                <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                    <h2 className="text-xl font-bold mb-6 pb-3 border-b border-gray-200">서비스 설명</h2>
                    <div className="prose max-w-none">
                        <p className="whitespace-pre-line">{product.description}</p>
                    </div>
                </div>

                {/* 리뷰 섹션 */}
                <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                    <div className="flex justify-between items-center mb-6">
                        <h2 className="text-xl font-bold">서비스 리뷰</h2>
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
                                        <div className="font-medium">{review.reviewerName || '익명'}</div>
                                        <div className="text-sm text-gray-500">{new Date(review.createdAt).toLocaleDateString()}</div>
                                    </div>
                                    <div className="text-yellow-400 mb-2">
                                        {[...Array(5)].map((_, index) => (
                                            <i key={index} className={`${index < review.rating ? 'fas' : 'far'} fa-star`}></i>
                                        ))}
                                    </div>
                                    <div className="text-gray-700 leading-relaxed">
                                        {review.review}
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <p className="text-center text-gray-500 py-8">아직 작성된 리뷰가 없습니다.</p>
                    )}
                </div>

                {/* 관련 상품 섹션 */}
                {relatedProducts.length > 0 && (
                    <div>
                        <h2 className="text-xl font-bold mb-6">관련 서비스</h2>
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                            {relatedProducts.map(relatedProduct => (
                                <div key={relatedProduct.id} className="bg-white rounded-lg overflow-hidden shadow-sm transition duration-300 hover:shadow-md">
                                    <a href={`/products/${relatedProduct.id}`}>
                                        <img
                                            src={relatedProduct.mainImage || "/assets/default-product.png"}
                                            alt={relatedProduct.name}
                                            className="w-full h-48 object-cover"
                                        />
                                        <div className="p-4">
                                            <h3 className="font-medium mb-2 line-clamp-2 h-12">{relatedProduct.name}</h3>
                                            <p className="text-sm text-gray-500 mb-2">{relatedProduct.companyName || ''}</p>
                                            <p className="font-bold text-blue-600">
                                                {relatedProduct.price ? `${relatedProduct.price.toLocaleString()}원` : "문의 필요"}
                                            </p>
                                        </div>
                                    </a>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>

            <Footer />
        </div>
    );
};

export default ProductDetailPage;