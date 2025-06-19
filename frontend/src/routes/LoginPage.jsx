// src/routes/LoginPage.jsx
import React from "react";
import LoginForm from "../components/LoginForm";
import SignupForm from "../components/SignupForm";

const LoginPage = () => {
  return (
    <div className="auth-container">
      <div className="login-box">
        <h2>로그인</h2>
        <LoginForm />
      </div>
      <div className="signup-box">
        <h2>회원가입</h2>
        <SignupForm />
      </div>
    </div>
  );
};

export default LoginPage;
