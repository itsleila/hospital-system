import { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { createDoctor } from '../../services/DoctorService';
import '../pages.css';

const DoctorForm = () => {
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

      const response = await createDoctor(data);

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
      <h1>Register New Doctor</h1>

      {errors.general && (
        <div className="text-error">{errors.general.message}</div>
      )}

      {showConfirmation && (
        <div className="text-success">Doctor created successfully!</div>
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
            {errors.email && (
              <p className="text-error">{errors.email.message}</p>
            )}
          </div>
          <div className="form-group">
            <label>CRM</label>
            <input
              type="text"
              placeholder="XXXXXX/UF"
              {...register('CRM', {
                required: 'CRM is required',
              })}
            />
            {errors.CRM && <p className="text-error">{errors.CRM.message}</p>}
          </div>
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

        <button type="submit" disabled={isSubmitting} className="btn-primary">
          {isSubmitting ? 'Saving...' : 'Create Doctor'}
        </button>
      </form>
    </>
  );
};
export default DoctorForm;
