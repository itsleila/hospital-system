import { createBrowserRouter } from 'react-router';
import { AppLayout } from './layouts/AppLayout';
import Appointments from './pages/Appointments/Appointments.jsx';
import Doctors from './pages/Doctors/Doctors.jsx';
import Patients from './pages/Patients/Patients.jsx';
import PatientForm from './pages/Patients/PatientForm.jsx';
import DoctorForm from './pages/Doctors/DoctorForm.jsx';
import AppointmentForm from './pages/Appointments/AppointmentForm.jsx';
export const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      {
        index: true,
        element: <Appointments />,
      },
      {
        path: '/doctors',
        children: [
          {
            index: true,
            element: <Doctors />,
          },
          { path: 'doctor-form', element: <DoctorForm /> },
        ],
      },
      {
        path: '/appointments',
        children: [
          {
            index: true,
            element: <Appointments />,
          },
          { path: 'appointment-form', element: <AppointmentForm /> },
        ],
      },
      {
        path: '/patients',
        children: [
          {
            index: true,
            element: <Patients />,
          },
          { path: 'patient-form', element: <PatientForm /> },
        ],
      },
    ],
  },
]);
