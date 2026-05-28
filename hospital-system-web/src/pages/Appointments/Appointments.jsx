import { useNavigate } from 'react-router-dom';
import { Modal, SearchArea, Table } from '../../components';
import '../pages.css';
import {
  deleteAppointment,
  getAllAppointments,
} from '../../services/AppointmentService';
import { useEffect, useState } from 'react';
import AppointmentEditModal from './AppointmentEditModal';
import { filterByQuery } from '../../utils/search';

const Appointments = () => {
  const [appointments, setAppointments] = useState([]);
  const navigate = useNavigate();

  const [openModalConfirmation, setOpenModalConfirmation] = useState(false);

  const [selectedAppointment, setSelectedAppointment] = useState(null);

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  const [filteredAppointments, setFilteredAppointments] = useState([]);

  async function fetchAppointments() {
    try {
      const response = await getAllAppointments();
      setAppointments(response);
    } catch (error) {
      console.error('Error fetching appointments:', error);
    } finally {
      console.log('Fetch appointments completed');
    }
  }

  useEffect(() => {
    async function fetchAppointments() {
      const response = await getAllAppointments();
      setAppointments(response);
    }
    fetchAppointments();
  }, []);

  const columns = [
    {
      name: 'Date',
      selector: (row) => row.dateTime,
    },
    {
      name: 'Status',
      selector: (row) => row.status,
    },
    {
      name: 'Doctor',
      selector: (row) => row.doctorName,
    },
    {
      name: 'Patient',
      selector: (row) => row.patientName,
    },
  ];

  function handleEdit(appointment) {
    setSelectedAppointment(appointment);
    setIsEditModalOpen(true);
  }

  function handleConfirmDelete(appointment) {
    setSelectedAppointment(appointment);
    setOpenModalConfirmation(true);
  }

  const handleDeleteAppointment = async () => {
    try {
      await deleteAppointment(selectedAppointment.id);

      setAppointments(
        appointments.filter((a) => a.id !== selectedAppointment.id),
      );

      setOpenModalConfirmation(false);
    } catch (error) {
      console.error('Error deleting appointment:', error);
    }
  };

  const handleSearch = (query) => {
    const filtered = filterByQuery(
      appointments,
      ['doctorName', 'patientName', 'status'],
      query,
    );

    setFilteredAppointments(filtered);
  };

  return (
    <>
      <div>
        <h1>Appointments</h1>

        <div className="header-actions">
          <SearchArea
            placeholder="Search appointments..."
            onChange={handleSearch}
          />

          <button
            onClick={() => navigate('/appointments/appointment-form')}
            className="btn-primary"
          >
            Add Appointment
          </button>
        </div>

        <Table
          columns={columns}
          data={
            filteredAppointments.length === 0
              ? appointments
              : filteredAppointments
          }
          onEdit={handleEdit}
          onDelete={handleConfirmDelete}
          noDataComponent={
            <div className="empty-state">No appointments found</div>
          }
        />
      </div>

      <Modal
        isOpen={openModalConfirmation}
        onClose={() => setOpenModalConfirmation(false)}
        title="Confirm Delete"
      >
        <p>Are you sure you want to delete this appointment?</p>

        <button
          onClick={() => {
            handleDeleteAppointment();
          }}
          className="btn-danger"
        >
          Delete
        </button>
      </Modal>

      <AppointmentEditModal
        isOpen={isEditModalOpen}
        appointment={selectedAppointment}
        onClose={() => setIsEditModalOpen(false)}
        onSuccess={() => {
          setIsEditModalOpen(false);
          fetchAppointments();
        }}
      />
    </>
  );
};

export default Appointments;
