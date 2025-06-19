import React from "react";
import { Link } from "react-router-dom";

function Footer() {
  return (
    <footer className="bg-gray-800 text-white py-8">
      <div className="container mx-auto px-4">
        <div className="flex flex-col md:flex-row justify-between items-center">
          <p className="text-gray-400 mb-4 md:mb-0">
            © 2025 FreeTalent. All rights reserved.
          </p>
          <div className="flex space-x-4">
            <Link
              to="#"
              className="text-gray-400 hover:text-white transition duration-300"
            >
              이용약관
            </Link>
            <Link
              to="#"
              className="text-gray-400 hover:text-white transition duration-300"
            >
              개인정보처리방침
            </Link>
            <Link
              to="#"
              className="text-gray-400 hover:text-white transition duration-300"
            >
              고객센터
            </Link>
          </div>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
