import { ApiConfig } from '../config/api';

async function getErrorMessage(response, fallback) {
  let body;

  try {
    body = await response.json();
  } catch {
    return `${fallback} (${response.status})`;
  }

  console.error('Backend error:', {
    status: response.status,
    body,
  });

  return (
    body.message ||
    body.error ||
    body.detail ||
    `${fallback} (${response.status})`
  );
}

export async function getAllDoctors() {
  try {
    const response = await fetch(ApiConfig('doctors'));
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create doctor');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching doctor:', error);
    throw error;
  }
}

export async function getDoctorById(id) {
  try {
    const response = await fetch(ApiConfig(`doctors/${id}`));
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to fetch doctor');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching doctor:', error);
    throw error;
  }
}

export async function getDoctorByCRM(crm) {
  try {
    const response = await fetch(ApiConfig(`doctors/crm?crm=${crm}`));
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to fetch doctor');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching doctor:', error);
    throw error;
  }
}

export async function createDoctor(doctorData) {
  try {
    console.log('Creating doctor:', doctorData);

    const response = await fetch(ApiConfig('doctors'), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(doctorData),
    });

    if (!response.ok) {
      throw new Error(
        await getErrorMessage(response, 'Failed to create doctor'),
      );
    }

    return await response.json();
  } catch (error) {
    console.error('Error creating doctor:', error);
    throw error;
  }
}

export async function updateDoctor(id, doctorData) {
  try {
    const response = await fetch(ApiConfig(`doctors/${id}`), {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(doctorData),
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to update doctor');
    }
    return await response.json();
  } catch (error) {
    console.error('Error updating doctor:', error);
    throw error;
  }
}

export async function deleteDoctor(id) {
  try {
    const response = await fetch(ApiConfig(`doctors/${id}`), {
      method: 'DELETE',
    });

    if (!response.ok) {
      throw new Error(
        await getErrorMessage(response, 'Failed to delete doctor'),
      );
    }
  } catch (error) {
    console.error('Error deleting doctor:', error);
    throw error;
  }
}
