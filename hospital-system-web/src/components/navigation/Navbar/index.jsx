import { Link } from 'react-router-dom';

const Navbar = () => {
  return (
    <nav>
      <Link to="/">Appointments</Link>
      <Link to="/doctors">Doctors</Link>
      <Link to="/patients">Patients</Link>
    </nav>
  );
};

export default Navbar;
