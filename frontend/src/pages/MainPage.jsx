"use client";

import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

const MainPage = () => {
  const [featuredCompanies, setFeaturedCompanies] = useState([]);
  const [recommendedProducts, setRecommendedProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        const apiHost = process.env.REACT_APP_API_HOST;

        // 인기 기업 가져오기
        try {
          const companiesRes = await fetch(
            `${apiHost}/api/users?sort=rating&limit=4`
          );
          if (companiesRes.ok) {
            const companiesData = await companiesRes.json();
            // API 응답이 { data: [...] } 형태라고 가정
            setFeaturedCompanies(
              Array.isArray(companiesData.data) ? companiesData.data : []
            );
          } else {
            console.error("인기 기업 API 응답 실패:", companiesRes.status);
            setFeaturedCompanies([]);
            setError((prevError) => ({
              ...prevError,
              companies: `인기 기업 API 응답 실패: ${companiesRes.status}`,
            }));
          }
        } catch (networkError) {
          console.error("인기 기업 로딩 중 네트워크 오류:", networkError);
          setFeaturedCompanies([]);
          setError((prevError) => ({
            ...prevError,
            companies: "인기 기업 로딩 중 네트워크 오류",
          }));
        }

        // 추천 상품 가져오기
        try {
          const productsRes = await fetch(`${apiHost}/api/products?limit=4`); // 추천 상품 API 엔드포인트 확인 필요
          if (productsRes.ok) {
            const productsData = await productsRes.json();
            // API 응답이 { data: [...] } 형태라고 가정
            setRecommendedProducts(
              Array.isArray(productsData.data) ? productsData.data : []
            );
          } else {
            console.error("추천 상품 API 응답 실패:", productsRes.status);
            setRecommendedProducts([]);
            setError((prevError) => ({
              ...prevError,
              products: `추천 상품 API 응답 실패: ${productsRes.status}`,
            }));
          }
        } catch (networkError) {
          console.error("추천 상품 로딩 중 네트워크 오류:", networkError);
          setRecommendedProducts([]);
          setError((prevError) => ({
            ...prevError,
            products: "추천 상품 로딩 중 네트워크 오류",
          }));
        }

        // 카테고리 가져오기
        try {
          const categoriesRes = await fetch(`${apiHost}/api/categories`); // 카테고리 API 엔드포인트 확인 필요
          if (categoriesRes.ok) {
            const categoriesData = await categoriesRes.json();
            // 카테고리 API가 배열을 직접 반환하거나 { data: [...] } 형태일 수 있음.
            // 여기서는 categoriesData가 배열이거나 categoriesData.data가 배열이라고 가정
            if (Array.isArray(categoriesData)) {
              setCategories(categoriesData);
            } else if (categoriesData && Array.isArray(categoriesData.data)) {
              setCategories(categoriesData.data);
            } else {
              setCategories([]);
            }
          } else {
            console.error("카테고리 API 응답 실패:", categoriesRes.status);
            setCategories([]);
            setError((prevError) => ({
              ...prevError,
              categories: `카테고리 API 응답 실패: ${categoriesRes.status}`,
            }));
          }
        } catch (networkError) {
          console.error("카테고리 로딩 중 네트워크 오류:", networkError);
          setCategories([]);
          setError((prevError) => ({
            ...prevError,
            categories: "카테고리 로딩 중 네트워크 오류",
          }));
        }

        setLoading(false);
      } catch (fetchDataError) {
        console.error("데이터 로딩 중 전체 오류 발생:", fetchDataError);
        setError({ general: "데이터 로딩 중 오류가 발생했습니다." });
        setFeaturedCompanies([]);
        setRecommendedProducts([]);
        setCategories([]);
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error && Object.keys(error).length > 0) {
    // error 객체가 null이 아니고, 하나 이상의 오류 속성을 가지고 있다면
    return (
      <div className="container mx-auto p-4 text-center">
        <h1 className="text-2xl font-bold text-red-600 mb-4">
          데이터 로딩 중 오류 발생
        </h1>
        {error.general && <p className="text-red-500">{error.general}</p>}
        {error.companies && (
          <p className="text-red-500">인기 기업: {error.companies}</p>
        )}
        {error.products && (
          <p className="text-red-500">추천 상품: {error.products}</p>
        )}
        {error.categories && (
          <p className="text-red-500">카테고리: {error.categories}</p>
        )}
        <p className="mt-4">잠시 후 다시 시도해주세요.</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6 text-center">메인 페이지</h1>

      <section className="mb-8">
        <h2 className="text-2xl font-semibold mb-4">인기 기업</h2>
        {featuredCompanies.length > 0 ? (
          <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {featuredCompanies.map((company) => (
              <li
                key={company.id || company.companyId}
                className="border p-4 rounded shadow hover:shadow-lg transition-shadow"
              >
                <Link
                  to={`/company/${company.id || company.companyId}`}
                  className="text-blue-600 hover:underline"
                >
                  {company.name || company.companyName}
                </Link>
                {/* 추가 정보 표시 가능: company.rating 등 */}
              </li>
            ))}
          </ul>
        ) : (
          <p>인기 기업 정보를 불러올 수 없습니다.</p>
        )}
      </section>

      <section className="mb-8">
        <h2 className="text-2xl font-semibold mb-4">추천 상품</h2>
        {recommendedProducts.length > 0 ? (
          <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {recommendedProducts.map((product) => (
              <li
                key={product.id || product.productId}
                className="border p-4 rounded shadow hover:shadow-lg transition-shadow"
              >
                <Link
                  to={`/product/${product.id || product.productId}`}
                  className="text-blue-600 hover:underline"
                >
                  {product.name || product.productName}
                </Link>
                {/* 추가 정보 표시 가능: product.price 등 */}
              </li>
            ))}
          </ul>
        ) : (
          <p>추천 상품 정보를 불러올 수 없습니다.</p>
        )}
      </section>

      <section>
        <h2 className="text-2xl font-semibold mb-4">카테고리</h2>
        {categories.length > 0 ? (
          <ul className="flex flex-wrap gap-2">
            {categories.map((category) => (
              <li
                key={category.id || category.categoryId}
                className="border px-3 py-1 rounded bg-gray-100 hover:bg-gray-200 transition-colors"
              >
                <Link
                  to={`/category/${category.id || category.categoryId}`}
                  className="text-gray-700"
                >
                  {category.name || category.categoryName}
                </Link>
              </li>
            ))}
          </ul>
        ) : (
          <p>카테고리 정보를 불러올 수 없습니다.</p>
        )}
      </section>
    </div>
  );
};

export default MainPage;
