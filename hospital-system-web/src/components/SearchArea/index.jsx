import './index.css';
import { FaSearch } from 'react-icons/fa';
const SearchArea = ({ placeholder }) => {
  return (
    <div className="search-area">
      <input type="text" placeholder={placeholder} className="search-input" />
      <button className="search-button">
        <FaSearch />
      </button>
    </div>
  );
};

export default SearchArea;
