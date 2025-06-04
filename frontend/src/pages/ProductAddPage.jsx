"use client"

import { useState, useEffect, useCallback } from "react"
import { useNavigate } from "react-router-dom"
import DatePicker from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"

const ProductAddPage = () => {
  const navigate = useNavigate()

  const [activeTab, setActiveTab] = useState("product")
  const [categories, setCategories] = useState([])
  const [productForm, setProductForm] = useState({
    categoryId: "",
    name: "",
    description: "",
    price: "",
    imageUrl: "",
    companyId: sessionStorage.getItem("loginCompanyId") || "",
  })
  const [adForm, setAdForm] = useState({
    productId: "",
    startDate: new Date(),
    endDate: new Date(),
    budget: "",
  })
  const [myProducts, setMyProducts] = useState([]) // 새로 추가

  const fetchMyProducts = useCallback(async () => {
    // Define with useCallback
    const loginId = sessionStorage.getItem("loginCompanyId")
    if (!loginId) return

    try {
      const res = await fetch(`/api/products?companyId=${loginId}`)
      if (res.ok) {
        const data = await res.json()
        const productsArray = data.items || data
        setMyProducts(productsArray)
        if (productsArray.length > 0 && !adForm.productId) {
          // Set productId only if not already set
          setAdForm((prev) => ({
            ...prev,
            productId: productsArray[0].id,
          }))
        }
      } else {
        console.error("내 상품 목록 로딩 실패:", res.status)
        setMyProducts([])
      }
    } catch (error) {
      console.error("내 상품 로딩 중 오류:", error)
      setMyProducts([])
    }
  }, [adForm.productId]) // Add adForm.productId to dependencies if it influences the logic inside

  useEffect(() => {
    // 로그인 확인
    const loginCompanyId = sessionStorage.getItem("loginCompanyId")
    if (!loginCompanyId) {
      alert("로그인이 필요합니다.")
      navigate("/company/login")
      return
    }

    // 카테고리 목록 로드
    const fetchCategories = async () => {
      try {
        const res = await fetch("/api/categories")
        if (res.ok) {
          const data = await res.json()
          setCategories(data)
        } else {
          console.error("카테고리 로딩 실패:", res.status)
          setCategories([])
        }
      } catch (error) {
        console.error("카테고리 로딩 중 오류:", error)
        setCategories([]) // 에러 시 빈 배열로 설정
      }
    }

    fetchCategories()
    fetchMyProducts() // Call the memoized version

    // 오늘 날짜로 DatePicker 초기화
    setAdForm((prev) => ({
      ...prev,
      startDate: new Date(),
      endDate: new Date(),
    }))
  }, [navigate, fetchMyProducts]) // Add fetchMyProducts to useEffect dependencies

  const handleTabChange = (tab) => {
    setActiveTab(tab)
  }

  const handleProductFormChange = (e) => {
    const { name, value } = e.target
    setProductForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleAdFormChange = (e) => {
    const { name, value } = e.target
    setAdForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleStartDateChange = (date) => {
    setAdForm((prev) => ({
      ...prev,
      startDate: date,
    }))
  }

  const handleEndDateChange = (date) => {
    setAdForm((prev) => ({
      ...prev,
      endDate: date,
    }))
  }

  const handleSubmitProduct = async (e) => {
    e.preventDefault()
    try {
      const res = await fetch("/api/products", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(productForm),
      })

      if (res.ok) {
        alert("상품이 성공적으로 등록되었습니다.")
        setProductForm({
          categoryId: "",
          name: "",
          description: "",
          price: "",
          imageUrl: "",
          companyId: sessionStorage.getItem("loginCompanyId") || "",
        })
        fetchMyProducts() // Now this call is valid
      } else {
        alert("상품 등록에 실패했습니다.")
      }
    } catch (error) {
      console.error("상품 등록 중 오류:", error)
      alert("상품 등록 중 오류가 발생했습니다.")
    }
  }

  const handleSubmitAd = async (e) => {
    e.preventDefault()
    try {
      const res = await fetch("/api/ads", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ...adForm,
          startDate: adForm.startDate.toISOString(),
          endDate: adForm.endDate.toISOString(),
        }),
      })

      if (res.ok) {
        alert("광고가 성공적으로 등록되었습니다.")
        setAdForm({
          productId: "",
          startDate: new Date(),
          endDate: new Date(),
          budget: "",
        })
      } else {
        alert("광고 등록에 실패했습니다.")
      }
    } catch (error) {
      console.error("광고 등록 중 오류:", error)
      alert("광고 등록 중 오류가 발생했습니다.")
    }
  }

  return (
    <div className="container mx-auto mt-8">
      <h1 className="text-2xl font-bold mb-4">상품 및 광고 등록</h1>

      {/* 탭 메뉴 */}
      <div className="mb-4">
        <button
          className={`px-4 py-2 rounded-md ${activeTab === "product" ? "bg-blue-500 text-white" : "bg-gray-200"}`}
          onClick={() => handleTabChange("product")}
        >
          상품 등록
        </button>
        <button
          className={`px-4 py-2 rounded-md ${activeTab === "ad" ? "bg-blue-500 text-white" : "bg-gray-200"}`}
          onClick={() => handleTabChange("ad")}
        >
          광고 등록
        </button>
      </div>

      {/* 상품 등록 탭 */}
      {activeTab === "product" && (
        <div className="bg-white shadow-md rounded-md p-4">
          <h2 className="text-xl font-semibold mb-4">상품 등록</h2>
          <form onSubmit={handleSubmitProduct}>
            <div className="mb-4">
              <label htmlFor="categoryId" className="block text-gray-700 text-sm font-bold mb-2">
                카테고리
              </label>
              <select
                id="categoryId"
                name="categoryId"
                value={productForm.categoryId}
                onChange={handleProductFormChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="">선택해주세요</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="mb-4">
              <label htmlFor="name" className="block text-gray-700 text-sm font-bold mb-2">
                상품명
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={productForm.name}
                onChange={handleProductFormChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
            <div className="mb-4">
              <label htmlFor="description" className="block text-gray-700 text-sm font-bold mb-2">
                상품 설명
              </label>
              <textarea
                id="description"
                name="description"
                value={productForm.description}
                onChange={handleProductFormChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
            <div className="mb-4">
              <label htmlFor="price" className="block text-gray-700 text-sm font-bold mb-2">
                가격
              </label>
              <input
                type="number"
                id="price"
                name="price"
                value={productForm.price}
                onChange={handleProductFormChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
            <div className="mb-4">
              <label htmlFor="imageUrl" className="block text-gray-700 text-sm font-bold mb-2">
                이미지 URL
              </label>
              <input
                type="text"
                id="imageUrl"
                name="imageUrl"
                value={productForm.imageUrl}
                onChange={handleProductFormChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
            <button
              type="submit"
              className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md focus:outline-none focus:shadow-outline"
            >
              등록하기
            </button>
          </form>
        </div>
      )}

      {/* 광고 등록 탭 */}
      {activeTab === "ad" && (
        <div className="bg-white shadow-md rounded-md p-4">
          <h2 className="text-xl font-semibold mb-4">광고 등록</h2>
          <form onSubmit={handleSubmitAd}>
            <div className="mb-4">
              <label htmlFor="productId" className="block text-gray-700 text-sm font-bold mb-2">
                광고할 상품 선택
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
                {myProducts.length > 0 ? (
                  myProducts.map((product) => (
                    <option key={product.id} value={product.id}>
                      {product.name}
                    </option>
                  ))
                ) : (
                  <option value="" disabled>
                    등록된 상품이 없습니다.
                  </option>
                )}
              </select>
            </div>
            <div className="mb-4">
              <label htmlFor="startDate" className="block text-gray-700 text-sm font-bold mb-2">
                시작일
              </label>
              <DatePicker
                selected={adForm.startDate}
                onChange={handleStartDateChange}
                dateFormat="yyyy-MM-dd"
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="mb-4">
              <label htmlFor="endDate" className="block text-gray-700 text-sm font-bold mb-2">
                종료일
              </label>
              <DatePicker
                selected={adForm.endDate}
                onChange={handleEndDateChange}
                dateFormat="yyyy-MM-dd"
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="mb-4">
              <label htmlFor="budget" className="block text-gray-700 text-sm font-bold mb-2">
                예산
              </label>
              <input
                type="number"
                id="budget"
                name="budget"
                value={adForm.budget}
                onChange={handleAdFormChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
            <button
              type="submit"
              className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md focus:outline-none focus:shadow-outline"
            >
              등록하기
            </button>
          </form>
        </div>
      )}
    </div>
  )
}

export default ProductAddPage
