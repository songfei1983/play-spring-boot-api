import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('renders Spring Boot API management system', () => {
  render(<App />);
  const headerElement = screen.getByText(/Spring Boot API 管理系统/i);
  expect(headerElement).toBeInTheDocument();
});

test('renders user management tab', () => {
  render(<App />);
  const userManagementTab = screen.getByRole('button', { name: /用户管理/i });
  expect(userManagementTab).toBeInTheDocument();
  expect(userManagementTab).toHaveClass('active');
});
