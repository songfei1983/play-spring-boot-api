import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import UserProfileManagement from './UserProfileManagement';
import { userProfileApi, userApi } from '../services/api';

// Mock the APIs
jest.mock('../services/api', () => ({
  userProfileApi: {
    getAllProfiles: jest.fn(),
    createProfile: jest.fn(),
    updateProfile: jest.fn(),
    deleteProfile: jest.fn(),
    searchProfilesByAddress: jest.fn(),
  },
  userApi: {
    getAllUsers: jest.fn(),
  },
}));

// Mock window.confirm
Object.defineProperty(window, 'confirm', {
  writable: true,
  value: jest.fn(),
});

const mockUserProfileApi = userProfileApi as jest.Mocked<typeof userProfileApi>;
const mockUserApi = userApi as jest.Mocked<typeof userApi>;
const mockConfirm = window.confirm as jest.MockedFunction<typeof window.confirm>;

const mockUsers = [
  { id: 1, name: 'John Doe', email: 'john@example.com', age: 30 },
  { id: 2, name: 'Jane Smith', email: 'jane@example.com', age: 25 },
];

const mockProfiles = [
  {
    id: 1,
    userId: 1,
    age: 30,
    gender: '男',
    birthday: '1993-01-01',
    phoneNumber: '13800138000',
    address: '北京市朝阳区',
    occupation: '软件工程师',
    bio: '热爱编程的工程师',
  },
  {
    id: 2,
    userId: 2,
    age: 25,
    gender: '女',
    birthday: '1998-05-15',
    phoneNumber: '13900139000',
    address: '上海市浦东新区',
    occupation: '产品经理',
    bio: '专注用户体验设计',
  },
];

describe('UserProfileManagement', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUserProfileApi.getAllProfiles.mockResolvedValue({ data: mockProfiles } as any);
    mockUserApi.getAllUsers.mockResolvedValue({ data: mockUsers } as any);
  });

  it('renders user profile management header', async () => {
    render(<UserProfileManagement />);
    
    expect(screen.getByText('用户档案管理')).toBeInTheDocument();
    expect(screen.getByText('添加用户档案')).toBeInTheDocument();
  });

  it('shows loading state initially', () => {
    mockUserProfileApi.getAllProfiles.mockImplementation(() => new Promise(() => {}));
    render(<UserProfileManagement />);
    
    expect(screen.getByText('加载中...')).toBeInTheDocument();
  });

  it('loads and displays profiles on mount', async () => {
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(mockUserProfileApi.getAllProfiles).toHaveBeenCalledTimes(1);
      expect(mockUserApi.getAllUsers).toHaveBeenCalledTimes(1);
    });
    
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    expect(screen.getByText('北京市朝阳区')).toBeInTheDocument();
    expect(screen.getByText('上海市浦东新区')).toBeInTheDocument();
  });

  it('shows error message when fetching profiles fails', async () => {
    const errorMessage = 'Network Error';
    mockUserProfileApi.getAllProfiles.mockRejectedValue(new Error(errorMessage));
    
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText(`获取用户档案列表失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('shows no data message when profiles list is empty', async () => {
    mockUserProfileApi.getAllProfiles.mockResolvedValue({ data: [] } as any);
    
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('暂无用户档案数据')).toBeInTheDocument();
    });
  });

  it('displays profile data correctly in table', async () => {
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Check various data fields
    expect(screen.getByText('30')).toBeInTheDocument();
    expect(screen.getByText('男')).toBeInTheDocument();
    expect(screen.getByText('1993-01-01')).toBeInTheDocument();
    expect(screen.getByText('13800138000')).toBeInTheDocument();
    expect(screen.getByText('软件工程师')).toBeInTheDocument();
    expect(screen.getByText('25')).toBeInTheDocument();
    expect(screen.getByText('女')).toBeInTheDocument();
    expect(screen.getByText('产品经理')).toBeInTheDocument();
  });

  it('displays user names correctly using getUserName function', async () => {
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    });
  });

  it('displays fallback user ID when user not found', async () => {
    const profileWithUnknownUser = {
      ...mockProfiles[0],
      userId: 999,
    };
    mockUserProfileApi.getAllProfiles.mockResolvedValue({ data: [profileWithUnknownUser] } as any);
    
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('用户ID: 999')).toBeInTheDocument();
    });
  });

  it('opens add profile form when add button is clicked', async () => {
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    fireEvent.click(screen.getByText('添加用户档案'));
    
    expect(screen.getByText('添加用户档案')).toBeInTheDocument();
    expect(screen.getByLabelText('用户:')).toBeInTheDocument();
    expect(screen.getByLabelText('年龄:')).toBeInTheDocument();
    expect(screen.getByLabelText('性别:')).toBeInTheDocument();
    expect(screen.getByLabelText('地址:')).toBeInTheDocument();
  });

  it('creates a new profile successfully', async () => {
    mockUserProfileApi.createProfile.mockResolvedValue({ data: { id: 3 } } as any);
    
    render(<UserProfileManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加用户档案'));
    
    // Fill required fields
    await userEvent.selectOptions(screen.getByLabelText('用户:'), '1');
    await userEvent.type(screen.getByLabelText('地址:'), '深圳市南山区');
    await userEvent.type(screen.getByLabelText('年龄:'), '28');
    await userEvent.selectOptions(screen.getByLabelText('性别:'), '男');
    
    // Submit form
    fireEvent.click(screen.getByText('创建'));
    
    await waitFor(() => {
      expect(mockUserProfileApi.createProfile).toHaveBeenCalledWith(
        expect.objectContaining({
          userId: 1,
          address: '深圳市南山区',
          age: 28,
          gender: '男',
        })
      );
      expect(mockUserProfileApi.getAllProfiles).toHaveBeenCalledTimes(2); // Initial load + after create
    });
  });

  it('shows error when creating profile fails', async () => {
    const errorMessage = 'Validation failed';
    mockUserProfileApi.createProfile.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<UserProfileManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加用户档案'));
    
    // Fill required fields
    await userEvent.selectOptions(screen.getByLabelText('用户:'), '1');
    await userEvent.type(screen.getByLabelText('地址:'), '深圳市南山区');
    
    // Submit form
    fireEvent.click(screen.getByText('创建'));
    
    await waitFor(() => {
      expect(screen.getByText(`操作失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('opens edit form when edit button is clicked', async () => {
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    const editButtons = screen.getAllByText('编辑');
    fireEvent.click(editButtons[0]);
    
    expect(screen.getByText('编辑用户档案')).toBeInTheDocument();
    expect(screen.getByDisplayValue('30')).toBeInTheDocument();
    expect(screen.getByDisplayValue('男')).toBeInTheDocument();
    expect(screen.getByDisplayValue('北京市朝阳区')).toBeInTheDocument();
    expect(screen.getByDisplayValue('软件工程师')).toBeInTheDocument();
  });

  it('updates profile successfully', async () => {
    mockUserProfileApi.updateProfile.mockResolvedValue({ data: { id: 1 } } as any);
    
    render(<UserProfileManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Click edit button
    const editButtons = screen.getAllByText('编辑');
    fireEvent.click(editButtons[0]);
    
    // Update address
    const addressInput = screen.getByDisplayValue('北京市朝阳区');
    await userEvent.clear(addressInput);
    await userEvent.type(addressInput, '北京市海淀区');
    
    // Submit form
    fireEvent.click(screen.getByText('更新'));
    
    await waitFor(() => {
      expect(mockUserProfileApi.updateProfile).toHaveBeenCalledWith(
        1,
        expect.objectContaining({
          address: '北京市海淀区',
        })
      );
      expect(mockUserProfileApi.getAllProfiles).toHaveBeenCalledTimes(2); // Initial load + after update
    });
  });

  it('cancels form editing', async () => {
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Open add form
    fireEvent.click(screen.getByText('添加用户档案'));
    expect(screen.getByText('添加用户档案')).toBeInTheDocument();
    
    // Cancel form
    fireEvent.click(screen.getByText('取消'));
    expect(screen.queryByText('添加用户档案')).not.toBeInTheDocument();
  });

  it('deletes profile after confirmation', async () => {
    mockConfirm.mockReturnValue(true);
    mockUserProfileApi.deleteProfile.mockResolvedValue({} as any);
    
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    await waitFor(() => {
      expect(mockConfirm).toHaveBeenCalledWith('确定要删除这个用户档案吗？');
      expect(mockUserProfileApi.deleteProfile).toHaveBeenCalledWith(1);
      expect(mockUserProfileApi.getAllProfiles).toHaveBeenCalledTimes(2); // Initial load + after delete
    });
  });

  it('does not delete profile when confirmation is cancelled', async () => {
    mockConfirm.mockReturnValue(false);
    
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    expect(mockConfirm).toHaveBeenCalledWith('确定要删除这个用户档案吗？');
    expect(mockUserProfileApi.deleteProfile).not.toHaveBeenCalled();
  });

  it('shows error when deleting profile fails', async () => {
    mockConfirm.mockReturnValue(true);
    const errorMessage = 'Cannot delete profile';
    mockUserProfileApi.deleteProfile.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    await waitFor(() => {
      expect(screen.getByText(`删除用户档案失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('searches profiles by address', async () => {
    const searchResults = [mockProfiles[0]];
    mockUserProfileApi.searchProfilesByAddress.mockResolvedValue({ data: searchResults } as any);
    
    render(<UserProfileManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Enter search term
    await userEvent.type(screen.getByPlaceholderText('按地址搜索...'), '北京');
    
    // Click search button
    fireEvent.click(screen.getByText('搜索'));
    
    await waitFor(() => {
      expect(mockUserProfileApi.searchProfilesByAddress).toHaveBeenCalledWith('北京');
    });
  });

  it('resets search and fetches all profiles', async () => {
    render(<UserProfileManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Enter search term
    await userEvent.type(screen.getByPlaceholderText('按地址搜索...'), '北京');
    
    // Click reset button
    fireEvent.click(screen.getByText('重置'));
    
    expect(screen.getByPlaceholderText('按地址搜索...')).toHaveValue('');
    await waitFor(() => {
      expect(mockUserProfileApi.getAllProfiles).toHaveBeenCalledTimes(2); // Initial load + after reset
    });
  });

  it('handles empty search term by fetching all profiles', async () => {
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Click search with empty input
    fireEvent.click(screen.getByText('搜索'));
    
    await waitFor(() => {
      expect(mockUserProfileApi.getAllProfiles).toHaveBeenCalledTimes(2); // Initial load + search with empty term
    });
    expect(mockUserProfileApi.searchProfilesByAddress).not.toHaveBeenCalled();
  });

  it('shows error when search fails', async () => {
    const errorMessage = 'Search failed';
    mockUserProfileApi.searchProfilesByAddress.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<UserProfileManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Enter search term
    await userEvent.type(screen.getByPlaceholderText('按地址搜索...'), '北京');
    
    // Click search button
    fireEvent.click(screen.getByText('搜索'));
    
    await waitFor(() => {
      expect(screen.getByText(`搜索失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('handles form input changes correctly', async () => {
    render(<UserProfileManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加用户档案'));
    
    // Test various form inputs
    await userEvent.selectOptions(screen.getByLabelText('用户:'), '1');
    await userEvent.type(screen.getByLabelText('年龄:'), '30');
    await userEvent.selectOptions(screen.getByLabelText('性别:'), '男');
    await userEvent.type(screen.getByLabelText('生日:'), '1993-01-01');
    await userEvent.type(screen.getByLabelText('地址:'), '北京市朝阳区');
    await userEvent.type(screen.getByLabelText('电话号码:'), '13800138000');
    await userEvent.type(screen.getByLabelText('职业:'), '软件工程师');
    await userEvent.type(screen.getByLabelText('个人简介:'), '热爱编程的工程师');
    
    // Verify inputs are updated
    expect(screen.getByDisplayValue('1')).toBeInTheDocument();
    expect(screen.getByDisplayValue('30')).toBeInTheDocument();
    expect(screen.getByDisplayValue('男')).toBeInTheDocument();
    expect(screen.getByDisplayValue('1993-01-01')).toBeInTheDocument();
    expect(screen.getByDisplayValue('北京市朝阳区')).toBeInTheDocument();
    expect(screen.getByDisplayValue('13800138000')).toBeInTheDocument();
    expect(screen.getByDisplayValue('软件工程师')).toBeInTheDocument();
    expect(screen.getByDisplayValue('热爱编程的工程师')).toBeInTheDocument();
  });

  it('handles age input with empty value', async () => {
    render(<UserProfileManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加用户档案'));
    
    // Type age and then clear it
    const ageInput = screen.getByLabelText('年龄:');
    await userEvent.type(ageInput, '30');
    await userEvent.clear(ageInput);
    
    expect(ageInput).toHaveValue('');
  });

  it('disables user selection when editing profile', async () => {
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Click edit button
    const editButtons = screen.getAllByText('编辑');
    fireEvent.click(editButtons[0]);
    
    const userSelect = screen.getByLabelText('用户:');
    expect(userSelect).toBeDisabled();
  });

  it('displays dash for empty optional fields', async () => {
    const profileWithEmptyFields = {
      id: 3,
      userId: 1,
      age: undefined,
      gender: '',
      birthday: '',
      phoneNumber: '',
      address: '北京市',
      occupation: '',
      bio: '',
    };
    mockUserProfileApi.getAllProfiles.mockResolvedValue({ data: [profileWithEmptyFields] } as any);
    
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      const dashElements = screen.getAllByText('-');
      expect(dashElements.length).toBeGreaterThan(0);
    });
  });

  it('handles users API error gracefully', async () => {
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
    mockUserApi.getAllUsers.mockRejectedValue(new Error('Users API Error'));
    
    render(<UserProfileManagement />);
    
    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith('获取用户列表失败:', expect.any(Error));
    });
    
    consoleSpy.mockRestore();
  });

  it('shows loading state during form submission', async () => {
    mockUserProfileApi.createProfile.mockImplementation(() => new Promise(() => {}));
    
    render(<UserProfileManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加用户档案'));
    
    // Fill required fields
    await userEvent.selectOptions(screen.getByLabelText('用户:'), '1');
    await userEvent.type(screen.getByLabelText('地址:'), '深圳市南山区');
    
    // Submit form
    fireEvent.click(screen.getByText('创建'));
    
    expect(screen.getByText('处理中...')).toBeInTheDocument();
  });
});