// components/Navbar.jsx
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";

function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userEmail, setUserEmail] = useState("");
  const [showDropdown, setShowDropdown] = useState(false);

  useEffect(() => {
    // 세션스토리지에서 로그인 정보 확인
    const loginCompanyId = sessionStorage.getItem("loginCompanyId");
    if (loginCompanyId) {
      setIsLoggedIn(true);
      setUserEmail(loginCompanyId);
    }
  }, []);

  const handleLogout = () => {
    sessionStorage.removeItem("loginCompanyId");
    setIsLoggedIn(false);
    window.location.href = "/";
  };

  return (
    <nav className="bg-white shadow-md fixed w-full z-10">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center py-4">
          <div className="flex items-center space-x-8">
            <Link to="/main" className="font-bold text-2xl main-color-text">
              DataBridge
            </Link>
            <div className="hidden md:flex space-x-6">
              <Link
                to="#"
                className="text-gray-700 hover:main-color-text px-2 py-1 rounded transition duration-300"
              >
                기업 찾기
              </Link>
              <Link
                to="#"
                className="text-gray-700 hover:main-color-text px-2 py-1 rounded transition duration-300"
              >
                프로젝트
              </Link>
              <Link
                to="#"
                className="text-gray-700 hover:main-color-text px-2 py-1 rounded transition duration-300"
              >
                인기 서비스
              </Link>
              <Link
                to="#"
                className="text-gray-700 hover:main-color-text px-2 py-1 rounded transition duration-300"
              >
                커뮤니티
              </Link>
            </div>
          </div>
          <div className="flex items-center space-x-4">
            <div className="relative">
              <input
                type="text"
                placeholder="검색어를 입력하세요"
                className="px-4 py-2 rounded-full border focus:outline-none focus:ring-2 focus:ring-blue-500 w-64"
              />
              <button className="absolute right-3 top-2 text-gray-500">
                <i className="fas fa-search"></i>
              </button>
            </div>

            {isLoggedIn ? (
              <div className="relative">
                <button
                  onClick={() => setShowDropdown(!showDropdown)}
                  className="flex items-center text-gray-700 hover:text-gray-900 px-2 py-1 rounded transition duration-300"
                >
                  <span className="mr-1">{userEmail}</span>
                  <i className="fas fa-chevron-down text-xs"></i>
                </button>
                {showDropdown && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10">
                    <Link
                      to="/mypage"
                      className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    >
                      마이페이지
                    </Link>
                    <button
                      onClick={handleLogout}
                      className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    >
                      로그아웃
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <>
                <Link
                  to="/"
                  className="text-gray-700 hover:text-gray-900 px-2 py-1 rounded transition duration-300"
                >
                  로그인
                </Link>
                <Link
                  to="/"
                  className="main-color text-white px-4 py-2 rounded-md hover:bg-blue-700 transition duration-300"
                >
                  회원가입
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
