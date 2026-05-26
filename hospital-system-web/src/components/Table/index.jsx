import './index.css';
import DataTable from 'react-data-table-component';
import { FaEdit, FaTrash } from 'react-icons/fa';

const Table = ({ columns, data, onEdit, onDelete, noDataComponent }) => {
  const customStyles = {
    rows: {
      style: {
        minHeight: '50px',
        fontSize: '15px',
      },
    },

    headCells: {
      style: {
        fontWeight: 700,
        color: '#101828',
        fontSize: '14px',
        backgroundColor: '#6670854f',
      },
    },

    cells: {
      style: {
        color: '#667085',
      },
    },
  };
  const actionColumn = {
    name: 'Actions',
    cell: (row) => (
      <div className="table-actions">
        <button className="edit-btn" onClick={() => onEdit(row)}>
          <FaEdit />
        </button>
        <button className="delete-btn" onClick={() => onDelete(row)}>
          <FaTrash />
        </button>
      </div>
    ),
  };
  return (
    <div className="table-container">
      <DataTable
        columns={[...columns, actionColumn]}
        data={data}
        pagination
        highlightOnHover
        customStyles={customStyles}
        pointerOnHover
        noDataComponent={noDataComponent}
      />
    </div>
  );
};

export default Table;
