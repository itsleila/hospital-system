import { Modal } from '../../components';

const NotificationDetailsModal = ({
  isOpen,
  notification,
  onClose,
  onRetry,
  onCancel,
}) => {
  if (!notification) return null;

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

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Notification Details">
      <div className="selected-card">
        <h3>Notification #{notification.id}</h3>

        <p>
          <strong>Status:</strong> {notification.status}
        </p>

        <p>
          <strong>Type:</strong> {formatType(notification.type)}
        </p>
      </div>

      <div className="form-group">
        <label>Patient</label>
        <p>{notification.patientName}</p>
      </div>

      <div className="form-group">
        <label>Patient Phone</label>
        <p>{notification.patientPhone}</p>
      </div>

      <div className="form-group">
        <label>Doctor</label>
        <p>{notification.doctorName}</p>
      </div>

      <div className="form-group">
        <label>Appointment</label>
        <p>#{notification.appointmentId}</p>
      </div>

      <div className="form-group">
        <label>Appointment Date and Time</label>
        <p>{formatDate(notification.appointmentDateTime)}</p>
      </div>
      <div className="form-group">
        <label>Appointment Status</label>
        <p>{notification.appointmentStatus}</p>
      </div>

      <div className="form-group">
        <label>Created At</label>
        <p>{formatDate(notification.createdAt)}</p>
      </div>

      <div className="form-group">
        <label>Sent At</label>
        <p>{formatDate(notification.sentAt)}</p>
      </div>

      <div className="modal-actions">
        <button type="button" onClick={onClose} className="btn-secondary">
          Close
        </button>

        {notification.status === 'FAILED' && (
          <button
            type="button"
            onClick={() => {
              onClose();
              onRetry(notification);
            }}
            className="btn-primary"
          >
            Retry
          </button>
        )}

        {notification.status === 'PENDING' && (
          <button
            type="button"
            onClick={() => {
              onClose();
              onCancel(notification);
            }}
            className="btn-danger"
          >
            Cancel Notification
          </button>
        )}
      </div>
    </Modal>
  );
};

export default NotificationDetailsModal;
