"use client";

import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

const ProductDetailPage = () => {
  const { productId } = useParams();
  const [product, setProduct] = useState(null);
  const [company, setCompany] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [relatedProducts, setRelatedProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  // const [error, setError] = useState(null); // 필요하다면 사용

  useEffect(() => {
    const fetchProductData = async () => {
      setLoading(true);
      try {
        const productRes = await fetch(`/api/products/${productId}`);
        if (productRes.ok) {
          const productData = await productRes.json();
          setProduct(productData);

          if (productData.companyId) {
            const companyRes = await fetch(
              `/api/users/${productData.companyId}`
            );
            if (companyRes.ok) {
              const companyData = await companyRes.json();
              setCompany(companyData);
            } else {
              console.warn("회사 정보 API 응답 실패:", companyRes.status);
              setCompany(null);
            }
          } else {
            setCompany(null);
          }

          const reviewsRes = await fetch(`/api/reviews?productId=${productId}`);
          if (reviewsRes.ok) {
            const reviewsData = await reviewsRes.json();
            setReviews(reviewsData.items || reviewsData);
          } else {
            console.warn("리뷰 API 응답 실패:", reviewsRes.status);
            setReviews([]);
          }

          if (productData.categoryId) {
            const relatedRes = await fetch(
              `/api/products?categoryId=${productData.categoryId}&limit=4`
            );
            if (relatedRes.ok) {
              const relatedData = await relatedRes.json();
              const filteredRelatedProducts = (
                relatedData.items || relatedData
              ).filter((p) => p.id !== productId);
              setRelatedProducts(filteredRelatedProducts);
            } else {
              console.warn("관련 상품 API 응답 실패:", relatedRes.status);
              setRelatedProducts([]);
            }
          } else {
            setRelatedProducts([]);
          }
        } else {
          console.error("상품 API 응답 실패:", productRes.status);
          setProduct(null);
          setCompany(null);
          setReviews([]);
          setRelatedProducts([]);
        }
      } catch (error) {
        console.error("데이터 로딩 중 오류 발생:", error);
        // setError(error.message); // setError 상태가 있다면 사용
        setProduct(null);
        setCompany(null);
        setReviews([]);
        setRelatedProducts([]);
        setLoading(false);
      } finally {
        setLoading(false);
      }
    };

    fetchProductData();
  }, [productId]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!product) {
    return <div>Product not found.</div>;
  }

  return (
    <div>
      <h1>{product.name}</h1>
      <p>Description: {product.description}</p>
      <p>Price: ${product.price}</p>

      {company && (
        <div>
          <h2>Company: {company.name}</h2>
          <p>Company Description: {company.description}</p>
        </div>
      )}

      <h2>Reviews</h2>
      {reviews.length > 0 ? (
        <ul>
          {reviews.map((review) => (
            <li key={review.id}>
              {review.text} - {review.rating}
            </li>
          ))}
        </ul>
      ) : (
        <p>No reviews yet.</p>
      )}

      <h2>Related Products</h2>
      {relatedProducts.length > 0 ? (
        <ul>
          {relatedProducts.map((relatedProduct) => (
            <li key={relatedProduct.id}>{relatedProduct.name}</li>
          ))}
        </ul>
      ) : (
        <p>No related products.</p>
      )}
    </div>
  );
};

export default ProductDetailPage;
