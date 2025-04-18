import React from 'react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

//ToDo 메인페이지 접근이 로그인 안했을 때에도 접근이 가능함(navbar에 로고 눌렀을 때)

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
