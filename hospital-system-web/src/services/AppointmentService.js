import { ApiConfig } from '../config/api';

export async function getAllAppointments() {
  try {
    const response = await fetch(ApiConfig('appointments'));

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to fetch appointments');
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching appointments:', error);
    throw error;
  }
}

export async function createAppointment(appointmentData) {
  try {
    const response = await fetch(ApiConfig('appointments'), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(appointmentData),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create appointment');
    }

    return await response.json();
  } catch (error) {
    console.error('Error creating appointment:', error);
    throw error;
  }
}

export async function updateAppointment(id, appointmentData) {
  try {
    const response = await fetch(ApiConfig(`appointments/${id}`), {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(appointmentData),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to update appointment');
    }

    return await response.json();
  } catch (error) {
    console.error('Error updating appointment:', error);
    throw error;
  }
}

export async function cancelAppointment(id) {
  try {
    const response = await fetch(ApiConfig(`appointments/${id}/cancel`), {
      method: 'PATCH',
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to cancel appointment');
    }

    return await response.json();
  } catch (error) {
    console.error('Error cancelling appointment:', error);
    throw error;
  }
}

export async function getAppointmentById(id) {
  try {
    const response = await fetch(ApiConfig(`appointments/${id}`));

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to fetch appointment');
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching appointment:', error);
    throw error;
  }
}

export async function getAppointmentByDoctorId(id) {
  try {
    const response = await fetch(ApiConfig(`appointments/doctor/${id}`));

    if (!response.ok) {
      const errorData = await response.json();
      console.error('Error fetching appointments for doctor:', errorData);
      throw new Error(
        errorData.message || 'Failed to fetch appointments for doctor',
      );
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching appointments for doctor:', error);

    throw error;
  }
}

export async function getAppointmentByPatientId(id) {
  try {
    const response = await fetch(ApiConfig(`appointments/patient/${id}`));

    if (!response.ok) {
      const errorData = await response.json();

      throw new Error(
        errorData.message || 'Failed to fetch appointments for patient',
      );
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching appointments for patient:', error);

    throw error;
  }
}

export async function getAppointmentByDate(date) {
  try {
    const response = await fetch(ApiConfig(`appointments/date/${date}`));

    if (!response.ok) {
      const errorData = await response.json();

      throw new Error(
        errorData.message || 'Failed to fetch appointments for date',
      );
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching appointments for date:', error);

    throw error;
  }
}
