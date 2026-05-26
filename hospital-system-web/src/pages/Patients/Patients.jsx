import { SearchArea, Table } from '../../components';
import '../pages.css';

const Patients = () => {
  const patients = [
    {
      id: 1,
      name: 'Maria Silva',
      age: 42,
      condition: 'Diabetes',
    },
    {
      id: 2,
      name: 'João Santos',
      age: 35,
      condition: 'Hypertension',
    },
  ];

  const columns = [
    {
      name: 'Name',
      selector: (row) => row.name,
    },
    {
      name: 'Age',
      selector: (row) => row.age,
    },
    {
      name: 'Condition',
      selector: (row) => row.condition,
    },
  ];

  function handleEdit(patient) {
    console.log('Edit:', patient);
  }

  function handleDelete(patient) {
    console.log('Delete:', patient);
  }

  return (
    <div>
      <h1>Patients</h1>
      <div className="header-actions">
        <SearchArea placeholder="Search patients..." />
        <button
          onClick={() => console.log('Add new patient')}
          className="btn-primary"
        >
          Add Patient
        </button>
      </div>

      <Table
        columns={columns}
        data={patients}
        onEdit={handleEdit}
        onDelete={handleDelete}
        noDataComponent={<div className="empty-state">No patients found</div>}
      />
    </div>
  );
};
export default Patients;
