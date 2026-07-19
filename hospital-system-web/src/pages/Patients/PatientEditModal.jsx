import { useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Modal } from '../../components';
import { updatePatient } from '../../services/PatientService';

const PatientEditModal = ({ isOpen, patient, onClose, onSuccess }) => {
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
    if (patient) {
      reset({
        name: patient.name || '',
        surname: patient.surname || '',
        cpf: patient.cpf || '',
        birthdate: patient.birthdate || '',
        gender: patient.gender || '',
        phonenumber: patient.phonenumber || '',
      });
    }
  }, [patient, reset]);

  const onSubmit = async (data) => {
    try {
      setIsSubmitting(true);
      await updatePatient(patient.id, data);
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
    <Modal isOpen={isOpen} onClose={onClose} title="Edit Patient">
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
          <label>Birthdate</label>

          <input
            type="date"
            {...register('birthdate', {
              required: 'Birthdate is required',
            })}
          />

          {errors.birthdate && (
            <p className="text-error">{errors.birthdate.message}</p>
          )}
        </div>
        <div className="form-group">
          <label>Gender</label>

          <Controller
            name="gender"
            control={control}
            rules={{
              required: 'Gender is required',
            }}
            render={({ field }) => (
              <select {...field}>
                <option value="">Select gender</option>

                <option value="MALE">Male</option>

                <option value="FEMALE">Female</option>

                <option value="OTHER">Other</option>
              </select>
            )}
          />

          {errors.gender && (
            <p className="text-error">{errors.gender.message}</p>
          )}
        </div>
        <div className="form-group">
          <label>Phone Number</label>

          <input
            type="tel"
            {...register('phonenumber', {
              required: 'Phone number is required',
            })}
          />

          {errors.phonenumber && (
            <p className="text-error">{errors.phonenumber.message}</p>
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

export default PatientEditModal;
