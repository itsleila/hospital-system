import { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { createPatient } from '../../services/PatientService';
import '../pages.css';

const PatientForm = () => {
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    handleSubmit,
    control,
    register,
    reset,
    setError,
    formState: { errors },
  } = useForm();

  const onSubmit = async (data) => {
    try {
      setIsSubmitting(true);

      const response = await createPatient(data);

      if (response.errors) {
        throw new Error(response.errors[0].message);
      }

      setShowConfirmation(true);
      reset();
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
    <>
      <h1>Register New Patient</h1>

      {errors.general && (
        <div className="text-error">{errors.general.message}</div>
      )}

      {showConfirmation && (
        <div className="text-success">Patient created successfully!</div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="register-form">
        <div className="double-column">
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
        </div>
        <div className="double-column">
          <div className="form-group">
            <label>CPF</label>
            <input
              type="text"
              placeholder="000.000.000-00"
              {...register('cpf', {
                required: 'CPF is required',
              })}
            />
            {errors.cpf && <p className="text-error">{errors.cpf.message}</p>}
          </div>
          <div className="form-group">
            <label>Phone Number</label>
            <input
              type="tel"
              placeholder="(00) 00000-0000"
              {...register('phonenumber', {
                required: 'Phone number is required',
              })}
            />
            {errors.phonenumber && (
              <p className="text-error">{errors.phonenumber.message}</p>
            )}
          </div>
        </div>
        <div className="double-column">
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
              rules={{ required: 'Gender is required' }}
              render={({ field }) => (
                <select {...field} className="select-input">
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
        </div>

        <button type="submit" disabled={isSubmitting} className="btn-primary">
          {isSubmitting ? 'Saving...' : 'Create Patient'}
        </button>
      </form>
    </>
  );
};

export default PatientForm;
