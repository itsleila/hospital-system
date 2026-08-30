import { useEffect, useState } from 'react';
import { Modal, SearchArea, Table } from '../../components';
import '../pages.css';

import {
  cancelNotification,
  getAllNotifications,
  retryNotification,
} from '../../services/NotificationService';

import { filterByQuery } from '../../utils/search';
import NotificationDetailsModal from './NotificationDetailsModal';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [filteredNotifications, setFilteredNotifications] = useState([]);

  const [selectedNotification, setSelectedNotification] = useState(null);

  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState(false);

  const [openCancelConfirmation, setOpenCancelConfirmation] = useState(false);

  const [openRetryConfirmation, setOpenRetryConfirmation] = useState(false);

  const [statusFilter, setStatusFilter] = useState('ALL');

  async function fetchNotifications() {
    try {
      const response = await getAllNotifications();
      setNotifications(response);
    } catch (error) {
      console.error('Error fetching notifications:', error);
    }
  }

  useEffect(() => {
    async function fetchData() {
      await fetchNotifications();
    }
    fetchData();
  }, []);

  const columns = [
    {
      name: 'Appointment',
      selector: (row) => row.appointmentId,
    },
    {
      name: 'Patient',
      selector: (row) => row.patientName,
    },
    {
      name: 'Doctor',
      selector: (row) => row.doctorName,
    },
    {
      name: 'Type',
      selector: (row) => formatType(row.type),
    },
    {
      name: 'Status',
      selector: (row) => row.status,
    },
    {
      name: 'Appointment Date',
      selector: (row) => formatDate(row.appointmentDateTime),
    },
  ];

  function formatType(type) {
    switch (type) {
      case 'APPOINTMENT_CREATED':
        return 'Appointment Created';

      case 'APPOINTMENT_UPDATED':
        return 'Appointment Updated';

      case 'APPOINTMENT_CANCELLED':
        return 'Appointment Cancelled';

      case 'APPOINTMENT_REMINDER':
        return 'Appointment Reminder';

      default:
        return type;
    }
  }

  function formatDate(date) {
    if (!date) return '-';

    return new Date(date).toLocaleString();
  }

  function handleDetails(notification) {
    setSelectedNotification(notification);
    setIsDetailsModalOpen(true);
  }

  function handleConfirmCancel(notification) {
    setSelectedNotification(notification);
    setOpenCancelConfirmation(true);
  }

  function handleConfirmRetry(notification) {
    setSelectedNotification(notification);
    setOpenRetryConfirmation(true);
  }

  async function handleCancelNotification() {
    try {
      await cancelNotification(selectedNotification.id);

      await fetchNotifications();

      setOpenCancelConfirmation(false);
      setSelectedNotification(null);
    } catch (error) {
      console.error('Error cancelling notification:', error);
    }
  }

  async function handleRetryNotification() {
    try {
      await retryNotification(selectedNotification.id);

      await fetchNotifications();

      setOpenRetryConfirmation(false);
      setSelectedNotification(null);
    } catch (error) {
      console.error('Error retrying notification:', error);
    }
  }

  const handleSearch = (query) => {
    const filtered = filterByQuery(
      notifications,
      ['patientName', 'doctorName', 'type', 'status', 'patientPhone'],
      query,
    );

    setFilteredNotifications(filtered);
  };

  const notificationsToDisplay =
    filteredNotifications.length > 0 ? filteredNotifications : notifications;

  const statusFilteredNotifications =
    statusFilter === 'ALL'
      ? notificationsToDisplay
      : notificationsToDisplay.filter(
          (notification) => notification.status === statusFilter,
        );

  return (
    <>
      <div>
        <h1>Notifications</h1>

        <div className="header-actions">
          <SearchArea
            placeholder="Search notifications..."
            onChange={handleSearch}
          />

          <select
            className="select-input"
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value)}
          >
            <option value="ALL">All statuses</option>
            <option value="PENDING">Pending</option>
            <option value="SENT">Sent</option>
            <option value="FAILED">Failed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>

        <Table
          columns={columns}
          data={statusFilteredNotifications}
          onEdit={handleDetails}
          onDelete={handleConfirmCancel}
          noDataComponent={
            <div className="empty-state">No notifications found</div>
          }
        />
      </div>

      <NotificationDetailsModal
        isOpen={isDetailsModalOpen}
        notification={selectedNotification}
        onClose={() => {
          setIsDetailsModalOpen(false);
          setSelectedNotification(null);
        }}
        onRetry={handleConfirmRetry}
        onCancel={handleConfirmCancel}
      />

      <Modal
        isOpen={openCancelConfirmation}
        onClose={() => setOpenCancelConfirmation(false)}
        title="Cancel Notification"
      >
        <p>Are you sure you want to cancel this notification?</p>

        <div className="modal-actions">
          <button
            type="button"
            onClick={() => setOpenCancelConfirmation(false)}
            className="btn-secondary"
          >
            Back
          </button>

          <button onClick={handleCancelNotification} className="btn-danger">
            Cancel Notification
          </button>
        </div>
      </Modal>

      <Modal
        isOpen={openRetryConfirmation}
        onClose={() => setOpenRetryConfirmation(false)}
        title="Retry Notification"
      >
        <p>Do you want to retry sending this notification?</p>

        <div className="modal-actions">
          <button
            type="button"
            onClick={() => setOpenRetryConfirmation(false)}
            className="btn-secondary"
          >
            Cancel
          </button>

          <button onClick={handleRetryNotification} className="btn-primary">
            Retry
          </button>
        </div>
      </Modal>
    </>
  );
};

export default Notifications;
