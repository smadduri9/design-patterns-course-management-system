import { Route, Routes } from 'react-router-dom';

import { AppShell } from './app/AppShell';

function ScaffoldHome() {
  return (
    <section className="card" aria-labelledby="scaffold-title">
      <p className="eyebrow">Phase F1</p>
      <h2 id="scaffold-title">Frontend shell is ready</h2>
      <p>
        This React app is configured to consume the Spring Boot API through relative
        <code> /api/app</code> URLs. Product screens and workflows will be added in later phases.
      </p>
    </section>
  );
}

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
        <Route path="/" element={<ScaffoldHome />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </AppShell>
  );
}
