// components/AuthContainer.jsx - 수정 버전
import React, { useState } from "react";
import LoginForm from "./LoginForm";
import SignupForm from "./SignupForm";

function AuthContainer() {
  const [activeTab, setActiveTab] = useState("login");

  return (
    <div className="pt-20 auth-container">
      <div className="container mx-auto px-4 flex justify-center items-center py-10">
        <div className="w-full max-w-md">
          {/* 탭 메뉴 */}
          <div className="flex border-b mb-6">
            <button
              onClick={() => setActiveTab("login")}
              className={`form-tab py-3 px-6 font-semibold text-lg w-1/2 text-center ${activeTab === "login" ? "active" : "text-gray-500"}`}
            >
              로그인
            </button>
            <button
              onClick={() => setActiveTab("signup")}
              className={`form-tab py-3 px-6 font-semibold text-lg w-1/2 text-center ${activeTab === "signup" ? "active" : "text-gray-500"}`}
            >
              회원가입
            </button>
          </div>

          {/* 로그인/회원가입 폼 */}
          {activeTab === "login" ? <LoginForm /> : <SignupForm />}
        </div>
      </div>
    </div>
  );
}

export default AuthContainer;
