import { NavLink } from 'react-router-dom';
import Logo from '../../../assets/logo.svg';
import { FaUsers, FaStethoscope, FaRegCalendarCheck } from 'react-icons/fa';

import './index.css';
const Sidebar = () => {
  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <img src={Logo} alt="MediCore Logo" />
        <span>MediCore</span>
      </div>
      <nav className="sidebar-nav">
        <NavLink to="/" className="nav-links">
          <FaRegCalendarCheck />
          <span>Appointments</span>
        </NavLink>

        <NavLink to="/doctors" className="nav-links">
          <FaStethoscope />
          <span>Doctors</span>
        </NavLink>

        <NavLink to="/patients" className="nav-links">
          <FaUsers />
          <span>Patients</span>
        </NavLink>
      </nav>
    </aside>
  );
};

export default Sidebar;
