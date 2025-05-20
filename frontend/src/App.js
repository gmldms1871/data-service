// src/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginSignupPage from './pages/LoginSignupPage';
import MainPage from './pages/MainPage';
import MyPage from './pages/MyPage';
import CompanyDetailPage from './pages/CompanyDetailPage';
import ProductDetailPage from './pages/ProductDetailPage';
import ProductListPage from './pages/ProductListPage';
import InquiryPage from './pages/InquiryPage';
import ProductAddPage from './pages/ProductAddPage';
import CompanyListPage from './pages/CompanyListPage';

// 로그인 상태에 따른 보호된 라우트 컴포넌트
const ProtectedRoute = ({ children }) => {
    const isLoggedIn = sessionStorage.getItem('loginCompanyId');
    if (!isLoggedIn) {
        // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        return <Navigate to="/" replace />;
    }
    return children;
};

function App() {
    return (
        <Router>
            <Routes>
                {/* 공개 라우트 */}
                <Route path="/" element={<LoginSignupPage />} />
                <Route path="/main" element={<MainPage />} />
                <Route path="/companies" element={<CompanyListPage />} />
                <Route path="/companies/:companyId" element={<CompanyDetailPage />} />
                <Route path="/products" element={<ProductListPage />} />
                <Route path="/products/:productId" element={<ProductDetailPage />} />

                {/* 보호된 라우트 */}
                <Route path="/mypage" element={
                    <ProtectedRoute>
                        <MyPage />
                    </ProtectedRoute>
                } />
                <Route path="/inquiry" element={
                    <ProtectedRoute>
                        <InquiryPage />
                    </ProtectedRoute>
                } />
                <Route path="/products/add" element={
                    <ProtectedRoute>
                        <ProductAddPage />
                    </ProtectedRoute>
                } />

                {/* 존재하지 않는 페이지 처리 */}
                <Route path="*" element={<Navigate to="/main" replace />} />
            </Routes>
        </Router>
    );
}

export default App;