import { ApiConfig } from '../config/api';

export async function getAllNotifications() {
  try {
    const response = await fetch(ApiConfig('notifications'));

    if (!response.ok) {
      const errorData = await response.json();

      throw new Error(errorData.message || 'Failed to fetch notifications');
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching notifications:', error);

    throw error;
  }
}

export async function getNotificationById(id) {
  try {
    const response = await fetch(ApiConfig(`notifications/${id}`));

    if (!response.ok) {
      const errorData = await response.json();

      throw new Error(errorData.message || 'Failed to fetch notification');
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching notification:', error);

    throw error;
  }
}

export async function getNotificationsByStatus(status) {
  try {
    const response = await fetch(ApiConfig(`notifications/status/${status}`));

    if (!response.ok) {
      const errorData = await response.json();

      throw new Error(
        errorData.message || 'Failed to fetch notifications by status',
      );
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching notifications by status:', error);

    throw error;
  }
}

export async function retryNotification(id) {
  try {
    const response = await fetch(ApiConfig(`notifications/${id}/retry`), {
      method: 'POST',
    });

    if (!response.ok) {
      const errorData = await response.json();

      throw new Error(errorData.message || 'Failed to retry notification');
    }

    return await response.json();
  } catch (error) {
    console.error('Error retrying notification:', error);

    throw error;
  }
}

export async function cancelNotification(id) {
  try {
    const response = await fetch(ApiConfig(`notifications/${id}/cancel`), {
      method: 'PATCH',
    });

    if (!response.ok) {
      const errorData = await response.json();

      throw new Error(errorData.message || 'Failed to cancel notification');
    }

    return await response.json();
  } catch (error) {
    console.error('Error cancelling notification:', error);

    throw error;
  }
}
