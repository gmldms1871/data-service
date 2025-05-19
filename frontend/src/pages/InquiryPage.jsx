// src/pages/InquiryPage.jsx
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

const InquiryPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [company, setCompany] = useState(null);
    const [product, setProduct] = useState(null);
    const [myInquiries, setMyInquiries] = useState([]);
    const [selectedExtensions, setSelectedExtensions] = useState(['pdf', 'doc', 'xls']);
    const [uploadedFiles, setUploadedFiles] = useState([]);

    // 폼 데이터
    const [formData, setFormData] = useState({
        message: '',
        budget: '',
        description: '',
        attachmentId: null,
        companyId: '',
        productId: '',
    });

    // URL 쿼리 파라미터 파싱
    const queryParams = new URLSearchParams(location.search);
    const companyId = queryParams.get('companyId');
    const productId = queryParams.get('productId');

    useEffect(() => {
        const checkLogin = () => {
            const loggedIn = sessionStorage.getItem('loginCompanyId');
            if (!loggedIn) {
                alert('로그인이 필요한 서비스입니다.');
                navigate('/');
                return false;
            }
            return true;
        };

        const fetchData = async () => {
            if (!checkLogin()) return;

            try {
                setLoading(true);

                // 초기 폼 데이터 설정
                setFormData(prev => ({
                    ...prev,
                    companyId: companyId || '',
                    productId: productId || '',
                }));

                // 회사 정보 가져오기 (companyId가 있는 경우)
                if (companyId) {
                    const companyRes = await fetch(`/api/users/${companyId}`);
                    if (companyRes.ok) {
                        const companyData = await companyRes.json();
                        setCompany(companyData);
                    }
                }

                // 상품 정보 가져오기 (productId가 있는 경우)
                if (productId) {
                    const productRes = await fetch(`/api/products/${productId}`);
                    if (productRes.ok) {
                        const productData = await productRes.json();
                        setProduct(productData);

                        // 상품에 연결된 회사 정보도 가져오기
                        if (productData.companyId && !companyId) {
                            const productCompanyRes = await fetch(`/api/users/${productData.companyId}`);
                            if (productCompanyRes.ok) {
                                const productCompanyData = await productCompanyRes.json();
                                setCompany(productCompanyData);
                                setFormData(prev => ({
                                    ...prev,
                                    companyId: productData.companyId
                                }));
                            }
                        }
                    }
                }

                // 내 문의 내역 가져오기
                const loggedInId = sessionStorage.getItem('loginCompanyId');
                const myInquiriesRes = await fetch(`/api/inquiries?userId=${loggedInId}`);
                if (myInquiriesRes.ok) {
                    const myInquiriesData = await myInquiriesRes.json();
                    setMyInquiries(myInquiriesData);
                }

                setLoading(false);
            } catch (error) {
                console.error('데이터 로딩 중 오류:', error);
                setLoading(false);
            }
        };

        fetchData();
    }, [companyId, productId, navigate]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const toggleExtension = (extension) => {
        setSelectedExtensions(prev => {
            if (prev.includes(extension)) {
                return prev.filter(ext => ext !== extension);
            } else {
                return [...prev, extension];
            }
        });
    };

    const handleFileUpload = (e) => {
        const files = Array.from(e.target.files);
        if (files.length === 0) return;

        // 파일 5개 제한
        if (uploadedFiles.length + files.length > 5) {
            alert('최대 5개 파일까지만 업로드 가능합니다.');
            return;
        }

        // 파일 크기 체크 (20MB)
        const oversizedFiles = files.filter(file => file.size > 20 * 1024 * 1024);
        if (oversizedFiles.length > 0) {
            alert('파일 크기는 20MB 이하여야 합니다.');
            return;
        }

        // 확장자 체크
        const allowedExtensions = selectedExtensions.map(ext => `.${ext}`);
        const invalidFiles = files.filter(file => {
            const extension = `.${file.name.split('.').pop().toLowerCase()}`;
            return !allowedExtensions.includes(extension);
        });

        if (invalidFiles.length > 0) {
            alert(`허용되지 않은 파일 형식입니다. 허용된 형식: ${selectedExtensions.join(', ')}`);
            return;
        }

        // 새 파일 추가
        setUploadedFiles(prev => [...prev, ...files]);
    };

    const removeFile = (index) => {
        setUploadedFiles(prev => prev.filter((_, i) => i !== index));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 필수 필드 검증
        if (!formData.message) {
            alert('문의 내용을 입력해주세요.');
            return;
        }

        if (!formData.budget) {
            alert('예산을 입력해주세요.');
            return;
        }

        if (!formData.companyId) {
            alert('문의할 회사를 선택해주세요.');
            return;
        }

        try {
            // 파일 업로드 로직 (필요시 구현)
            let attachmentId = null;
            if (uploadedFiles.length > 0) {
                const formDataForFile = new FormData();
                uploadedFiles.forEach(file => {
                    formDataForFile.append('files', file);
                });

                const fileRes = await fetch('/api/files/upload', {
                    method: 'POST',
                    body: formDataForFile,
                    credentials: 'include'
                });

                if (fileRes.ok) {
                    const fileData = await fileRes.json();
                    attachmentId = fileData.id;
                } else {
                    throw new Error('파일 업로드에 실패했습니다.');
                }
            }

            // 문의 데이터 준비
            const inquiryData = {
                companyId: formData.companyId,
                productId: formData.productId || null,
                message: formData.message,
                budget: parseInt(formData.budget),
                description: formData.description,
                attachmentId: attachmentId
            };

            // 문의 등록 API 호출
            const inquiryRes = await fetch('/api/inquiries', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify(inquiryData)
            });

            if (!inquiryRes.ok) {
                throw new Error('문의 등록에 실패했습니다.');
            }

            alert('문의가 성공적으로 등록되었습니다!');

            // 문의 성공 후 이동할 페이지 (마이페이지 또는 홈)
            navigate('/mypage');
        } catch (error) {
            console.error('문의 등록 중 오류 발생:', error);
            alert(`문의 등록 중 오류가 발생했습니다: ${error.message}`);
        }
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

    return (
        <div className="bg-gray-50 min-h-screen">
            <Navbar />

            <div className="container mx-auto px-4 py-8">
                <h1 className="text-2xl font-bold mb-6">문의하기</h1>

                <div className="flex flex-col lg:flex-row gap-6">
                    {/* 왼쪽: 문의 폼 */}
                    <div className="lg:w-2/3 bg-white rounded-lg shadow-sm p-6">
                        <h2 className="text-xl font-bold mb-6 pb-3 border-b border-gray-200">서비스 문의 작성</h2>

                        <form onSubmit={handleSubmit}>
                            <div className="mb-6">
                                <label htmlFor="message" className="block font-medium mb-2 text-gray-700">
                                    문의 내용 <span className="text-red-500">*</span>
                                </label>
                                <textarea
                                    id="message"
                                    name="message"
                                    value={formData.message}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    rows="5"
                                    placeholder="원하시는 서비스에 대해 상세하게 설명해주세요. 구체적인 내용을 작성할수록 더 정확한 답변을 받으실 수 있습니다."
                                    required
                                ></textarea>
                                <p className="text-sm text-gray-500 mt-1">최소 30자 이상 작성해주세요.</p>
                            </div>

                            <div className="mb-6">
                                <label htmlFor="budget" className="block font-medium mb-2 text-gray-700">
                                    예산 <span className="text-red-500">*</span>
                                </label>
                                <div className="relative">
                                    <span className="absolute left-3 top-3 text-gray-500">₩</span>
                                    <input
                                        type="number"
                                        id="budget"
                                        name="budget"
                                        value={formData.budget}
                                        onChange={handleChange}
                                        className="w-full pl-8 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        placeholder="예산을 입력해주세요"
                                        required
                                    />
                                </div>
                                <p className="text-sm text-gray-500 mt-1">상담을 위한 참고 금액입니다. 협의 가능합니다.</p>
                            </div>

                            <div className="mb-6">
                                <label className="block font-medium mb-2 text-gray-700">허용 파일 확장자</label>
                                <div className="flex flex-wrap gap-2">
                                    {['pdf', 'doc', 'xls', 'ppt', 'jpg', 'png', 'zip'].map(ext => (
                                        <label
                                            key={ext}
                                            className={`inline-flex items-center px-3 py-2 rounded-full cursor-pointer 
                                                      ${selectedExtensions.includes(ext)
                                                ? 'bg-blue-50 text-blue-600'
                                                : 'bg-gray-100 text-gray-700'}`}
                                            onClick={() => toggleExtension(ext)}
                                        >
                                            <input
                                                type="checkbox"
                                                className="hidden"
                                                checked={selectedExtensions.includes(ext)}
                                                onChange={() => {}}
                                            />
                                            <span>{ext.toUpperCase()}</span>
                                        </label>
                                    ))}
                                </div>
                            </div>

                            <div className="mb-6">
                                <label className="block font-medium mb-2 text-gray-700">파일 첨부</label>
                                <div className="border-2 border-dashed border-gray-300 rounded-md p-6 text-center cursor-pointer hover:border-blue-500 transition-colors"
                                     onClick={() => document.getElementById('fileInput').click()}>
                                    <i className="fas fa-upload text-gray-400 text-2xl mb-2"></i>
                                    <p>파일을 드래그하거나 클릭하여 업로드하세요</p>
                                    <p className="text-sm text-gray-500 mt-1">최대 5개 파일, 각 파일 20MB 이하</p>
                                </div>
                                <input
                                    type="file"
                                    id="fileInput"
                                    className="hidden"
                                    multiple
                                    onChange={handleFileUpload}
                                />

                                {uploadedFiles.length > 0 && (
                                    <ul className="mt-4 space-y-2">
                                        {uploadedFiles.map((file, index) => (
                                            <li key={index} className="flex justify-between items-center bg-gray-50 rounded-md px-4 py-2">
                                                <div className="flex items-center">
                                                    <i className="far fa-file mr-2 text-blue-500"></i>
                                                    <span className="text-sm">{file.name}</span>
                                                </div>
                                                <button
                                                    type="button"
                                                    className="text-red-500 hover:text-red-700"
                                                    onClick={() => removeFile(index)}
                                                >
                                                    <i className="fas fa-times"></i>
                                                </button>
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </div>

                            <div className="mb-6">
                                <label htmlFor="description" className="block font-medium mb-2 text-gray-700">추가 설명</label>
                                <textarea
                                    id="description"
                                    name="description"
                                    value={formData.description}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    rows="3"
                                    placeholder="추가로 전달하고 싶은 내용이 있으면 작성해주세요."
                                ></textarea>
                            </div>

                            <button
                                type="submit"
                                className="w-full bg-blue-600 text-white py-3 rounded-md font-semibold hover:bg-blue-700 transition duration-300"
                            >
                                문의 보내기
                            </button>
                        </form>

                        <div className="mt-8 bg-blue-50 p-4 rounded-md">
                            <h3 className="font-medium text-blue-800 mb-2 flex items-center">
                                <i className="fas fa-lightbulb mr-2"></i>
                                효과적인 문의를 위한 팁
                            </h3>
                            <ul className="text-sm text-gray-700 space-y-1 pl-5">
                                <li>구체적인 프로젝트 목표와 요구사항을 명확하게 설명해주세요.</li>
                                <li>예산 범위와 일정에 대한 정보를 포함하면 더 정확한 답변을 받을 수 있습니다.</li>
                                <li>참고할 만한 사이트나 디자인이 있다면 링크나 이미지를 첨부해주세요.</li>
                                <li>문의 후 판매자의 답변은 이메일과 마이페이지에서 확인하실 수 있습니다.</li>
                            </ul>
                        </div>
                    </div>

                    {/* 오른쪽: 회사/상품 정보 */}
                    <div className="lg:w-1/3">
                        {company && (
                            <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                                <div className="flex items-center mb-4">
                                    <img
                                        src={company.profileAttachmentUrl || "/assets/default-company.png"}
                                        alt={company.companyName}
                                        className="w-16 h-16 rounded-full object-cover mr-4"
                                    />
                                    <div>
                                        <h3 className="font-bold">{company.companyName}</h3>
                                        <p className="text-sm text-gray-500">{company.nickname || ''}</p>
                                    </div>
                                </div>

                                <div className="flex items-center mb-4">
                                    <div className="text-yellow-400 flex items-center">
                                        <i className="fas fa-star"></i>
                                        <i className="fas fa-star"></i>
                                        <i className="fas fa-star"></i>
                                        <i className="fas fa-star"></i>
                                        <i className="fas fa-star-half-alt"></i>
                                    </div>
                                    <span className="ml-1">4.5</span>
                                    <span className="text-sm text-gray-500 ml-2">(42개 리뷰)</span>
                                </div>

                                <div className="space-y-2 mb-4">
                                    <div className="flex">
                                        <div className="w-1/3 text-sm text-gray-500">평균 응답 시간</div>
                                        <div className="w-2/3 text-sm">1일 이내</div>
                                    </div>
                                    {company.categoryId && (
                                        <div className="flex">
                                            <div className="w-1/3 text-sm text-gray-500">전문 분야</div>
                                            <div className="w-2/3 text-sm">{company.categoryId}</div>
                                        </div>
                                    )}
                                    <div className="flex">
                                        <div className="w-1/3 text-sm text-gray-500">설립 연도</div>
                                        <div className="w-2/3 text-sm">{new Date(company.createdAt).getFullYear()}년</div>
                                    </div>
                                </div>

                                {product && (
                                    <div className="bg-gray-50 p-4 rounded-md mt-4">
                                        <h3 className="font-medium mb-1">{product.name}</h3>
                                        <p className="text-blue-600 font-bold">
                                            {product.price ? `₩${product.price.toLocaleString()}` : "문의 필요"}
                                        </p>
                                    </div>
                                )}
                            </div>
                        )}

                        <div className="bg-white rounded-lg shadow-sm p-6">
                            <h3 className="font-bold mb-4 pb-2 border-b border-gray-200">알림</h3>
                            <ul className="text-sm space-y-2">
                                <li className="flex items-start">
                                    <i className="fas fa-info-circle text-blue-500 mt-1 mr-2"></i>
                                    <span>문의 내용은 판매자와 회사만 확인할 수 있습니다.</span>
                                </li>
                                <li className="flex items-start">
                                    <i className="fas fa-info-circle text-blue-500 mt-1 mr-2"></i>
                                    <span>문의 후에는 취소가 불가능합니다.</span>
                                </li>
                                <li className="flex items-start">
                                    <i className="fas fa-info-circle text-blue-500 mt-1 mr-2"></i>
                                    <span>상담 후 최종 금액은 협의에 따라 달라질 수 있습니다.</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                {/* 나의 문의 내역 */}
                {myInquiries.length > 0 && (
                    <div className="mt-12">
                        <h2 className="text-xl font-bold mb-6">나의 문의 내역</h2>
                        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
                            <div className="overflow-x-auto">
                                <table className="min-w-full divide-y divide-gray-200">
                                    <thead className="bg-gray-50">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">날짜</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">문의 대상</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">문의 내용</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상태</th>
                                    </tr>
                                    </thead>
                                    <tbody className="bg-white divide-y divide-gray-200">
                                    {myInquiries.map((inquiry, index) => (
                                        <tr key={inquiry.id || index}>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {new Date(inquiry.editAddDate).toLocaleDateString()}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                                {inquiry.companyName || inquiry.companyId}
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-500 truncate max-w-xs">
                                                {inquiry.message}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                    <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">
                                                        대기중
                                                    </span>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                )}
            </div>

            <Footer />
        </div>
    );
};

export default InquiryPage;