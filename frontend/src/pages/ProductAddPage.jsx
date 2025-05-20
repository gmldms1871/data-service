// src/pages/ProductAddPage.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

const ProductAddPage = () => {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('product');
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState({
        main: '',
        sub: '',
        detail: ''
    });
    const [tags, setTags] = useState([]);
    const [uploadedMainImage, setUploadedMainImage] = useState(null);
    const [uploadedDescImages, setUploadedDescImages] = useState([]);
    const [packageOptions, setPackageOptions] = useState([
        { title: 'Basic', price: 0, description: '' },
        { title: 'Standard', price: 0, description: '' },
        { title: 'Premium', price: 0, description: '' }
    ]);
    const [selectedPackage, setSelectedPackage] = useState(1); // 0: Basic, 1: Standard, 2: Premium

    // 상품 폼 데이터
    const [productForm, setProductForm] = useState({
        name: '',
        description: '',
        price: '',
        discountRate: 0,
        categoryId: '',
        mainImage: null,
        descriptionImage: null,
        extensionList: ''
    });

    // 광고 폼 데이터
    const [adForm, setAdForm] = useState({
        productId: '',
        adType: 'main', // main, search, category
        startDate: '',
        endDate: '',
        duration: 2, // 1, 2, 4주
        title: '',
        description: '',
        attachmentId: null
    });

    useEffect(() => {
        // 로그인 확인
        const loginId = sessionStorage.getItem('loginCompanyId');
        if (!loginId) {
            alert('로그인이 필요한 서비스입니다.');
            navigate('/');
            return;
        }

        // 카테고리 목록 로드
        const fetchCategories = async () => {
            try {
                const res = await fetch('/api/categories');
                if (res.ok) {
                    const data = await res.json();
                    setCategories(data);
                }
            } catch (error) {
                console.error('카테고리 로딩 중 오류:', error);
            }
        };

        // 내 상품 목록 로드 (광고 등록용)
        const fetchMyProducts = async () => {
            try {
                const res = await fetch(`/api/products?companyId=${loginId}`);
                if (res.ok) {
                    const data = await res.json();
                    if (data.length > 0) {
                        setAdForm(prev => ({
                            ...prev,
                            productId: data[0].id
                        }));
                    }
                }
            } catch (error) {
                console.error('상품 로딩 중 오류:', error);
            }
        };

        fetchCategories();
        fetchMyProducts();

        // 오늘 날짜와 2주 후 날짜 설정
        const today = new Date();
        const twoWeeksLater = new Date();
        twoWeeksLater.setDate(today.getDate() + 14);

        setAdForm(prev => ({
            ...prev,
            startDate: today.toISOString().split('T')[0],
            endDate: twoWeeksLater.toISOString().split('T')[0]
        }));
    }, [navigate]);

    const handleTabChange = (tab) => {
        setActiveTab(tab);
    };

    // 상품 폼 입력 처리
    const handleProductFormChange = (e) => {
        const { name, value, type } = e.target;

        if (type === 'file') {
            const file = e.target.files[0];
            if (!file) return;

            if (name === 'mainImage') {
                setUploadedMainImage(file);
                setProductForm(prev => ({
                    ...prev,
                    mainImage: file
                }));
            } else if (name === 'descriptionImage') {
                setUploadedDescImages(prev => [...prev, file]);
                setProductForm(prev => ({
                    ...prev,
                    descriptionImage: file
                }));
            }
        } else {
            setProductForm(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    // 광고 폼 입력 처리
    const handleAdFormChange = (e) => {
        const { name, value, type } = e.target;

        if (type === 'file') {
            const file = e.target.files[0];
            if (!file) return;

            setAdForm(prev => ({
                ...prev,
                attachmentId: file
            }));
        } else {
            setAdForm(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    // 카테고리 선택 처리
    const handleCategorySelect = (level, value) => {
        if (level === 'main') {
            setSelectedCategories({
                main: value,
                sub: '',
                detail: ''
            });
        } else if (level === 'sub') {
            setSelectedCategories(prev => ({
                ...prev,
                sub: value,
                detail: ''
            }));
        } else {
            setSelectedCategories(prev => ({
                ...prev,
                detail: value
            }));

            // 최종 카테고리 ID 설정
            setProductForm(prev => ({
                ...prev,
                categoryId: value
            }));
        }
    };

    // 태그 추가 처리
    const handleAddTag = (e) => {
        if (e.key === 'Enter' && e.target.value.trim()) {
            e.preventDefault();

            const newTag = e.target.value.trim();

            if (tags.length >= 10) {
                alert('태그는 최대 10개까지 추가할 수 있습니다.');
                return;
            }

            if (!tags.includes(newTag)) {
                setTags(prev => [...prev, newTag]);

                // extensionList 업데이트
                setProductForm(prev => ({
                    ...prev,
                    extensionList: [...tags, newTag].join(',')
                }));
            }

            e.target.value = '';
        }
    };

    // 태그 삭제 처리
    const handleRemoveTag = (index) => {
        const newTags = tags.filter((_, i) => i !== index);
        setTags(newTags);

        // extensionList 업데이트
        setProductForm(prev => ({
            ...prev,
            extensionList: newTags.join(',')
        }));
    };

    // 패키지 옵션 처리
    const handlePackageChange = (index, field, value) => {
        const newPackageOptions = [...packageOptions];
        newPackageOptions[index] = {
            ...newPackageOptions[index],
            [field]: value
        };
        setPackageOptions(newPackageOptions);
    };

    // 광고 기간 선택 처리
    const handleDurationSelect = (weeks) => {
        setAdForm(prev => ({
            ...prev,
            duration: weeks
        }));

        // 종료일 계산
        const startDate = new Date(adForm.startDate);
        const endDate = new Date(startDate);
        endDate.setDate(startDate.getDate() + weeks * 7);

        setAdForm(prev => ({
            ...prev,
            endDate: endDate.toISOString().split('T')[0]
        }));
    };

    // 날짜 변경시 기간 재계산
    const handleDateChange = (e) => {
        const { name, value } = e.target;
        setAdForm(prev => ({
            ...prev,
            [name]: value
        }));

        if (name === 'startDate' && adForm.endDate) {
            const startDate = new Date(value);
            const endDate = new Date(adForm.endDate);
            const diffInDays = Math.round((endDate - startDate) / (1000 * 60 * 60 * 24));
            const diffInWeeks = Math.round(diffInDays / 7);

            if (diffInWeeks === 1 || diffInWeeks === 2 || diffInWeeks === 4) {
                setAdForm(prev => ({
                    ...prev,
                    duration: diffInWeeks
                }));
            }
        } else if (name === 'endDate' && adForm.startDate) {
            const startDate = new Date(adForm.startDate);
            const endDate = new Date(value);
            const diffInDays = Math.round((endDate - startDate) / (1000 * 60 * 60 * 24));
            const diffInWeeks = Math.round(diffInDays / 7);

            if (diffInWeeks === 1 || diffInWeeks === 2 || diffInWeeks === 4) {
                setAdForm(prev => ({
                    ...prev,
                    duration: diffInWeeks
                }));
            }
        }
    };

    // 상품 등록 처리
    const handleProductSubmit = async (e) => {
        e.preventDefault();

        // 필수 필드 검증
        if (!productForm.name) {
            alert('상품명을 입력해주세요.');
            return;
        }

        if (!productForm.description) {
            alert('상품 설명을 입력해주세요.');
            return;
        }

        if (!productForm.price) {
            alert('가격을 입력해주세요.');
            return;
        }

        if (!productForm.categoryId) {
            alert('카테고리를 선택해주세요.');
            return;
        }

        try {
            // 이미지 업로드
            let mainImageId = null;
            let descriptionImageId = null;

            if (productForm.mainImage) {
                const mainImageFormData = new FormData();
                mainImageFormData.append('file', productForm.mainImage);

                const mainImageRes = await fetch('/api/files/upload', {
                    method: 'POST',
                    body: mainImageFormData,
                    credentials: 'include'
                });

                if (mainImageRes.ok) {
                    const mainImageData = await mainImageRes.json();
                    mainImageId = mainImageData.id;
                }
            }

            if (productForm.descriptionImage) {
                const descImageFormData = new FormData();
                descImageFormData.append('file', productForm.descriptionImage);

                const descImageRes = await fetch('/api/files/upload', {
                    method: 'POST',
                    body: descImageFormData,
                    credentials: 'include'
                });

                if (descImageRes.ok) {
                    const descImageData = await descImageRes.json();
                    descriptionImageId = descImageData.id;
                }
            }

            // 상품 등록 데이터 준비
            const productData = {
                name: productForm.name,
                description: productForm.description,
                price: parseInt(productForm.price),
                discountRate: productForm.discountRate ? parseInt(productForm.discountRate) : 0,
                categoryId: productForm.categoryId,
                mainImage: mainImageId,
                descriptionImage: descriptionImageId,
                extensionList: productForm.extensionList
            };

            // 상품 등록 API 호출
            const createProductRes = await fetch('/api/products', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify(productData)
            });

            if (!createProductRes.ok) {
                throw new Error('상품 등록에 실패했습니다.');
            }

            const createdProduct = await createProductRes.json();

            alert('상품이 성공적으로 등록되었습니다!');
            navigate(`/products/${createdProduct.id}`);
        } catch (error) {
            console.error('상품 등록 중 오류 발생:', error);
            alert(`상품 등록 중 오류가 발생했습니다: ${error.message}`);
        }
    };

    // 광고 등록 처리
    const handleAdSubmit = async (e) => {
        e.preventDefault();

        // 필수 필드 검증
        if (!adForm.productId) {
            alert('광고할 상품을 선택해주세요.');
            return;
        }

        if (!adForm.startDate || !adForm.endDate) {
            alert('광고 기간을 설정해주세요.');
            return;
        }

        try {
            // 이미지 업로드
            let adImageId = null;

            if (adForm.attachmentId) {
                const adImageFormData = new FormData();
                adImageFormData.append('file', adForm.attachmentId);

                const adImageRes = await fetch('/api/files/upload', {
                    method: 'POST',
                    body: adImageFormData,
                    credentials: 'include'
                });

                if (adImageRes.ok) {
                    const adImageData = await adImageRes.json();
                    adImageId = adImageData.id;
                }
            }

            // 광고 등록 데이터 준비
            const adData = {
                productId: adForm.productId,
                companyId: sessionStorage.getItem('loginCompanyId'),
                adsPeriod: adForm.duration * 7, // 일 단위로 변환
                attachmentId: adImageId
            };

            // 광고 등록 API 호출
            const createAdRes = await fetch('/api/ads', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify(adData)
            });

            if (!createAdRes.ok) {
                throw new Error('광고 등록에 실패했습니다.');
            }

            alert('광고가 성공적으로 등록되었습니다!');
            navigate('/mypage');
        } catch (error) {
            console.error('광고 등록 중 오류 발생:', error);
            alert(`광고 등록 중 오류가 발생했습니다: ${error.message}`);
        }
    };

    // 광고 가격 계산
    const calculateAdPrice = () => {
        const basePrice = 50000; // 1주 기본 가격
        let price = 0;

        switch (adForm.adType) {
            case 'main':
                price = basePrice;
                break;
            case 'search':
                price = 30000;
                break;
            case 'category':
                price = 20000;
                break;
            default:
                price = basePrice;
        }

        // 기간에 따른 할인
        switch (adForm.duration) {
            case 1:
                return price;
            case 2:
                return price * 2 * 0.95; // 5% 할인
            case 4:
                return price * 4 * 0.9; // 10% 할인
            default:
                return price * adForm.duration;
        }
    };

    return (
        <div className="bg-gray-50 min-h-screen">
            <Navbar />

            <div className="container mx-auto px-4 py-8">
                <div className="flex border-b border-gray-200 mb-6">
                    <button
                        className={`px-6 py-3 font-medium text-sm ${activeTab === 'product' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'}`}
                        onClick={() => handleTabChange('product')}
                    >
                        상품 등록
                    </button>
                    <button
                        className={`px-6 py-3 font-medium text-sm ${activeTab === 'ad' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'}`}
                        onClick={() => handleTabChange('ad')}
                    >
                        광고 등록
                    </button>
                </div>

                {/* 상품 등록 탭 */}
                {activeTab === 'product' && (
                    <form onSubmit={handleProductSubmit}>
                        {/* 기본 정보 */}
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <h2 className="text-lg font-bold mb-6 pb-3 border-b border-gray-200">기본 정보</h2>

                            <div className="mb-6">
                                <label htmlFor="name" className="block text-gray-700 font-medium mb-2">
                                    상품명 <span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={productForm.name}
                                    onChange={handleProductFormChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="판매할 상품명을 입력해주세요"
                                    maxLength={40}
                                    required
                                />
                                <p className="text-sm text-gray-500 mt-1">최대 40자까지 입력 가능합니다.</p>
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 font-medium mb-2">
                                    카테고리 <span className="text-red-500">*</span>
                                </label>
                                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <div className="border border-gray-300 rounded-md h-48 overflow-y-auto">
                                        {categories.length > 0 ? (
                                            <ul>
                                                {categories.map(category => (
                                                    <li
                                                        key={category.id}
                                                        className={`px-4 py-2 cursor-pointer hover:bg-gray-100 ${selectedCategories.main === category.id ? 'bg-blue-50 text-blue-600' : ''}`}
                                                        onClick={() => handleCategorySelect('main', category.id)}
                                                    >
                                                        {category.name}
                                                    </li>
                                                ))}
                                            </ul>
                                        ) : (
                                            <ul>
                                                {['디자인', 'IT 및 프로그래밍', '영상/사진/음향', '마케팅'].map((cat, idx) => (
                                                    <li
                                                        key={idx}
                                                        className={`px-4 py-2 cursor-pointer hover:bg-gray-100 ${selectedCategories.main === cat ? 'bg-blue-50 text-blue-600' : ''}`}
                                                        onClick={() => handleCategorySelect('main', cat)}
                                                    >
                                                        {cat}
                                                    </li>
                                                ))}
                                            </ul>
                                        )}
                                    </div>

                                    <div className="border border-gray-300 rounded-md h-48 overflow-y-auto">
                                        {selectedCategories.main ? (
                                            <ul>
                                                {['웹 개발', '앱 개발', '프로그램 개발', 'AI 및 데이터'].map((cat, idx) => (
                                                    <li
                                                        key={idx}
                                                        className={`px-4 py-2 cursor-pointer hover:bg-gray-100 ${selectedCategories.sub === cat ? 'bg-blue-50 text-blue-600' : ''}`}
                                                        onClick={() => handleCategorySelect('sub', cat)}
                                                    >
                                                        {cat}
                                                    </li>
                                                ))}
                                            </ul>
                                        ) : (
                                            <div className="flex items-center justify-center h-full text-gray-400">
                                                먼저 대분류를 선택해주세요
                                            </div>
                                        )}
                                    </div>

                                    <div className="border border-gray-300 rounded-md h-48 overflow-y-auto">
                                        {selectedCategories.sub ? (
                                            <ul>
                                                {['웹사이트 제작', '웹 애플리케이션', '반응형 웹', '워드프레스'].map((cat, idx) => (
                                                    <li
                                                        key={idx}
                                                        className={`px-4 py-2 cursor-pointer hover:bg-gray-100 ${selectedCategories.detail === cat ? 'bg-blue-50 text-blue-600' : ''}`}
                                                        onClick={() => handleCategorySelect('detail', cat)}
                                                    >
                                                        {cat}
                                                    </li>
                                                ))}
                                            </ul>
                                        ) : (
                                            <div className="flex items-center justify-center h-full text-gray-400">
                                                중분류를 선택해주세요
                                            </div>
                                        )}
                                    </div>
                                </div>
                                <p className="text-sm text-gray-500 mt-2">선택된 카테고리: {selectedCategories.detail || selectedCategories.sub || selectedCategories.main || '없음'}</p>
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 font-medium mb-2">태그</label>
                                <div className="border border-gray-300 rounded-md p-3 focus-within:ring-2 focus-within:ring-blue-500 focus-within:border-blue-500">
                                    <div className="flex flex-wrap gap-2 mb-2">
                                        {tags.map((tag, index) => (
                                            <div key={index} className="bg-blue-50 text-blue-600 px-3 py-1 rounded-full text-sm flex items-center">
                                                {tag}
                                                <button
                                                    type="button"
                                                    className="ml-1 text-blue-600 hover:text-blue-800 focus:outline-none"
                                                    onClick={() => handleRemoveTag(index)}
                                                >
                                                    &times;
                                                </button>
                                            </div>
                                        ))}
                                    </div>
                                    <input
                                        type="text"
                                        placeholder="태그를 입력하고 Enter를 누르세요"
                                        className="w-full border-none p-0 focus:outline-none focus:ring-0"
                                        onKeyDown={handleAddTag}
                                    />
                                </div>
                                <p className="text-sm text-gray-500 mt-1">상품을 잘 나타내는 태그를 입력해주세요. 최대 10개까지 가능합니다.</p>
                            </div>
                        </div>

                        {/* 상품 상세 정보 */}
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <h2 className="text-lg font-bold mb-6 pb-3 border-b border-gray-200">상품 상세 정보</h2>

                            <div className="mb-6">
                                <label htmlFor="description" className="block text-gray-700 font-medium mb-2">
                                    상품 설명 <span className="text-red-500">*</span>
                                </label>
                                <textarea
                                    id="description"
                                    name="description"
                                    value={productForm.description}
                                    onChange={handleProductFormChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    rows="8"
                                    placeholder="판매하는 상품에 대한 소개를 작성해주세요"
                                    required
                                ></textarea>
                                <p className="text-sm text-gray-500 mt-1">최소 200자 이상 작성해주세요.</p>
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 font-medium mb-2">
                                    상품 메인 이미지 <span className="text-red-500">*</span>
                                </label>
                                <div
                                    className="border-2 border-dashed border-gray-300 rounded-md p-6 text-center cursor-pointer hover:border-blue-500 transition-colors"
                                    onClick={() => document.getElementById('mainImageInput').click()}
                                >
                                    {uploadedMainImage ? (
                                        <div className="relative">
                                            <img
                                                src={URL.createObjectURL(uploadedMainImage)}
                                                alt="메인 이미지 미리보기"
                                                className="max-h-48 mx-auto"
                                            />
                                            <button
                                                type="button"
                                                className="absolute top-2 right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center"
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    setUploadedMainImage(null);
                                                    setProductForm(prev => ({ ...prev, mainImage: null }));
                                                }}
                                            >
                                                &times;
                                            </button>
                                        </div>
                                    ) : (
                                        <>
                                            <div className="text-3xl mb-2">+</div>
                                            <p>이미지를 업로드해주세요</p>
                                            <p className="text-sm text-gray-500 mt-1">최대 5MB, JPG, PNG 파일</p>
                                        </>
                                    )}
                                </div>
                                <input
                                    type="file"
                                    id="mainImageInput"
                                    name="mainImage"
                                    className="hidden"
                                    accept="image/jpeg,image/png"
                                    onChange={handleProductFormChange}
                                />
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 font-medium mb-2">상품 상세 이미지</label>
                                <div
                                    className="border-2 border-dashed border-gray-300 rounded-md p-6 text-center cursor-pointer hover:border-blue-500 transition-colors"
                                    onClick={() => document.getElementById('descImageInput').click()}
                                >
                                    {uploadedDescImages.length > 0 ? (
                                        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                                            {uploadedDescImages.map((img, idx) => (
                                                <div key={idx} className="relative">
                                                    <img
                                                        src={URL.createObjectURL(img)}
                                                        alt={`상세 이미지 ${idx+1}`}
                                                        className="h-32 w-full object-cover rounded-md"
                                                    />
                                                    <button
                                                        type="button"
                                                        className="absolute top-2 right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            const newImages = [...uploadedDescImages];
                                                            newImages.splice(idx, 1);
                                                            setUploadedDescImages(newImages);
                                                            if (idx === 0) {
                                                                setProductForm(prev => ({
                                                                    ...prev,
                                                                    descriptionImage: newImages.length > 0 ? newImages[0] : null
                                                                }));
                                                            }
                                                        }}
                                                    >
                                                        &times;
                                                    </button>
                                                </div>
                                            ))}
                                        </div>
                                    ) : (
                                        <>
                                            <div className="text-3xl mb-2">+</div>
                                            <p>상세 이미지를 업로드해주세요</p>
                                            <p className="text-sm text-gray-500 mt-1">최대 5MB, JPG, PNG 파일</p>
                                        </>
                                    )}
                                </div>
                                <input
                                    type="file"
                                    id="descImageInput"
                                    name="descriptionImage"
                                    className="hidden"
                                    accept="image/jpeg,image/png"
                                    onChange={handleProductFormChange}
                                />
                                <p className="text-sm text-gray-500 mt-1">상세 이미지는 1장만 지원됩니다.</p>
                            </div>
                        </div>

                        {/* 가격 정보 */}
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <h2 className="text-lg font-bold mb-6 pb-3 border-b border-gray-200">가격 정보</h2>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                                <div>
                                    <label htmlFor="price" className="block text-gray-700 font-medium mb-2">
                                        기본 가격 <span className="text-red-500">*</span>
                                    </label>
                                    <div className="relative">
                                        <span className="absolute left-3 top-3 text-gray-500">₩</span>
                                        <input
                                            type="number"
                                            id="price"
                                            name="price"
                                            value={productForm.price}
                                            onChange={handleProductFormChange}
                                            className="w-full pl-8 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                            placeholder="0"
                                            min="0"
                                            required
                                        />
                                    </div>
                                    <p className="text-sm text-gray-500 mt-1">VAT 포함된 가격으로 입력해주세요.</p>
                                </div>

                                <div>
                                    <label htmlFor="discountRate" className="block text-gray-700 font-medium mb-2">할인율</label>
                                    <div className="relative">
                                        <input
                                            type="number"
                                            id="discountRate"
                                            name="discountRate"
                                            value={productForm.discountRate}
                                            onChange={handleProductFormChange}
                                            className="w-full pr-8 pl-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                            placeholder="0"
                                            min="0"
                                            max="100"
                                        />
                                        <span className="absolute right-3 top-3 text-gray-500">%</span>
                                    </div>
                                </div>
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 font-medium mb-2">패키지 옵션</label>
                                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    {packageOptions.map((option, idx) => (
                                        <div
                                            key={idx}
                                            className={`border rounded-lg p-4 cursor-pointer transition-colors ${selectedPackage === idx ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-blue-300'}`}
                                            onClick={() => setSelectedPackage(idx)}
                                        >
                                            <div className="mb-3">
                                                <input
                                                    type="text"
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                    placeholder="패키지 제목"
                                                    value={option.title}
                                                    onChange={(e) => handlePackageChange(idx, 'title', e.target.value)}
                                                />
                                            </div>
                                            <div className="mb-3">
                                                <div className="relative">
                                                    <span className="absolute left-3 top-3 text-gray-500">₩</span>
                                                    <input
                                                        type="number"
                                                        className="w-full pl-8 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                        placeholder="가격"
                                                        value={option.price}
                                                        onChange={(e) => handlePackageChange(idx, 'price', e.target.value)}
                                                        min="0"
                                                    />
                                                </div>
                                            </div>
                                            <div>
                                                <textarea
                                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                    placeholder="설명"
                                                    rows="3"
                                                    value={option.description}
                                                    onChange={(e) => handlePackageChange(idx, 'description', e.target.value)}
                                                ></textarea>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                                <p className="text-sm text-gray-500 mt-2">패키지 옵션은 선택사항입니다. 필요한 경우 설정해주세요.</p>
                            </div>
                        </div>

                        <div className="flex justify-end">
                            <button type="button" className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md mr-3 hover:bg-gray-50">임시저장</button>
                            <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">등록하기</button>
                        </div>
                    </form>
                )}

                {/* 광고 등록 탭 */}
                {activeTab === 'ad' && (
                    <form onSubmit={handleAdSubmit}>
                        {/* 광고 상품 선택 */}
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <h2 className="text-lg font-bold mb-6 pb-3 border-b border-gray-200">광고 상품 선택</h2>

                            <div className="mb-6">
                                <label htmlFor="productId" className="block text-gray-700 font-medium mb-2">
                                    광고할 상품 <span className="text-red-500">*</span>
                                </label>
                                <select
                                    id="productId"
                                    name="productId"
                                    value={adForm.productId}
                                    onChange={handleAdFormChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                >
                                    <option value="">선택해주세요</option>
                                    <option value="1">안드로이드 앱 개발해드립니다</option>
                                    <option value="2">맞춤형 모바일 앱 제작</option>
                                    <option value="3">하이브리드 앱 개발 전문가</option>
                                </select>
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 font-medium mb-2">
                                    광고 유형 <span className="text-red-500">*</span>
                                </label>
                                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <div
                                        className={`border rounded-lg p-4 cursor-pointer transition-colors ${adForm.adType === 'main' ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-blue-300'}`}
                                        onClick={() => setAdForm(prev => ({ ...prev, adType: 'main' }))}
                                    >
                                        <div className="flex justify-between items-center mb-2">
                                            <h3 className="font-medium">메인 노출</h3>
                                            <div className="text-blue-600 font-medium">50,000원 / 주</div>
                                        </div>
                                        <p className="text-sm text-gray-600">메인 페이지 상단에 상품이 노출됩니다</p>
                                    </div>

                                    <div
                                        className={`border rounded-lg p-4 cursor-pointer transition-colors ${adForm.adType === 'search' ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-blue-300'}`}
                                        onClick={() => setAdForm(prev => ({ ...prev, adType: 'search' }))}
                                    >
                                        <div className="flex justify-between items-center mb-2">
                                            <h3 className="font-medium">검색결과 상위노출</h3>
                                            <div className="text-blue-600 font-medium">30,000원 / 주</div>
                                        </div>
                                        <p className="text-sm text-gray-600">검색결과 최상단에 상품이 노출됩니다</p>
                                    </div>

                                    <div
                                        className={`border rounded-lg p-4 cursor-pointer transition-colors ${adForm.adType === 'category' ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-blue-300'}`}
                                        onClick={() => setAdForm(prev => ({ ...prev, adType: 'category' }))}
                                    >
                                        <div className="flex justify-between items-center mb-2">
                                            <h3 className="font-medium">카테고리 추천</h3>
                                            <div className="text-blue-600 font-medium">20,000원 / 주</div>
                                        </div>
                                        <p className="text-sm text-gray-600">해당 카테고리 페이지에서 추천 상품으로 노출됩니다</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* 광고 기간 설정 */}
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <h2 className="text-lg font-bold mb-6 pb-3 border-b border-gray-200">광고 기간 설정</h2>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                                <div>
                                    <label htmlFor="startDate" className="block text-gray-700 font-medium mb-2">
                                        시작일 <span className="text-red-500">*</span>
                                    </label>
                                    <input
                                        type="date"
                                        id="startDate"
                                        name="startDate"
                                        value={adForm.startDate}
                                        onChange={handleDateChange}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        required
                                    />
                                </div>

                                <div>
                                    <label htmlFor="endDate" className="block text-gray-700 font-medium mb-2">
                                        종료일 <span className="text-red-500">*</span>
                                    </label>
                                    <input
                                        type="date"
                                        id="endDate"
                                        name="endDate"
                                        value={adForm.endDate}
                                        onChange={handleDateChange}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        required
                                    />
                                </div>
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 font-medium mb-2">광고 기간</label>
                                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <div
                                        className={`border rounded-lg p-4 cursor-pointer transition-colors ${adForm.duration === 1 ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-blue-300'}`}
                                        onClick={() => handleDurationSelect(1)}
                                    >
                                        <div className="flex justify-between items-center">
                                            <h3 className="font-medium">1주</h3>
                                            <div className="text-blue-600 font-medium">50,000원</div>
                                        </div>
                                    </div>

                                    <div
                                        className={`border rounded-lg p-4 cursor-pointer transition-colors ${adForm.duration === 2 ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-blue-300'}`}
                                        onClick={() => handleDurationSelect(2)}
                                    >
                                        <div className="flex justify-between items-center mb-1">
                                            <h3 className="font-medium">2주</h3>
                                            <div className="text-blue-600 font-medium">95,000원</div>
                                        </div>
                                        <p className="text-xs text-green-600">5% 할인</p>
                                    </div>

                                    <div
                                        className={`border rounded-lg p-4 cursor-pointer transition-colors ${adForm.duration === 4 ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-blue-300'}`}
                                        onClick={() => handleDurationSelect(4)}
                                    >
                                        <div className="flex justify-between items-center mb-1">
                                            <h3 className="font-medium">4주</h3>
                                            <div className="text-blue-600 font-medium">180,000원</div>
                                        </div>
                                        <p className="text-xs text-green-600">10% 할인</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* 광고 내용 설정 */}
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <h2 className="text-lg font-bold mb-6 pb-3 border-b border-gray-200">광고 내용 설정</h2>

                            <div className="mb-6">
                                <label htmlFor="adTitle" className="block text-gray-700 font-medium mb-2">
                                    광고 제목 <span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="text"
                                    id="adTitle"
                                    name="title"
                                    value={adForm.title}
                                    onChange={handleAdFormChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="광고에 표시될 제목을 입력해주세요"
                                    maxLength={30}
                                />
                                <p className="text-sm text-gray-500 mt-1">최대 30자까지 입력 가능합니다.</p>
                            </div>

                            <div className="mb-6">
                                <label htmlFor="adDescription" className="block text-gray-700 font-medium mb-2">광고 설명</label>
                                <textarea
                                    id="adDescription"
                                    name="description"
                                    value={adForm.description}
                                    onChange={handleAdFormChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    rows="3"
                                    placeholder="광고에 대한 간단한 설명을 작성해주세요"
                                    maxLength={100}
                                ></textarea>
                                <p className="text-sm text-gray-500 mt-1">최대 100자까지 입력 가능합니다.</p>
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 font-medium mb-2">
                                    광고 이미지 <span className="text-red-500">*</span>
                                </label>
                                <div
                                    className="border-2 border-dashed border-gray-300 rounded-md p-6 text-center cursor-pointer hover:border-blue-500 transition-colors"
                                    onClick={() => document.getElementById('adImageInput').click()}
                                >
                                    {adForm.attachmentId ? (
                                        <div className="relative">
                                            <img
                                                src={URL.createObjectURL(adForm.attachmentId)}
                                                alt="광고 이미지 미리보기"
                                                className="max-h-48 mx-auto"
                                            />
                                            <button
                                                type="button"
                                                className="absolute top-2 right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center"
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    setAdForm(prev => ({ ...prev, attachmentId: null }));
                                                }}
                                            >
                                                &times;
                                            </button>
                                        </div>
                                    ) : (
                                        <>
                                            <div className="text-3xl mb-2">+</div>
                                            <p>광고용 이미지를 업로드해주세요</p>
                                            <p className="text-sm text-gray-500 mt-1">권장 사이즈: 1200x628px (16:9 비율)</p>
                                        </>
                                    )}
                                </div>
                                <input
                                    type="file"
                                    id="adImageInput"
                                    name="attachmentId"
                                    className="hidden"
                                    accept="image/jpeg,image/png"
                                    onChange={handleAdFormChange}
                                />
                            </div>

                            <div className="border border-gray-200 rounded-lg p-4 bg-gray-50">
                                <h3 className="font-medium mb-3">광고 미리보기</h3>
                                <div className="bg-gray-200 h-32 mb-3 flex items-center justify-center text-gray-400">
                                    {adForm.attachmentId ? (
                                        <img
                                            src={URL.createObjectURL(adForm.attachmentId)}
                                            alt="광고 이미지"
                                            className="h-full object-contain"
                                        />
                                    ) : '이미지 미리보기'}
                                </div>
                                <h4 className="font-medium mb-1">{adForm.title || '광고 제목'}</h4>
                                <p className="text-sm text-gray-600">{adForm.description || '광고 설명'}</p>
                            </div>
                        </div>

                        {/* 결제 정보 */}
                        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                            <h2 className="text-lg font-bold mb-6 pb-3 border-b border-gray-200">결제 정보</h2>

                            <div className="mb-6">
                                <div className="text-xl font-bold text-blue-600 mb-1">
                                    ₩{calculateAdPrice().toLocaleString()}
                                </div>
                                <p className="text-sm text-gray-500">VAT 포함 금액입니다.</p>
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 font-medium mb-2">
                                    결제 방법 <span className="text-red-500">*</span>
                                </label>
                                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <div
                                        className="border rounded-lg p-4 cursor-pointer transition-colors border-blue-500 bg-blue-50"
                                    >
                                        <h3 className="font-medium">신용카드</h3>
                                    </div>
                                    <div
                                        className="border rounded-lg p-4 cursor-pointer transition-colors border-gray-300 hover:border-blue-300"
                                    >
                                        <h3 className="font-medium">계좌이체</h3>
                                    </div>
                                    <div
                                        className="border rounded-lg p-4 cursor-pointer transition-colors border-gray-300 hover:border-blue-300"
                                    >
                                        <h3 className="font-medium">무통장입금</h3>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="flex justify-end">
                            <button type="button" className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md mr-3 hover:bg-gray-50">저장하기</button>
                            <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">결제하기</button>
                        </div>
                    </form>
                )}
            </div>

            <Footer />
        </div>
    );
};

export default ProductAddPage;