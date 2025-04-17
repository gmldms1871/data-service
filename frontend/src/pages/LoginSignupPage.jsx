import React from 'react';
import Navbar from '../components/Navbar';
import AuthContainer from '../components/AuthContainer';
import Footer from '../components/Footer';

function LoginSignupPage() {
    return (
        <div className="bg-gray-50 font-sans">
            <Navbar />
            <AuthContainer />
            <Footer />
        </div>
    );
}
//안녕하세용
export default LoginSignupPage;