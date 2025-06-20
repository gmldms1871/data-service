"use client";

import { useState, useEffect, useCallback } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import ReactPaginate from "react-paginate";
import "./CompanyListPage.css";

const CompanyListPage = () => {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pageNumber, setPageNumber] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  const companiesPerPage = 10;

  const fetchCategories = useCallback(async () => {
    try {
      const response = await axios.get("/api/categories");
      setCategories(response.data);
    } catch (error) {
      console.error("카테고리 로딩 중 오류:", error);
      setCategories([]); // 에러 시 빈 배열로 설정
    }
  }, []);

  const fetchCompanies = useCallback(async () => {
    setLoading(true);
    try {
      let url = `/api/companies?page=${pageNumber}&size=${companiesPerPage}`;
      if (selectedCategory) {
        url += `&category=${selectedCategory}`;
      }
      if (searchTerm) {
        url += `&search=${searchTerm}`;
      }

      const response = await axios.get(url);
      setCompanies(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error("회사 로딩 중 오류:", error);
      setLoading(false);
      setCompanies([]); // 에러 시 빈 배열로 설정
      setTotalPages(0); // 에러 시 페이지 수 0으로 설정
    } finally {
      setLoading(false);
    }
  }, [pageNumber, selectedCategory, searchTerm, companiesPerPage]);

  useEffect(() => {
    fetchCompanies();
    fetchCategories();
  }, [fetchCompanies, fetchCategories]);

  const handlePageClick = ({ selected }) => {
    setPageNumber(selected);
  };

  const handleCategoryChange = (e) => {
    setSelectedCategory(e.target.value);
    setPageNumber(0); // Reset to the first page when category changes
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPageNumber(0); // Reset to the first page when search is submitted
    fetchCompanies(); // Trigger fetchCompanies to apply search immediately
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="company-list-page">
      <h1>회사 목록</h1>

      <div className="filters">
        <select value={selectedCategory} onChange={handleCategoryChange}>
          <option value="">전체 카테고리</option>
          {categories.map((category) => (
            <option key={category.id} value={category.name}>
              {category.name}
            </option>
          ))}
        </select>

        <form onSubmit={handleSearchSubmit}>
          <input
            type="text"
            placeholder="회사 이름 검색..."
            value={searchTerm}
            onChange={handleSearchChange}
          />
          <button type="submit">검색</button>
        </form>
      </div>

      <ul className="company-list">
        {companies.map((company) => (
          <li key={company.id}>
            <Link to={`/companies/${company.id}`}>{company.name}</Link>
          </li>
        ))}
      </ul>

      <ReactPaginate
        previousLabel={"이전"}
        nextLabel={"다음"}
        pageCount={totalPages}
        onPageChange={handlePageClick}
        containerClassName={"pagination"}
        previousLinkClassName={"pagination__link"}
        nextLinkClassName={"pagination__link"}
        disabledClassName={"pagination__link--disabled"}
        activeClassName={"pagination__link--active"}
      />
    </div>
  );
};

export default CompanyListPage;
