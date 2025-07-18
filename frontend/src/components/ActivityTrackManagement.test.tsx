import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ActivityTrackManagement from './ActivityTrackManagement';
import { activityTrackApi, userApi } from '../services/api';

// Mock the APIs
jest.mock('../services/api', () => ({
  activityTrackApi: {
    getAllActivities: jest.fn(),
    getActivitiesByUserId: jest.fn(),
    createActivity: jest.fn(),
    updateActivity: jest.fn(),
    deleteActivity: jest.fn(),
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

const mockActivityTrackApi = activityTrackApi as jest.Mocked<typeof activityTrackApi>;
const mockUserApi = userApi as jest.Mocked<typeof userApi>;
const mockConfirm = window.confirm as jest.MockedFunction<typeof window.confirm>;

const mockUsers = [
  { id: 1, name: '张三', email: 'zhangsan@example.com' },
  { id: 2, name: '李四', email: 'lisi@example.com' },
];

const mockActivities = [
  {
    id: 1,
    userId: 1,
    activityType: '登录',
    description: '用户登录系统',
    location: '北京市',
    deviceType: '手机',
    ipAddress: '192.168.1.100',
    duration: 30,
    createdAt: '2023-01-01T10:00:00Z',
  },
  {
    id: 2,
    userId: 2,
    activityType: '浏览',
    description: '浏览商品页面',
    location: '上海市',
    deviceType: '电脑',
    ipAddress: '192.168.1.101',
    duration: 60,
    createdAt: '2023-01-01T11:00:00Z',
  },
];

describe('ActivityTrackManagement', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockActivityTrackApi.getAllActivities.mockResolvedValue({ data: mockActivities } as any);
    mockUserApi.getAllUsers.mockResolvedValue({ data: mockUsers } as any);
  });

  it('renders activity track management header', async () => {
    render(<ActivityTrackManagement />);
    
    expect(screen.getByText('活动跟踪管理')).toBeInTheDocument();
    expect(screen.getByText('添加活动跟踪')).toBeInTheDocument();
  });

  it('loads and displays activities and users on mount', async () => {
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(mockActivityTrackApi.getAllActivities).toHaveBeenCalledTimes(1);
      expect(mockUserApi.getAllUsers).toHaveBeenCalledTimes(1);
    });
    
    expect(screen.getByText('登录')).toBeInTheDocument();
    expect(screen.getByText('用户登录系统')).toBeInTheDocument();
    expect(screen.getByText('浏览')).toBeInTheDocument();
    expect(screen.getByText('浏览商品页面')).toBeInTheDocument();
  });

  it('shows loading state while fetching activities', () => {
    mockActivityTrackApi.getAllActivities.mockImplementation(() => new Promise(() => {}));
    render(<ActivityTrackManagement />);
    
    expect(screen.getByText('加载中...')).toBeInTheDocument();
  });

  it('shows error message when fetching activities fails', async () => {
    const errorMessage = 'Network Error';
    mockActivityTrackApi.getAllActivities.mockRejectedValue(new Error(errorMessage));
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText(`获取活动跟踪列表失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('shows no data message when activities list is empty', async () => {
    mockActivityTrackApi.getAllActivities.mockResolvedValue({ data: [] } as any);
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('暂无活动跟踪数据')).toBeInTheDocument();
    });
  });

  it('displays user names correctly', async () => {
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
      expect(screen.getByText('李四')).toBeInTheDocument();
    });
  });

  it('formats timestamps correctly', async () => {
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      // Check that timestamps are formatted (exact format may vary by locale)
      const timestampElements = screen.getAllByText(/2023/);
      expect(timestampElements.length).toBeGreaterThan(0);
    });
  });

  it('truncates long descriptions and locations', async () => {
    const longActivity = {
      ...mockActivities[0],
      description: 'This is a very long description that should be truncated when displayed in the table',
      location: 'This is a very long location name that should be truncated',
    };
    mockActivityTrackApi.getAllActivities.mockResolvedValue({ data: [longActivity] } as any);
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      // Description is truncated to 30 characters + '...'
      expect(screen.getByText('This is a very long descriptio...')).toBeInTheDocument();
      // Location is truncated to 15 characters + '...'
      expect(screen.getByText('This is a very...')).toBeInTheDocument();
    });
  });

  it('opens add activity form when add button is clicked', async () => {
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    fireEvent.click(screen.getByText('添加活动跟踪'));
    
    expect(screen.getByText('添加活动跟踪')).toBeInTheDocument();
    expect(screen.getByLabelText('用户:')).toBeInTheDocument();
    expect(screen.getByLabelText('活动类型:')).toBeInTheDocument();
    expect(screen.getByLabelText('描述:')).toBeInTheDocument();
  });

  it('creates a new activity successfully', async () => {
    mockActivityTrackApi.createActivity.mockResolvedValue({ data: { id: 3 } } as any);
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加活动跟踪'));
    
    // Fill form
    await userEvent.selectOptions(screen.getByLabelText('用户:'), '1');
    await userEvent.type(screen.getByLabelText('活动类型:'), '购买');
    await userEvent.type(screen.getByLabelText('描述:'), '购买商品');
    
    // Submit form
    fireEvent.click(screen.getByText('创建'));
    
    await waitFor(() => {
      expect(mockActivityTrackApi.createActivity).toHaveBeenCalledWith(
        expect.objectContaining({
          userId: 1,
          activityType: '购买',
          description: '购买商品',
        })
      );
      expect(mockActivityTrackApi.getAllActivities).toHaveBeenCalledTimes(2); // Initial load + after create
    });
  });

  it('shows error when creating activity fails', async () => {
    const errorMessage = 'Validation failed';
    mockActivityTrackApi.createActivity.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加活动跟踪'));
    
    // Fill form
    await userEvent.selectOptions(screen.getByLabelText('用户:'), '1');
    await userEvent.type(screen.getByLabelText('活动类型:'), '购买');
    await userEvent.type(screen.getByLabelText('描述:'), '购买商品');
    
    // Submit form
    fireEvent.click(screen.getByText('创建'));
    
    await waitFor(() => {
      expect(screen.getByText(`操作失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('opens edit form when edit button is clicked', async () => {
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    const editButtons = screen.getAllByText('编辑');
    fireEvent.click(editButtons[0]);
    
    expect(screen.getByText('编辑活动跟踪')).toBeInTheDocument();
    expect(screen.getByDisplayValue('登录')).toBeInTheDocument();
    expect(screen.getByDisplayValue('用户登录系统')).toBeInTheDocument();
  });

  it('updates activity successfully', async () => {
    mockActivityTrackApi.updateActivity.mockResolvedValue({ data: { id: 1 } } as any);
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    // Click edit button
    const editButtons = screen.getAllByText('编辑');
    fireEvent.click(editButtons[0]);
    
    // Update activity type
    const activityTypeInput = screen.getByDisplayValue('登录');
    await userEvent.clear(activityTypeInput);
    await userEvent.type(activityTypeInput, '登出');
    
    // Submit form
    fireEvent.click(screen.getByText('更新'));
    
    await waitFor(() => {
      expect(mockActivityTrackApi.updateActivity).toHaveBeenCalledWith(
        1,
        expect.objectContaining({
          activityType: '登出',
        })
      );
      expect(mockActivityTrackApi.getAllActivities).toHaveBeenCalledTimes(2); // Initial load + after update
    });
  });

  it('cancels form editing', async () => {
    render(<ActivityTrackManagement />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
    
    // Open add form
    fireEvent.click(screen.getByText('添加活动跟踪'));
    expect(screen.getByText('创建')).toBeInTheDocument();
    
    // Cancel form
    fireEvent.click(screen.getByText('取消'));
    expect(screen.queryByText('创建')).not.toBeInTheDocument();
  });

  it('deletes activity after confirmation', async () => {
    mockConfirm.mockReturnValue(true);
    mockActivityTrackApi.deleteActivity.mockResolvedValue({} as any);
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    await waitFor(() => {
      expect(mockConfirm).toHaveBeenCalledWith('确定要删除这个活动跟踪吗？');
      expect(mockActivityTrackApi.deleteActivity).toHaveBeenCalledWith(1);
      expect(mockActivityTrackApi.getAllActivities).toHaveBeenCalledTimes(2); // Initial load + after delete
    });
  });

  it('does not delete activity when confirmation is cancelled', async () => {
    mockConfirm.mockReturnValue(false);
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    expect(mockConfirm).toHaveBeenCalledWith('确定要删除这个活动跟踪吗？');
    expect(mockActivityTrackApi.deleteActivity).not.toHaveBeenCalled();
  });

  it('shows error when deleting activity fails', async () => {
    mockConfirm.mockReturnValue(true);
    const errorMessage = 'Cannot delete activity';
    mockActivityTrackApi.deleteActivity.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    await waitFor(() => {
      expect(screen.getByText(`删除活动跟踪失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('searches activities by user ID', async () => {
    const userActivities = [mockActivities[0]];
    mockActivityTrackApi.getActivitiesByUserId.mockResolvedValue({ data: userActivities } as any);
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    // Select user for search
    await userEvent.selectOptions(screen.getByDisplayValue('选择用户进行搜索...'), '1');
    
    // Click search button
    fireEvent.click(screen.getByText('搜索'));
    
    await waitFor(() => {
      expect(mockActivityTrackApi.getActivitiesByUserId).toHaveBeenCalledWith(1);
    });
  });

  it('resets search and shows all activities', async () => {
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    // Select user for search
    await userEvent.selectOptions(screen.getByDisplayValue('选择用户进行搜索...'), '1');
    
    // Click reset button
    fireEvent.click(screen.getByText('重置'));
    
    await waitFor(() => {
      expect(mockActivityTrackApi.getAllActivities).toHaveBeenCalledTimes(2); // Initial load + after reset
    });
  });

  it('handles search with no user selected', async () => {
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    // Click search without selecting user
    fireEvent.click(screen.getByText('搜索'));
    
    await waitFor(() => {
      expect(mockActivityTrackApi.getAllActivities).toHaveBeenCalledTimes(2); // Initial load + search fallback
    });
  });

  it('shows error when search fails', async () => {
    const errorMessage = 'Search failed';
    mockActivityTrackApi.getActivitiesByUserId.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    // Select user for search
    await userEvent.selectOptions(screen.getByDisplayValue('选择用户进行搜索...'), '1');
    
    // Click search button
    fireEvent.click(screen.getByText('搜索'));
    
    await waitFor(() => {
      expect(screen.getByText(`搜索失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('handles form input changes correctly', async () => {
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加活动跟踪'));
    
    // Test various form inputs
    await userEvent.type(screen.getByLabelText('经度:'), '116.404');
    await userEvent.type(screen.getByLabelText('纬度:'), '39.915');
    await userEvent.type(screen.getByLabelText('位置:'), '北京市');
    await userEvent.type(screen.getByLabelText('IP地址:'), '192.168.1.1');
    await userEvent.selectOptions(screen.getByLabelText('设备类型:'), '手机');
    await userEvent.type(screen.getByLabelText('操作系统:'), 'iOS 15.0');
    await userEvent.type(screen.getByLabelText('浏览器:'), 'Safari');
    await userEvent.type(screen.getByLabelText('会话ID:'), 'session_123');
    await userEvent.type(screen.getByLabelText('页面URL:'), '/home');
    await userEvent.type(screen.getByLabelText('持续时间(秒):'), '120');
    
    // Verify inputs are updated
    expect(screen.getByDisplayValue('116.404')).toBeInTheDocument();
    expect(screen.getByDisplayValue('39.915')).toBeInTheDocument();
    expect(screen.getByDisplayValue('北京市')).toBeInTheDocument();
    expect(screen.getByDisplayValue('192.168.1.1')).toBeInTheDocument();
    expect(screen.getByDisplayValue('手机')).toBeInTheDocument();
    expect(screen.getByDisplayValue('iOS 15.0')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Safari')).toBeInTheDocument();
    expect(screen.getByDisplayValue('session_123')).toBeInTheDocument();
    expect(screen.getByDisplayValue('/home')).toBeInTheDocument();
    expect(screen.getByDisplayValue('120')).toBeInTheDocument();
  });

  it('disables buttons when loading', async () => {
    mockActivityTrackApi.createActivity.mockImplementation(() => new Promise(() => {}));
    
    render(<ActivityTrackManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('登录')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加活动跟踪'));
    
    // Fill required fields
    await userEvent.selectOptions(screen.getByLabelText('用户:'), '1');
    await userEvent.type(screen.getByLabelText('活动类型:'), '购买');
    await userEvent.type(screen.getByLabelText('描述:'), '购买商品');
    
    // Submit form (this will trigger loading state)
    fireEvent.click(screen.getByText('创建'));
    
    // Check that buttons are disabled
    expect(screen.getByText('处理中...')).toBeInTheDocument();
    expect(screen.getByText('处理中...')).toBeDisabled();
  });
});