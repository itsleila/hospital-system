import { useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Modal } from '../../components';
import { updateDoctor } from '../../services/DoctorService';

const DoctorEditModal = ({ isOpen, doctor, onClose, onSuccess }) => {
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    control,
    reset,
    setError,
    formState: { errors },
  } = useForm();

  useEffect(() => {
    if (doctor) {
      reset({
        name: doctor.name || '',
        surname: doctor.surname || '',
        email: doctor.email || '',
        specialty: doctor.specialty || '',
      });
    }
  }, [doctor, reset]);
  const onSubmit = async (data) => {
    try {
      setIsSubmitting(true);
      await updateDoctor(doctor.id, data);
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

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Edit Doctor">
      {errors.general && (
        <div className="text-error">{errors.general.message}</div>
      )}
      <form onSubmit={handleSubmit(onSubmit)} className="edit-form">
        <div className="form-group">
          <label>Name</label>

          <input
            type="text"
            {...register('name', {
              required: 'Name is required',
            })}
          />

          {errors.name && <p className="text-error">{errors.name.message}</p>}
        </div>

        <div className="form-group">
          <label>Surname</label>

          <input
            type="text"
            {...register('surname', {
              required: 'Surname is required',
            })}
          />

          {errors.surname && (
            <p className="text-error">{errors.surname.message}</p>
          )}
        </div>
        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            placeholder="example@example.com"
            {...register('email', {
              required: 'Email is required',
              pattern: {
                value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
                message: 'Invalid email address',
              },
            })}
          />
          {errors.email && <p className="text-error">{errors.email.message}</p>}
        </div>
        <div className="form-group">
          <label>Specialty</label>
          <Controller
            name="specialty"
            control={control}
            rules={{ required: 'Specialty is required' }}
            render={({ field }) => (
              <select {...field} className="select-input">
                <option value="">Select specialty</option>
                <option value="cardiology">Cardiology</option>
                <option value="neurology">Neurology</option>
                <option value="pediatrics">Pediatrics</option>
              </select>
            )}
          />

          {errors.specialty && (
            <p className="text-error">{errors.specialty.message}</p>
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
export default DoctorEditModal;
