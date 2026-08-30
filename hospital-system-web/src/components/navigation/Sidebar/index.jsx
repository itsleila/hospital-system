import { NavLink } from 'react-router-dom';
import Logo from '../../../assets/logo.svg';
import {
  FaUsers,
  FaStethoscope,
  FaRegCalendarCheck,
  FaRegBell,
} from 'react-icons/fa';

import './index.css';
const Sidebar = () => {
  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <img src={Logo} alt="Union Hospital Logo" />
        <span>Union Hospital</span>
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
        <NavLink to="/notifications" className="nav-links">
          <FaRegBell />
          <span>Notifications</span>
        </NavLink>
      </nav>
      <div className="sidebar-footer">
        <p>&copy; 2026 Union Hospital. All rights reserved.</p>
      </div>
    </aside>
  );
};

export default Sidebar;
