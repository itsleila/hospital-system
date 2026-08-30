import { useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Modal } from '../../components';
import { updateAppointment } from '../../services/AppointmentService';
import { getAllDoctors } from '../../services/DoctorService';

const AppointmentsEditModal = ({ isOpen, appointment, onClose, onSuccess }) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [doctors, setDoctors] = useState([]);

  const {
    register,
    handleSubmit,
    control,
    reset,
    setError,
    formState: { errors },
  } = useForm({
    defaultValues: {
      doctorId: '',
      dateTime: '',
      status: '',
      patientName: '',
    },
  });

  useEffect(() => {
    if (appointment) {
      reset({
        doctorId: appointment.doctorId ? String(appointment.doctorId) : '',
        dateTime: appointment.dateTime ? appointment.dateTime.slice(0, 16) : '',
        status: appointment.status || '',
        patientName: appointment.patientName || '',
      });
    }
  }, [appointment, reset]);

  useEffect(() => {
    async function fetchDoctors() {
      try {
        const doctorsResponse = await getAllDoctors();
        setDoctors(doctorsResponse);
      } catch (error) {
        console.error('Error fetching doctors:', error);
      }
    }
    fetchDoctors();
  }, []);

  const onSubmit = async (data) => {
    try {
      setIsSubmitting(true);
      await updateAppointment(appointment.id, data);
      onSuccess();
      onClose();
    } catch (error) {
      setError('general', {
        type: 'manual',
        message: error.message,
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!appointment) return null;
  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Edit Appointment">
      {errors.general && (
        <div className="text-error">{errors.general.message}</div>
      )}
      <form onSubmit={handleSubmit(onSubmit)} className="edit-form">
        {appointment.patientName && (
          <div className="selected-card">
            <p> Patient: {appointment.patientName}</p>
          </div>
        )}
        <div className="form-group">
          <label>Doctor</label>

          <Controller
            name="doctorId"
            control={control}
            rules={{ required: 'Doctor is required' }}
            render={({ field }) => (
              <select {...field} className="select-input">
                <option value="" disabled>
                  Select doctor
                </option>

                {doctors.map((doctor) => (
                  <option key={doctor.id} value={String(doctor.id)}>
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
        <div className="form-group">
          <label>Status</label>
          <Controller
            name="status"
            control={control}
            rules={{ required: 'Status is required' }}
            render={({ field }) => (
              <select {...field} className="select-input">
                <option value="">Select status</option>
                <option value="SCHEDULED">Scheduled</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            )}
          />

          {errors.status && (
            <p className="text-error">{errors.status.message}</p>
          )}
        </div>

        <div className="modal-actions">
          <button type="button" onClick={onClose} className="btn-secondary">
            Cancel
          </button>

          <button type="submit" className="btn-primary" disabled={isSubmitting}>
            {isSubmitting ? 'Saving...' : 'Save Changes'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
export default AppointmentsEditModal;
