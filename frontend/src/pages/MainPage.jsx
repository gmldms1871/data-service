// src/pages/MainPage.jsx
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";

const MainPage = () => {
  const [featuredCompanies, setFeaturedCompanies] = useState([]);
  const [recommendedProducts, setRecommendedProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        // 인기 기업 가져오기 (API가 있다면 API 호출)
        try {
          const companiesRes = await fetch("/api/company/readMany");
          if (companiesRes.ok) {
            const companiesData = await companiesRes.json();
            // "삭제된 기업"은 제외
            const validCompanies = companiesData.companies.filter(
              (company) => company.companyName !== "삭제된 기업"
            );

            setFeaturedCompanies(validCompanies);
          }
        } catch (error) {
          console.error("인기 기업 로딩 오류:", error);
          // 에러 발생 시 더미 데이터 사용
          createDummyCompanies();
        }

        // 추천 상품 가져오기
        try {
          const productsRes = await fetch("/api/product/readMany");
          if (productsRes.ok) {
            const productsData = await productsRes.json();
            setRecommendedProducts(productsData);
          }
        } catch (error) {
          console.error("추천 상품 로딩 오류:", error);
          // 에러 발생 시 더미 데이터 사용
          createDummyProducts();
        }

        // 카테고리 가져오기
        try {
          const categoriesRes = await fetch("/api/categories");
          if (categoriesRes.ok) {
            const categoriesData = await categoriesRes.json();
            setCategories(categoriesData);
          }
        } catch (error) {
          console.error("카테고리 로딩 오류:", error);
          // 에러 발생 시 더미 카테고리 사용
          setCategories([]);
        }

        setLoading(false);
      } catch (error) {
        console.error("데이터 로딩 중 오류:", error);
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const createDummyCompanies = () => {
    const dummyCompanies = [
      {
        id: "company-1",
        companyName: "디자인 스튜디오",
        nickname: "브랜딩 디자인 전문",
        rating: 4.9,
        reviewCount: 42,
        tags: ["로고", "브랜딩"],
        profileAttachmentUrl: null,
      },
      {
        id: "company-2",
        companyName: "웹 개발 전문가",
        nickname: "웹사이트 개발과 유지보수",
        rating: 4.8,
        reviewCount: 36,
        tags: ["웹개발", "반응형"],
        profileAttachmentUrl: null,
      },
      {
        id: "company-3",
        companyName: "마케팅 에이전시",
        nickname: "디지털 마케팅 전략 수립",
        rating: 4.7,
        reviewCount: 28,
        tags: ["SNS", "광고"],
        profileAttachmentUrl: null,
      },
      {
        id: "company-4",
        companyName: "콘텐츠 크리에이터",
        nickname: "영상 제작 및 편집",
        rating: 4.6,
        reviewCount: 24,
        tags: ["영상", "모션그래픽"],
        profileAttachmentUrl: null,
      },
    ];

    setFeaturedCompanies(dummyCompanies);
  };

  const createDummyProducts = () => {
    const dummyProducts = [
      {
        id: "product-1",
        name: "브랜드 로고 디자인 패키지",
        companyName: "디자인 스튜디오",
        companyId: "company-1",
        rating: 4.9,
        reviewCount: 42,
        price: 150000,
        mainImage: null,
      },
      {
        id: "product-2",
        name: "반응형 랜딩페이지 제작",
        companyName: "웹 개발 전문가",
        companyId: "company-2",
        rating: 4.8,
        reviewCount: 36,
        price: 300000,
        mainImage: null,
      },
      {
        id: "product-3",
        name: "SNS 마케팅 컨설팅",
        companyName: "마케팅 에이전시",
        companyId: "company-3",
        rating: 4.7,
        reviewCount: 28,
        price: 200000,
        mainImage: null,
      },
      {
        id: "product-4",
        name: "유튜브 인트로 모션그래픽",
        companyName: "콘텐츠 크리에이터",
        companyId: "company-4",
        rating: 4.6,
        reviewCount: 24,
        price: 120000,
        mainImage: null,
      },
    ];

    setRecommendedProducts(dummyProducts);
  };

  return (
    <div className="bg-white min-h-screen">
      <Navbar />

      {/* 메인 배너 */}
      <div
        className="relative bg-gray-100 flex items-center"
        style={{ height: "400px" }}
      >
        <div className="container mx-auto px-4">
          <div className="w-full md:w-1/2">
            <h1 className="text-4xl font-bold mb-4">
              원하는 서비스를
              <br />
              자유롭게 찾아보세요
            </h1>
            <p className="text-lg text-gray-700 mb-8">
              전문가와 함께 프로젝트를 성공적으로 완수하세요
            </p>
            <div className="flex flex-col md:flex-row gap-4">
              <form className="flex-grow">
                <div className="relative">
                  <input
                    type="text"
                    name="search"
                    className="w-full px-4 py-3 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="어떤 서비스가 필요하신가요?"
                  />
                  <button
                    type="submit"
                    className="absolute right-2 top-3 text-gray-500"
                  >
                    <i className="fas fa-search"></i>
                  </button>
                </div>
              </form>
              <Link className="bg-blue-600 text-white px-6 py-3 rounded-md hover:bg-blue-700 transition duration-300 text-center font-semibold">
                상품 등록하기
              </Link>
            </div>
          </div>
        </div>
        <div className="hidden md:block absolute right-0 top-0 h-full w-1/2 bg-blue-50">
          <img
            src="/assets/main-banner.jpg"
            alt="메인 배너 이미지"
            className="h-full w-full object-cover"
          />
        </div>
      </div>

      {/* 카테고리 섹션 */}
      <div className="container mx-auto py-12 px-4">
        <h2 className="text-2xl font-bold mb-8">카테고리</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-4">
          {categories.length > 0
            ? categories.map((category) => (
                <Link
                  key={category.id}
                  className="flex flex-col items-center p-4 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition duration-300"
                >
                  <div className="bg-blue-100 p-3 rounded-full mb-3">
                    <i
                      className={`fas fa-${category.icon || "star"} text-blue-600 text-xl`}
                    ></i>
                  </div>
                  <span className="text-sm text-center">{category.name}</span>
                </Link>
              ))
            : // 더미 카테고리
              [
                { id: "it", name: "IT 개발", icon: "laptop-code" },
                { id: "design", name: "디자인", icon: "palette" },
                { id: "marketing", name: "마케팅", icon: "bullhorn" },
                { id: "video", name: "영상", icon: "video" },
                { id: "translation", name: "번역", icon: "language" },
                { id: "content", name: "콘텐츠", icon: "pencil-alt" },
                { id: "business", name: "비즈니스", icon: "chart-line" },
                { id: "more", name: "더보기", icon: "ellipsis-h" },
              ].map((category) => (
                <Link
                  key={category.id}
                  className="flex flex-col items-center p-4 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition duration-300"
                >
                  <div className="bg-blue-100 p-3 rounded-full mb-3">
                    <i
                      className={`fas fa-${category.icon} text-blue-600 text-xl`}
                    ></i>
                  </div>
                  <span className="text-sm text-center">{category.name}</span>
                </Link>
              ))}
        </div>
      </div>

      {/* 인기 기업 섹션 */}
      <div className="bg-gray-50 py-12">
        <div className="container mx-auto px-4">
          <div className="flex justify-between items-center mb-8">
            <h2 className="text-2xl font-bold">기업</h2>
            <Link className="text-sm text-blue-600 hover:underline">
              전체보기
            </Link>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {featuredCompanies.map((company) => (
              <div
                key={company.id}
                className="bg-white rounded-lg overflow-hidden shadow-md hover:shadow-lg transition duration-300"
              >
                <Link>
                  <img
                    src={
                      company.profileAttachmentUrl ||
                      "/assets/default-company.png"
                    }
                    alt={company.companyName}
                    className="w-full h-48 object-cover"
                  />
                  <div className="p-4">
                    <div className="flex items-center justify-between">
                      <h3 className="font-bold">{company.companyName}</h3>
                      <div className="flex items-center">
                        <i className="fas fa-star text-yellow-400"></i>
                        <span className="ml-1 text-sm">
                          {company.rating?.toFixed(1)}
                        </span>
                      </div>
                    </div>
                    <p className="text-gray-600 text-sm mt-2">
                      {company.nickname}
                    </p>
                    <div className="flex mt-3">
                      {company.tags &&
                        company.tags.map((tag, idx) => (
                          <span
                            key={idx}
                            className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded mr-2"
                          >
                            {tag}
                          </span>
                        ))}
                    </div>
                  </div>
                </Link>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* 추천 상품 섹션 */}
      <div className="container mx-auto py-12 px-4">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-2xl font-bold">상품</h2>
          <Link className="text-sm text-blue-600 hover:underline">
            전체보기
          </Link>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {recommendedProducts.map((product) => (
            <div
              key={product.id}
              className="bg-white rounded-lg overflow-hidden shadow-md hover:shadow-lg transition duration-300"
            >
              <Link>
                <img
                  src={product.mainImage || "/assets/default-product.png"}
                  alt={product.name}
                  className="w-full h-48 object-cover"
                />
                <div className="p-4">
                  <div className="flex items-center text-sm text-gray-500 mb-2">
                    <img
                      src="/assets/default-company.png"
                      alt={product.companyName}
                      className="w-6 h-6 rounded-full mr-2"
                    />
                    <span>{product.companyName}</span>
                  </div>
                  <h3 className="font-medium mb-2 line-clamp-2 h-12">
                    {product.name}
                  </h3>
                  <div className="flex items-center mb-3">
                    <div className="text-yellow-400 flex items-center text-sm">
                      <i className="fas fa-star mr-1"></i>
                      <span>{product.rating?.toFixed(1)}</span>
                    </div>
                    <span className="text-gray-400 text-xs ml-2">
                      ({product.reviewCount}개 리뷰)
                    </span>
                  </div>
                  <div className="flex justify-between items-center">
                    <p className="font-bold text-blue-600">
                      {product.price && `₩${product.price.toLocaleString()}`}
                    </p>
                    <span className="text-sm text-blue-600 border border-blue-500 px-2 py-1 rounded hover:bg-blue-50 transition duration-300">
                      문의하기
                    </span>
                  </div>
                </div>
              </Link>
            </div>
          ))}
        </div>
      </div>

      {/* 서비스 이용 방법 */}
      <div className="bg-gray-50 py-12">
        <div className="container mx-auto px-4">
          <h2 className="text-2xl font-bold mb-8 text-center">
            서비스 이용 방법
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white p-6 rounded-lg shadow-md text-center">
              <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <i className="fas fa-search text-blue-600 text-2xl"></i>
              </div>
              <h3 className="font-bold text-lg mb-2">서비스 검색</h3>
              <p className="text-gray-600">
                원하는 서비스와 전문가를 검색하고 찾아보세요.
              </p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow-md text-center">
              <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <i className="fas fa-comment-alt text-blue-600 text-2xl"></i>
              </div>
              <h3 className="font-bold text-lg mb-2">문의하기</h3>
              <p className="text-gray-600">
                마음에 드는 서비스에 견적을 요청하고 의뢰하세요.
              </p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow-md text-center">
              <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <i className="fas fa-check-circle text-blue-600 text-2xl"></i>
              </div>
              <h3 className="font-bold text-lg mb-2">거래 완료</h3>
              <p className="text-gray-600">
                서비스 완료 후 리뷰를 남기고 다음 프로젝트를 진행하세요.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* 광고 배너 */}
      <div className="container mx-auto py-12 px-4">
        <div className="bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg p-8 text-white">
          <div className="flex flex-col md:flex-row items-center justify-between">
            <div className="mb-6 md:mb-0">
              <h3 className="text-2xl font-bold mb-2">
                전문가로 등록하고 수익을 창출하세요
              </h3>
              <p className="text-white text-opacity-90">
                프리랜서로 활동하며 더 많은 고객을 만나보세요.
              </p>
            </div>
            <Link className="bg-white text-blue-600 px-6 py-3 rounded-md font-bold hover:bg-gray-100 transition duration-300">
              상품 등록하기
            </Link>
          </div>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default MainPage;
