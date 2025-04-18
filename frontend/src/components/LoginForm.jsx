import React, { useState } from 'react';

function LoginForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const res = await fetch(`${process.env.REACT_APP_API_HOST}/api/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include', // 세션 유지
                body: JSON.stringify({ email, password })
            });

            const text = await res.text();
            if (res.ok) {
                alert('✅ 로그인 성공');
                sessionStorage.setItem('loginCompanyId', email);
                window.location.href = "/main";
                console.log(text);
            } else {
                alert(`❌ 로그인 실패: ${text}`);
            }
        } catch (err) {
            console.error(err);
            alert('서버 오류');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md">
            <h2 className="text-2xl font-bold mb-6 text-center">환영합니다!</h2>

            <form onSubmit={handleLogin}>
                <div className="mb-4">
                    <label htmlFor="email" className="block text-gray-700 font-medium mb-2">이메일</label>
                    <input
                        type="email"
                        id="email"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="이메일 주소를 입력하세요"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-6">
                    <label htmlFor="password" className="block text-gray-700 font-medium mb-2">비밀번호</label>
                    <input
                        type="password"
                        id="password"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="비밀번호를 입력하세요"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <div className="flex justify-between items-center mb-6">
                    <div className="flex items-center">
                        <input type="checkbox" id="remember" className="mr-2" />
                        <label htmlFor="remember" className="text-gray-600 text-sm">자동 로그인</label>
                    </div>
                    <a href="#" className="text-sm text-gray-600 hover:text-blue-600">비밀번호를 잊으셨나요?</a>
                </div>
                <button type="submit" className="main-color text-white py-3 px-4 rounded-md w-full font-semibold hover:bg-blue-700 transition duration-300">로그인</button>
            </form>
        </div>
    );
}
export default LoginForm;
