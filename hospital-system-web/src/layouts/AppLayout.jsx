import { Outlet } from 'react-router-dom';
import { Container, Header, Sidebar } from '../components/index.js';
import './AppLayout.css';

export function AppLayout() {
  return (
    <div className="app-layout">
      <Sidebar />
      <div className="app-content">
        <Header />

        <main>
          <Container>
            <Outlet />
          </Container>
        </main>
      </div>
    </div>
  );
}
