import React, { useState } from 'react';

function SignupForm() {
    const [form, setForm] = useState({
        email: '',
        password: '',
        confirmPassword: '',
        userName: '',
        nickname: '',
        userPhone: '',
        companyName: '',
        businessNumber: '',
        homepage: '',
        interestTopic: '',
        categoryId: '',
        role: 'client',
        interests: {
            design: false,
            it: false,
            marketing: false,
            video: false
        },
        agreeTerms: false
    });

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;

        if (type === 'checkbox') {
            if (name.startsWith('interest-')) {
                const interest = name.replace('interest-', '');
                setForm(prev => ({
                    ...prev,
                    interests: {
                        ...prev.interests,
                        [interest]: checked
                    }
                }));
            } else {
                setForm(prev => ({ ...prev, [name]: checked }));
            }
        } else {
            setForm(prev => ({ ...prev, [name]: value }));
        }
    };

    const handleSignup = async (e) => {
        e.preventDefault();

        // 비밀번호 확인 체크
        if (form.password !== form.confirmPassword) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        // 이용약관 동의 체크
        if (!form.agreeTerms) {
            alert('이용약관에 동의해주세요.');
            return;
        }

        try {
            // 서버에 전송할 데이터 가공
            const dataToSend = {
                email: form.email,
                password: form.password,
                userName: form.userName,
                nickname: form.nickname,
                userPhone: form.userPhone,
                companyName: form.companyName,
                businessNumber: form.businessNumber,
                homepage: form.homepage,
                interestTopic: form.interestTopic,
                categoryId: form.categoryId,
                role: form.role,
                interests: Object.keys(form.interests).filter(key => form.interests[key])
            };

            const res = await fetch(`${process.env.REACT_APP_API_HOST}/api/company/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(dataToSend),
            });

            const result = await res.text();
            if (res.ok) {
                alert('✅ 회원가입 성공');
                console.log(result);
            } else {
                alert(`❌ 회원가입 실패: ${result}`);
            }
        } catch (err) {
            console.error(err);
            alert('서버 오류');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md">
            <h2 className="text-2xl font-bold mb-6 text-center">회원가입</h2>

            <form onSubmit={handleSignup}>
                <div className="mb-4">
                    <label htmlFor="signup-email" className="block text-gray-700 font-medium mb-2">이메일</label>
                    <input
                        type="email"
                        id="signup-email"
                        name="email"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="이메일 주소를 입력하세요"
                        value={form.email}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="signup-password" className="block text-gray-700 font-medium mb-2">비밀번호</label>
                    <input
                        type="password"
                        id="signup-password"
                        name="password"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="비밀번호를 입력하세요 (8자 이상)"
                        value={form.password}
                        onChange={handleChange}
                        required
                    />
                    <p className="text-xs text-gray-500 mt-1">영문, 숫자를 포함한 8자 이상의 비밀번호를 입력해주세요.</p>
                </div>
                <div className="mb-4">
                    <label htmlFor="signup-confirm-password" className="block text-gray-700 font-medium mb-2">비밀번호 확인</label>
                    <input
                        type="password"
                        id="signup-confirm-password"
                        name="confirmPassword"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="비밀번호를 다시 입력하세요"
                        value={form.confirmPassword}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="username" className="block text-gray-700 font-medium mb-2">이름</label>
                    <input
                        type="text"
                        id="username"
                        name="userName"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="이름을 입력하세요"
                        value={form.userName}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="nickname" className="block text-gray-700 font-medium mb-2">닉네임</label>
                    <input
                        type="text"
                        id="nickname"
                        name="nickname"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="닉네임을 입력하세요"
                        value={form.nickname}
                        onChange={handleChange}
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="phone" className="block text-gray-700 font-medium mb-2">휴대폰 번호</label>
                    <input
                        type="tel"
                        id="phone"
                        name="userPhone"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="'-' 없이 번호만 입력하세요"
                        value={form.userPhone}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="companyName" className="block text-gray-700 font-medium mb-2">기업명</label>
                    <input
                        type="text"
                        id="companyName"
                        name="companyName"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="기업명을 입력하세요"
                        value={form.companyName}
                        onChange={handleChange}
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="businessNumber" className="block text-gray-700 font-medium mb-2">사업자번호</label>
                    <input
                        type="text"
                        id="businessNumber"
                        name="businessNumber"
                        className="input-field w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="사업자번호를 입력하세요"
                        value={form.businessNumber}
                        onChange={handleChange}
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 font-medium mb-2">회원 유형</label>
                    <div className="flex space-x-4">
                        <div className="flex items-center">
                            <input
                                type="radio"
                                id="role-client"
                                name="role"
                                value="client"
                                className="mr-2"
                                checked={form.role === 'client'}
                                onChange={handleChange}
                            />
                            <label htmlFor="role-client" className="text-gray-600">클라이언트</label>
                        </div>
                        <div className="flex items-center">
                            <input
                                type="radio"
                                id="role-freelancer"
                                name="role"
                                value="freelancer"
                                className="mr-2"
                                checked={form.role === 'freelancer'}
                                onChange={handleChange}
                            />
                            <label htmlFor="role-freelancer" className="text-gray-600">프리랜서/기업</label>
                        </div>
                    </div>
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 font-medium mb-2">관심 분야 (선택사항)</label>
                    <div className="grid grid-cols-2 gap-2">
                        <div className="flex items-center">
                            <input
                                type="checkbox"
                                id="interest-design"
                                name="interest-design"
                                className="mr-2"
                                checked={form.interests.design}
                                onChange={handleChange}
                            />
                            <label htmlFor="interest-design" className="text-gray-600">디자인</label>
                        </div>
                        <div className="flex items-center">
                            <input
                                type="checkbox"
                                id="interest-it"
                                name="interest-it"
                                className="mr-2"
                                checked={form.interests.it}
                                onChange={handleChange}
                            />
                            <label htmlFor="interest-it" className="text-gray-600">IT 개발</label>
                        </div>
                        <div className="flex items-center">
                            <input
                                type="checkbox"
                                id="interest-marketing"
                                name="interest-marketing"
                                className="mr-2"
                                checked={form.interests.marketing}
                                onChange={handleChange}
                            />
                            <label htmlFor="interest-marketing" className="text-gray-600">마케팅</label>
                        </div>
                        <div className="flex items-center">
                            <input
                                type="checkbox"
                                id="interest-video"
                                name="interest-video"
                                className="mr-2"
                                checked={form.interests.video}
                                onChange={handleChange}
                            />
                            <label htmlFor="interest-video" className="text-gray-600">영상</label>
                        </div>
                    </div>
                </div>
                <div className="mb-6">
                    <div className="flex items-center">
                        <input
                            type="checkbox"
                            id="agree-terms"
                            name="agreeTerms"
                            className="mr-2"
                            checked={form.agreeTerms}
                            onChange={handleChange}
                        />
                        <label htmlFor="agree-terms" className="text-gray-600 text-sm">서비스 이용약관 및 개인정보 처리방침에 동의합니다.</label>
                    </div>
                </div>
                <button type="submit" className="main-color text-white py-3 px-4 rounded-md w-full font-semibold hover:bg-blue-700 transition duration-300">가입하기</button>
            </form>
        </div>
    );
}

export default SignupForm;