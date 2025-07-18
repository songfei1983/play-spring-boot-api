import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import UserManagement from './UserManagement';
import { userApi } from '../services/api';

// Mock the API
jest.mock('../services/api', () => ({
  userApi: {
    getAllUsers: jest.fn(),
    createUser: jest.fn(),
    updateUser: jest.fn(),
    deleteUser: jest.fn(),
  },
}));

// Mock window.confirm
Object.defineProperty(window, 'confirm', {
  writable: true,
  value: jest.fn(),
});

const mockUserApi = userApi as jest.Mocked<typeof userApi>;
const mockConfirm = window.confirm as jest.MockedFunction<typeof window.confirm>;

const mockUsers = [
  { id: 1, name: '张三', email: 'zhangsan@example.com' },
  { id: 2, name: '李四', email: 'lisi@example.com' },
];

describe('UserManagement', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUserApi.getAllUsers.mockResolvedValue({ data: mockUsers } as any);
  });

  it('renders user management header', async () => {
    render(<UserManagement />);
    
    expect(screen.getByText('用户管理')).toBeInTheDocument();
    expect(screen.getByText('添加用户')).toBeInTheDocument();
  });

  it('loads and displays users on mount', async () => {
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(mockUserApi.getAllUsers).toHaveBeenCalledTimes(1);
    });
    
    expect(screen.getByText('张三')).toBeInTheDocument();
    expect(screen.getByText('zhangsan@example.com')).toBeInTheDocument();
    expect(screen.getByText('李四')).toBeInTheDocument();
    expect(screen.getByText('lisi@example.com')).toBeInTheDocument();
  });

  it('shows loading state while fetching users', () => {
    mockUserApi.getAllUsers.mockImplementation(() => new Promise(() => {}));
    render(<UserManagement />);
    
    expect(screen.getByText('加载中...')).toBeInTheDocument();
  });

  it('shows error message when fetching users fails', async () => {
    const errorMessage = 'Network Error';
    mockUserApi.getAllUsers.mockRejectedValue(new Error(errorMessage));
    
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText(`获取用户列表失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('shows no data message when users list is empty', async () => {
    mockUserApi.getAllUsers.mockResolvedValue({ data: [] } as any);
    
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('暂无用户数据')).toBeInTheDocument();
    });
  });

  it('opens add user form when add button is clicked', async () => {
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    fireEvent.click(screen.getByText('添加用户'));
    
    expect(screen.getByText('添加用户')).toBeInTheDocument();
    expect(screen.getByLabelText('姓名:')).toBeInTheDocument();
    expect(screen.getByLabelText('邮箱:')).toBeInTheDocument();
  });

  it('creates a new user successfully', async () => {
    mockUserApi.createUser.mockResolvedValue({ data: { id: 3, name: '王五', email: 'wangwu@example.com' } } as any);
    
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加用户'));
    
    // Fill form
    await userEvent.type(screen.getByLabelText('姓名:'), '王五');
    await userEvent.type(screen.getByLabelText('邮箱:'), 'wangwu@example.com');
    
    // Submit form
    fireEvent.click(screen.getByText('创建'));
    
    await waitFor(() => {
      expect(mockUserApi.createUser).toHaveBeenCalledWith({
        name: '王五',
        email: 'wangwu@example.com',
      });
      expect(mockUserApi.getAllUsers).toHaveBeenCalledTimes(2); // Initial load + after create
    });
  });

  it('shows error when creating user fails', async () => {
    const errorMessage = 'Email already exists';
    mockUserApi.createUser.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加用户'));
    
    // Fill form
    await userEvent.type(screen.getByLabelText('姓名:'), '王五');
    await userEvent.type(screen.getByLabelText('邮箱:'), 'wangwu@example.com');
    
    // Submit form
    fireEvent.click(screen.getByText('创建'));
    
    await waitFor(() => {
      expect(screen.getByText(`操作失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('opens edit form when edit button is clicked', async () => {
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    const editButtons = screen.getAllByText('编辑');
    fireEvent.click(editButtons[0]);
    
    expect(screen.getByText('编辑用户')).toBeInTheDocument();
    expect(screen.getByDisplayValue('张三')).toBeInTheDocument();
    expect(screen.getByDisplayValue('zhangsan@example.com')).toBeInTheDocument();
  });

  it('updates user successfully', async () => {
    mockUserApi.updateUser.mockResolvedValue({ data: { id: 1, name: '张三三', email: 'zhangsan@example.com' } } as any);
    
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    // Click edit button
    const editButtons = screen.getAllByText('编辑');
    fireEvent.click(editButtons[0]);
    
    // Update name
    const nameInput = screen.getByDisplayValue('张三');
    await userEvent.clear(nameInput);
    await userEvent.type(nameInput, '张三三');
    
    // Submit form
    fireEvent.click(screen.getByText('更新'));
    
    await waitFor(() => {
      expect(mockUserApi.updateUser).toHaveBeenCalledWith(1, {
        name: '张三三',
        email: 'zhangsan@example.com',
      });
      expect(mockUserApi.getAllUsers).toHaveBeenCalledTimes(2); // Initial load + after update
    });
  });

  it('cancels form editing', async () => {
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    // Open add form
    fireEvent.click(screen.getByText('添加用户'));
    expect(screen.getByText('添加用户')).toBeInTheDocument();
    
    // Cancel form
    fireEvent.click(screen.getByText('取消'));
    expect(screen.queryByText('添加用户')).not.toBeInTheDocument();
  });

  it('deletes user after confirmation', async () => {
    mockConfirm.mockReturnValue(true);
    mockUserApi.deleteUser.mockResolvedValue({} as any);
    
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    await waitFor(() => {
      expect(mockConfirm).toHaveBeenCalledWith('确定要删除这个用户吗？');
      expect(mockUserApi.deleteUser).toHaveBeenCalledWith(1);
      expect(mockUserApi.getAllUsers).toHaveBeenCalledTimes(2); // Initial load + after delete
    });
  });

  it('does not delete user when confirmation is cancelled', async () => {
    mockConfirm.mockReturnValue(false);
    
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    expect(mockConfirm).toHaveBeenCalledWith('确定要删除这个用户吗？');
    expect(mockUserApi.deleteUser).not.toHaveBeenCalled();
  });

  it('shows error when deleting user fails', async () => {
    mockConfirm.mockReturnValue(true);
    const errorMessage = 'Cannot delete user';
    mockUserApi.deleteUser.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    await waitFor(() => {
      expect(screen.getByText(`删除用户失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('disables buttons when loading', async () => {
    mockUserApi.createUser.mockImplementation(() => new Promise(() => {}));
    
    render(<UserManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加用户'));
    
    // Fill form
    await userEvent.type(screen.getByLabelText('姓名:'), '王五');
    await userEvent.type(screen.getByLabelText('邮箱:'), 'wangwu@example.com');
    
    // Submit form (this will trigger loading state)
    fireEvent.click(screen.getByText('创建'));
    
    // Check that buttons are disabled
    expect(screen.getByText('处理中...')).toBeInTheDocument();
    expect(screen.getByText('处理中...')).toBeDisabled();
  });
});