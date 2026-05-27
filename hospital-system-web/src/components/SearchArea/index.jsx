import { useState } from 'react';
import './index.css';
import { FaSearch } from 'react-icons/fa';
const SearchArea = ({ placeholder, onChange }) => {
  const [query, setQuery] = useState('');

  const handleSearchChange = (e) => {
    const newQuery = e.target.value;
    setQuery(newQuery);
    onChange(newQuery) && onChange(newQuery);
  };

  return (
    <div className="search-area">
      <input
        type="text"
        placeholder={placeholder}
        className="search-input"
        onChange={handleSearchChange}
        value={query}
      />
      <button className="search-button">
        <FaSearch />
      </button>
    </div>
  );
};

export default SearchArea;
