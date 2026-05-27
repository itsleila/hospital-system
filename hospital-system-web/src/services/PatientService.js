import { ApiConfig } from '../config/api';

export async function getAllPatients() {
  try {
    const response = await fetch(ApiConfig('patients'));
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create patient');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching patients:', error);
    throw error;
  }
}

export async function getPatientById(id) {
  try {
    const response = await fetch(ApiConfig(`patients/${id}`));
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create patient');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching patient:', error);
    throw error;
  }
}

export async function getPatientByCPF(cpf) {
  try {
    const response = await fetch(ApiConfig(`patients/cpf/${cpf}`));
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to fetch patient');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching patient:', error);
    throw error;
  }
}

export async function createPatient(patientData) {
  try {
    const response = await fetch(ApiConfig('patients'), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(patientData),
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create patient');
    }
    return await response.json();
  } catch (error) {
    console.error('Error creating patient:', error);
    throw error;
  }
}

export async function updatePatient(id, patientData) {
  try {
    const response = await fetch(ApiConfig(`patients/${id}`), {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(patientData),
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create patient');
    }
    return await response.json();
  } catch (error) {
    console.error('Error updating patient:', error);
    throw error;
  }
}

export async function deletePatient(id) {
  try {
    const response = await fetch(ApiConfig(`patients/${id}`), {
      method: 'DELETE',
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create patient');
    }
  } catch (error) {
    console.error('Error deleting patient:', error);
    throw error;
  }
}
