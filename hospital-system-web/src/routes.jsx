import { createBrowserRouter } from 'react-router';
import { AppLayout } from './layouts/AppLayout';
import Appointments from './pages/Appointments/Appointments.jsx';
import Doctors from './pages/Doctors/Doctors.jsx';
import Patients from './pages/Patients/Patients.jsx';
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
        element: <Doctors />,
      },
      {
        path: '/patients',
        element: <Patients />,
      },
    ],
  },
]);
