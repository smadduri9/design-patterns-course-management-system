import { Route, Routes } from 'react-router-dom';

import { AppShell } from './app/AppShell';
import { DashboardPage } from './pages/DashboardPage';
import { FullTracePage } from './pages/FullTracePage';

function NotFound() {
  return (
    <section className="card" aria-labelledby="not-found-title">
      <h2 id="not-found-title">Route placeholder</h2>
      <p>This route is reserved for a future frontend phase.</p>
    </section>
  );
}

export function App() {
  return (
    <AppShell>
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/trace" element={<FullTracePage />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </AppShell>
  );
}
