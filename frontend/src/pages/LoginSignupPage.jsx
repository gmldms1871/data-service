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

export default LoginSignupPage;