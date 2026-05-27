import { useNavigate } from 'react-router-dom';
import { Modal, SearchArea, Table } from '../../components';
import '../pages.css';
import { deletePatient, getAllPatients } from '../../services/PatientService';
import { useEffect, useState } from 'react';
import PatientEditModal from './PatientEditModal';
import { filterByQuery } from '../../utils/search';

const Patients = () => {
  const [patients, setPatients] = useState([]);
  const navigate = useNavigate();
  const [openModalConfirmation, setOpenModalConfirmation] = useState(false);
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [filteredPatients, setFilteredPatients] = useState([]);

  async function fetchPatients() {
    try {
      const response = await getAllPatients();
      setPatients(response);
    } catch (error) {
      console.error('Error fetching patients:', error);
    } finally {
      console.log('Fetch patients completed');
    }
  }

  useEffect(() => {
    async function fetchPatients() {
      const response = await getAllPatients();
      setPatients(response);
    }
    fetchPatients();
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
      name: 'CPF',
      selector: (row) => row.cpf,
    },
    {
      name: 'Birth Date',
      selector: (row) => row.birthdate,
    },
    {
      name: 'Gender',
      selector: (row) => row.gender,
    },
    {
      name: 'Phone Number',
      selector: (row) => row.phonenumber,
    },
  ];

  function handleEdit(patient) {
    setSelectedPatient(patient);
    setIsEditModalOpen(true);
  }

  function handleConfirmDelete(patient) {
    setSelectedPatient(patient);
    setOpenModalConfirmation(true);
  }

  const handleDeletePatient = async () => {
    try {
      await deletePatient(selectedPatient.id);
      setPatients(patients.filter((p) => p.id !== selectedPatient.id));
      setOpenModalConfirmation(false);
    } catch (error) {
      console.error('Error deleting patient:', error);
    }
  };

  const handleSearch = (query) => {
    const filtered = filterByQuery(patients, ['name', 'surname', 'cpf'], query);
    setFilteredPatients(filtered);
  };

  return (
    <>
      <div>
        <h1>Patients</h1>
        <div className="header-actions">
          <SearchArea
            placeholder="Search patients..."
            onChange={handleSearch}
          />
          <button
            onClick={() => navigate('/patients/patient-form')}
            className="btn-primary"
          >
            Add Patient
          </button>
        </div>

        <Table
          columns={columns}
          data={filteredPatients.length === 0 ? patients : filteredPatients}
          onEdit={handleEdit}
          onDelete={handleConfirmDelete}
          noDataComponent={<div className="empty-state">No patients found</div>}
        />
      </div>
      <Modal
        isOpen={openModalConfirmation}
        onClose={() => setOpenModalConfirmation(false)}
        title="Confirm Delete"
      >
        <p>Are you sure you want to delete this patient?</p>
        <button
          onClick={() => {
            handleDeletePatient();
          }}
          className="btn-danger"
        >
          Delete
        </button>
      </Modal>
      <PatientEditModal
        isOpen={isEditModalOpen}
        patient={selectedPatient}
        onClose={() => setIsEditModalOpen(false)}
        onSuccess={() => {
          setIsEditModalOpen(false);
          fetchPatients();
        }}
      />
    </>
  );
};
export default Patients;
