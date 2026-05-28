import { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import '../pages.css';
import { createAppointment } from '../../services/AppointmentService';
import { getAllDoctors } from '../../services/DoctorService';
import { getPatientByCPF } from '../../services/PatientService';
import { useNavigate } from 'react-router-dom';

const AppointmentForm = () => {
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [doctors, setDoctors] = useState([]);
  const [cpf, setCpf] = useState('');
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [patientNotFound, setPatientNotFound] = useState(false);
  const navigate = useNavigate();

  const {
    handleSubmit,
    control,
    register,
    reset,
    setError,
    formState: { errors },
  } = useForm();

  useEffect(() => {
    async function fetchData() {
      try {
        const doctorsResponse = await getAllDoctors();
        setDoctors(doctorsResponse);
      } catch (error) {
        console.error(error);
      }
    }

    fetchData();
  }, []);

  const onSubmit = async (data) => {
    try {
      if (!selectedPatient) {
        throw new Error('Select a patient');
      }
      setIsSubmitting(true);
      const appointmentData = {
        ...data,
        patientId: selectedPatient.id,
      };

      const response = await createAppointment(appointmentData);

      if (response.errors) {
        throw new Error(response.errors[0].message);
      }
      setShowConfirmation(true);
      reset();
      setSelectedPatient(null);
      setCpf('');
    } catch (error) {
      setError('general', {
        type: 'manual',
        message: error.message,
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  useEffect(() => {
    async function searchPatient() {
      if (cpf.length < 14) {
        return;
      }

      try {
        setPatientNotFound(false);

        const patient = await getPatientByCPF(cpf);

        setSelectedPatient(patient);
      } catch (error) {
        setSelectedPatient(null);

        setPatientNotFound(true);
        console.error(error);
      }
    }

    searchPatient();
  }, [cpf]);

  return (
    <>
      <h1>Register New Appointment</h1>

      {errors.general && (
        <div className="text-error">{errors.general.message}</div>
      )}

      {showConfirmation && (
        <div className="text-success">Appointment created successfully!</div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="register-form">
        <div className="form-group">
          <label>Search Patient by CPF</label>

          <div className="search-inline">
            <input
              type="text"
              value={cpf}
              onChange={(e) => setCpf(e.target.value)}
              placeholder="000.000.000-00"
            />
          </div>
        </div>
        {selectedPatient && (
          <div className="selected-card">
            <h3>Patient Selected</h3>

            <p>
              {selectedPatient.name} {selectedPatient.surname}
            </p>

            <p>CPF: {selectedPatient.cpf}</p>
          </div>
        )}
        {patientNotFound && (
          <div className="text-error">
            Patient not found.
            <button
              type="button"
              onClick={() => navigate('/patients/patient-form')}
              className="btn-link"
            >
              Create patient
            </button>
          </div>
        )}
        <div className="double-column">
          <div className="form-group">
            <label>Doctor</label>

            <Controller
              name="doctorId"
              control={control}
              rules={{ required: 'Doctor is required' }}
              render={({ field }) => (
                <select {...field} className="select-input">
                  <option value="">Select doctor</option>

                  {doctors.map((doctor) => (
                    <option key={doctor.id} value={doctor.id}>
                      Dr. {doctor.name} - {doctor.specialty}
                    </option>
                  ))}
                </select>
              )}
            />

            {errors.doctorId && (
              <p className="text-error">{errors.doctorId.message}</p>
            )}
          </div>

          <div className="form-group">
            <label>Date and Time</label>

            <input
              type="datetime-local"
              {...register('dateTime', {
                required: 'Date and time is required',
              })}
            />

            {errors.dateTime && (
              <p className="text-error">{errors.dateTime.message}</p>
            )}
          </div>
        </div>

        <button
          type="submit"
          disabled={isSubmitting || !selectedPatient}
          className="btn-primary"
        >
          {isSubmitting ? 'Saving...' : 'Create Appointment'}
        </button>
      </form>
    </>
  );
};

export default AppointmentForm;
