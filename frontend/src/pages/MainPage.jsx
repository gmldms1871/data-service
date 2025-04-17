import React from 'react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

function MainPage() {
    return (
        <div className="bg-gray-50 font-sans">
            <Navbar />
            <div className="pt-20 min-h-screen flex items-center justify-center">
                <div className="text-center">
                    <h1 className="text-4xl font-bold text-gray-800 mb-4">메인 페이지</h1>
                    <p className="text-gray-600">로그인에 성공하셨습니다!</p>
                </div>
            </div>
            <Footer />
        </div>
    );
}

export default MainPage;
