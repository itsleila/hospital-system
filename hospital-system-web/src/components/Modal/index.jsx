import './index.css';

const Modal = ({ isOpen, onClose, title, children }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>{title}</h3>
        {children}
        <button onClick={onClose} className="close-modal-button">
          X
        </button>
      </div>
    </div>
  );
};

export default Modal;
