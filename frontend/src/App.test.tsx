import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import App from './App';

// Mock the child components
jest.mock('./components/UserManagement', () => {
  return function MockUserManagement() {
    return <div data-testid="user-management">User Management Component</div>;
  };
});

jest.mock('./components/UserProfileManagement', () => {
  return function MockUserProfileManagement() {
    return <div data-testid="user-profile-management">User Profile Management Component</div>;
  };
});

jest.mock('./components/ActivityTrackManagement', () => {
  return function MockActivityTrackManagement() {
    return <div data-testid="activity-track-management">Activity Track Management Component</div>;
  };
});

jest.mock('./components/PurchaseHistoryManagement', () => {
  return function MockPurchaseHistoryManagement() {
    return <div data-testid="purchase-history-management">Purchase History Management Component</div>;
  };
});

// Mock localStorage
const mockLocalStorage = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};

Object.defineProperty(window, 'localStorage', {
  value: mockLocalStorage,
});

describe('App Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockLocalStorage.getItem.mockReturnValue(null);
  });

  test('renders main heading', () => {
    render(<App />);
    const headingElement = screen.getByRole('heading', { name: /spring boot api 管理系统/i });
    expect(headingElement).toBeInTheDocument();
  });

  test('renders navigation tabs', () => {
    render(<App />);
    
    expect(screen.getByRole('button', { name: /用户管理/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /用户档案/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /活动跟踪/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /购买历史/i })).toBeInTheDocument();
  });

  test('renders swagger ui link', () => {
    render(<App />);
    const linkElement = screen.getByRole('link', { name: /swagger ui/i });
    expect(linkElement).toBeInTheDocument();
    expect(linkElement).toHaveAttribute('href', 'http://localhost:8080/swagger-ui.html');
    expect(linkElement).toHaveAttribute('target', '_blank');
    expect(linkElement).toHaveAttribute('rel', 'noopener noreferrer');
  });

  test('users tab is active by default when no localStorage value', () => {
    render(<App />);
    
    const usersTab = screen.getByRole('button', { name: /用户管理/i });
    expect(usersTab).toHaveClass('active');
    expect(screen.getByTestId('user-management')).toBeInTheDocument();
  });

  test('loads active tab from localStorage', () => {
    mockLocalStorage.getItem.mockReturnValue('profiles');
    
    render(<App />);
    
    expect(mockLocalStorage.getItem).toHaveBeenCalledWith('activeTab');
    const profilesTab = screen.getByRole('button', { name: /用户档案/i });
    expect(profilesTab).toHaveClass('active');
    expect(screen.getByTestId('user-profile-management')).toBeInTheDocument();
  });

  test('saves active tab to localStorage when changed', () => {
    render(<App />);
    
    const profilesTab = screen.getByRole('button', { name: /用户档案/i });
    fireEvent.click(profilesTab);
    
    expect(mockLocalStorage.setItem).toHaveBeenCalledWith('activeTab', 'profiles');
  });

  test('switches to user profiles tab when clicked', () => {
    render(<App />);
    
    const profilesTab = screen.getByRole('button', { name: /用户档案/i });
    fireEvent.click(profilesTab);
    
    expect(profilesTab).toHaveClass('active');
    expect(screen.getByTestId('user-profile-management')).toBeInTheDocument();
    expect(screen.queryByTestId('user-management')).not.toBeInTheDocument();
  });

  test('switches to activity tracking tab when clicked', () => {
    render(<App />);
    
    const activitiesTab = screen.getByRole('button', { name: /活动跟踪/i });
    fireEvent.click(activitiesTab);
    
    expect(activitiesTab).toHaveClass('active');
    expect(screen.getByTestId('activity-track-management')).toBeInTheDocument();
    expect(screen.queryByTestId('user-management')).not.toBeInTheDocument();
  });

  test('switches to purchase history tab when clicked', () => {
    render(<App />);
    
    const purchasesTab = screen.getByRole('button', { name: /购买历史/i });
    fireEvent.click(purchasesTab);
    
    expect(purchasesTab).toHaveClass('active');
    expect(screen.getByTestId('purchase-history-management')).toBeInTheDocument();
    expect(screen.queryByTestId('user-management')).not.toBeInTheDocument();
  });

  test('switches back to users tab when clicked', () => {
    render(<App />);
    
    // First switch to another tab
    const profilesTab = screen.getByRole('button', { name: /用户档案/i });
    fireEvent.click(profilesTab);
    expect(screen.getByTestId('user-profile-management')).toBeInTheDocument();
    
    // Then switch back to users tab
    const usersTab = screen.getByRole('button', { name: /用户管理/i });
    fireEvent.click(usersTab);
    
    expect(usersTab).toHaveClass('active');
    expect(screen.getByTestId('user-management')).toBeInTheDocument();
    expect(screen.queryByTestId('user-profile-management')).not.toBeInTheDocument();
  });

  test('only one tab is active at a time', () => {
    render(<App />);
    
    const usersTab = screen.getByRole('button', { name: /用户管理/i });
    const profilesTab = screen.getByRole('button', { name: /用户档案/i });
    const activitiesTab = screen.getByRole('button', { name: /活动跟踪/i });
    const purchasesTab = screen.getByRole('button', { name: /购买历史/i });
    
    // Initially users tab should be active
    expect(usersTab).toHaveClass('active');
    expect(profilesTab).not.toHaveClass('active');
    expect(activitiesTab).not.toHaveClass('active');
    expect(purchasesTab).not.toHaveClass('active');
    
    // Click profiles tab
    fireEvent.click(profilesTab);
    
    expect(usersTab).not.toHaveClass('active');
    expect(profilesTab).toHaveClass('active');
    expect(activitiesTab).not.toHaveClass('active');
    expect(purchasesTab).not.toHaveClass('active');
  });

  test('renders correct component structure', () => {
    render(<App />);
    
    expect(screen.getByRole('banner')).toBeInTheDocument(); // header
    expect(screen.getByRole('main')).toBeInTheDocument(); // main
    expect(screen.getByRole('contentinfo')).toBeInTheDocument(); // footer
  });

  test('handles default case in renderActiveComponent', () => {
    // Mock useState to return an invalid tab
    const mockSetState = jest.fn();
    jest.spyOn(React, 'useState')
      .mockImplementationOnce(() => ['invalid-tab', mockSetState])
      .mockImplementationOnce(() => ['invalid-tab', mockSetState]);
    
    render(<App />);
    
    // Should render UserManagement component as default
    expect(screen.getByTestId('user-management')).toBeInTheDocument();
  });

  test('applies correct CSS classes to navigation tabs', () => {
    render(<App />);
    
    const usersTab = screen.getByRole('button', { name: /用户管理/i });
    const profilesTab = screen.getByRole('button', { name: /用户档案/i });
    
    expect(usersTab).toHaveClass('nav-tab', 'active');
    expect(profilesTab).toHaveClass('nav-tab');
    expect(profilesTab).not.toHaveClass('active');
  });

  test('applies correct CSS classes to main elements', () => {
    const { container } = render(<App />);
    
    expect(container.firstChild).toHaveClass('App');
    expect(screen.getByRole('banner')).toHaveClass('app-header');
    expect(screen.getByRole('main')).toHaveClass('main-content');
    expect(screen.getByRole('contentinfo')).toHaveClass('app-footer');
    
    const nav = container.querySelector('.nav-tabs');
    expect(nav).toBeInTheDocument();
  });
});
