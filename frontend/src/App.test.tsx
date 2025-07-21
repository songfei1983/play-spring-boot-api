import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('renders user management system', () => {
  render(<App />);
  const titleElement = screen.getByText(/Spring Boot API 管理系统/i);
  expect(titleElement).toBeInTheDocument();
});

test('renders swagger ui link', () => {
  render(<App />);
  const linkElement = screen.getByText(/Swagger UI/i);
  expect(linkElement).toBeInTheDocument();
});
