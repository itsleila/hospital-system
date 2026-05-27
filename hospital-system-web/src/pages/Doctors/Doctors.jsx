import { useNavigate } from 'react-router-dom';
import { Modal, SearchArea, Table } from '../../components';
import '../pages.css';
import { deleteDoctor, getAllDoctors } from '../../services/DoctorService';
import { useEffect, useState } from 'react';
import DoctorEditModal from './DoctorEditModal';
import { filterByQuery } from '../../utils/search';
const Doctors = () => {
  const [doctors, setDoctors] = useState([]);
  const navigate = useNavigate();
  const [openModalConfirmation, setOpenModalConfirmation] = useState(false);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [filteredDoctors, setFilteredDoctors] = useState([]);

  async function fetchDoctors() {
    try {
      const response = await getAllDoctors();
      setDoctors(response);
    } catch (error) {
      console.error('Error fetching doctors:', error);
    } finally {
      console.log('Fetch doctors completed');
    }
  }

  useEffect(() => {
    async function fetchDoctors() {
      const response = await getAllDoctors();
      setDoctors(response);
    }
    fetchDoctors();
  }, []);

  const columns = [
    {
      name: 'Name',
      selector: (row) => row.name,
    },
    {
      name: 'Surname',
      selector: (row) => row.surname,
    },
    {
      name: 'Email',
      selector: (row) => row.email,
    },
    {
      name: 'CRM',
      selector: (row) => row.crm,
    },
    {
      name: 'Specialty',
      selector: (row) => row.specialty,
    },
  ];

  function handleEdit(doctor) {
    setSelectedDoctor(doctor);
    setIsEditModalOpen(true);
  }

  function handleConfirmDelete(doctor) {
    setSelectedDoctor(doctor);
    setOpenModalConfirmation(true);
  }

  const handleDeleteDoctor = async () => {
    try {
      await deleteDoctor(selectedDoctor.id);
      setDoctors(doctors.filter((d) => d.id !== selectedDoctor.id));
      setOpenModalConfirmation(false);
    } catch (error) {
      console.error('Error deleting doctor:', error);
    }
  };

  const handleSearch = (query) => {
    const filtered = filterByQuery(
      doctors,
      ['name', 'surname', 'email', 'crm', 'specialty'],
      query,
    );
    setFilteredDoctors(filtered);
  };

  return (
    <>
      <div>
        <h1>Doctors</h1>
        <div className="header-actions">
          <SearchArea placeholder="Search doctors..." onChange={handleSearch} />
          <button
            onClick={() => navigate('/doctors/doctor-form')}
            className="btn-primary"
          >
            Add Doctor
          </button>
        </div>
        <Table
          columns={columns}
          data={filteredDoctors.length === 0 ? doctors : filteredDoctors}
          onEdit={handleEdit}
          onDelete={handleConfirmDelete}
          noDataComponent={<div className="empty-state">No doctors found</div>}
        />
      </div>
      <Modal
        isOpen={openModalConfirmation}
        onClose={() => setOpenModalConfirmation(false)}
        title="Confirm Delete"
      >
        <p>Are you sure you want to delete this doctor?</p>
        <button
          onClick={() => {
            handleDeleteDoctor();
          }}
          className="btn-danger"
        >
          Delete
        </button>
      </Modal>
      <DoctorEditModal
        isOpen={isEditModalOpen}
        doctor={selectedDoctor}
        onClose={() => setIsEditModalOpen(false)}
        onSuccess={() => {
          setIsEditModalOpen(false);
          fetchDoctors();
        }}
      />
    </>
  );
};
export default Doctors;
