"use client"

import { useState, useEffect } from "react"
import { Link } from "react-router-dom"

const InquiryPage = () => {
  const [company, setCompany] = useState(null)
  const [product, setProduct] = useState(null)
  const [myInquiries, setMyInquiries] = useState([])
  const [loading, setLoading] = useState(true)

  // Replace with actual user ID retrieval logic
  const loggedInId = 1
  const companyId = 1
  const productId = 1

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)

        if (companyId) {
          const companyRes = await fetch(`/api/users/${companyId}`)
          if (companyRes.ok) {
            const companyData = await companyRes.json()
            setCompany(companyData)
          } else {
            console.warn("회사 정보 API 응답 실패:", companyRes.status)
            setCompany(null)
          }
        }

        if (productId) {
          const productRes = await fetch(`/api/products/${productId}`)
          if (productRes.ok) {
            const productData = await productRes.json()
            setProduct(productData)

            // Fetch company info related to the product (example)
            if (productData.companyId) {
              const productCompanyRes = await fetch(`/api/users/${productData.companyId}`)
              if (productCompanyRes.ok) {
                const productCompanyData = await productCompanyRes.json()
                // You might want to store this separately or merge with existing company data
                console.log("Product Company Data:", productCompanyData)
              } else {
                console.warn("상품에 연결된 회사 정보 API 응답 실패:", productCompanyRes.status)
              }
            }
          } else {
            console.warn("상품 정보 API 응답 실패:", productRes.status)
            setProduct(null)
          }
        }

        const myInquiriesRes = await fetch(`/api/inquiries?userId=${loggedInId}`)
        if (myInquiriesRes.ok) {
          const myInquiriesData = await myInquiriesRes.json()
          setMyInquiries(myInquiriesData.items || myInquiriesData)
        } else {
          console.warn("내 문의 내역 API 응답 실패:", myInquiriesRes.status)
          setMyInquiries([])
        }
      } catch (error) {
        console.error("데이터 로딩 중 오류:", error)
        setCompany(null)
        setProduct(null)
        setMyInquiries([])
        setLoading(false)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [loggedInId, companyId, productId])

  if (loading) {
    return <div>Loading...</div>
  }

  return (
    <div>
      <h1>Inquiry Page</h1>

      {company && (
        <div>
          <h2>Company Information</h2>
          <p>Name: {company.name}</p>
          {/* Display other company details */}
        </div>
      )}

      {product && (
        <div>
          <h2>Product Information</h2>
          <p>Name: {product.name}</p>
          {/* Display other product details */}
        </div>
      )}

      <div>
        <h2>My Inquiries</h2>
        {myInquiries.length > 0 ? (
          <ul>
            {myInquiries.map((inquiry) => (
              <li key={inquiry.id}>
                <Link to={`/inquiry/${inquiry.id}`}>Inquiry ID: {inquiry.id}</Link>
              </li>
            ))}
          </ul>
        ) : (
          <p>No inquiries found.</p>
        )}
      </div>
    </div>
  )
}

export default InquiryPage
