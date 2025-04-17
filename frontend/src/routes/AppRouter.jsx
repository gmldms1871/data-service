// src/routes/AppRouter.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './LoginPage';
import MainPage from '../pages/MainPage';
import MyPage from '../pages/MyPage';

export default function AppRouter() {
    const isLoggedIn = Boolean(sessionStorage.getItem('loginCompanyId'));

    return (
        <Router>
            <Routes>
                <Route path="/" element={<LoginPage />} />
                <Route path="/main" element={isLoggedIn ? <MainPage /> : <Navigate to="/" />} />
                <Route path="/mypage" element={isLoggedIn ? <MyPage /> : <Navigate to="/" />} />
            </Routes>
        </Router>
    );
}
