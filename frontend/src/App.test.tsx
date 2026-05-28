import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';

import { App } from './App';

describe('App shell', () => {
  it('renders the scaffold home route', () => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>,
    );

    expect(screen.getByRole('heading', { name: /course management system/i })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /frontend shell is ready/i })).toBeInTheDocument();
    expect(screen.getByText(/\/api\/app/i)).toBeInTheDocument();
  });
});
