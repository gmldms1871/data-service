// App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginSignupPage from './pages/LoginSignupPage';
import MainPage from './pages/MainPage';
import MyPage from './pages/MyPage';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<LoginSignupPage />} />
                <Route path="/main" element={<MainPage />} />
                <Route path="/mypage" element={<MyPage />} />
            </Routes>
        </Router>
    );
}

export default App;