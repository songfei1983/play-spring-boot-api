import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import PurchaseHistoryManagement from './PurchaseHistoryManagement';
import { purchaseHistoryApi } from '../services/api';

// Mock the API
jest.mock('../services/api', () => ({
  purchaseHistoryApi: {
    getAllPurchases: jest.fn(),
    createPurchase: jest.fn(),
    updatePurchase: jest.fn(),
    deletePurchase: jest.fn(),
    searchPurchases: jest.fn(),
  },
}));

// Mock window.confirm
Object.defineProperty(window, 'confirm', {
  writable: true,
  value: jest.fn(),
});

const mockPurchaseHistoryApi = purchaseHistoryApi as jest.Mocked<typeof purchaseHistoryApi>;
const mockConfirm = window.confirm as jest.MockedFunction<typeof window.confirm>;

const mockPurchases = [
  {
    id: 1,
    userId: 1,
    orderNumber: 'ORD001',
    productId: 1,
    productName: 'iPhone 15 Pro',
    category: '电子产品',
    brand: 'Apple',
    sku: 'IPH15P-256-BLU',
    quantity: 1,
    unitPrice: 8999,
    totalPrice: 8999,
    discountAmount: 500,
    actualPrice: 8499,
    paymentMethod: '支付宝',
    paymentStatus: '已支付',
    orderStatus: '已送达',
    deliveryAddress: '北京市朝阳区xxx街道xxx号',
    deliveryMethod: '快递',
    courierCompany: '顺丰速运',
    trackingNumber: 'SF1234567890',
    couponId: 'COUPON123',
    couponName: '新用户专享券',
    rating: 5,
    review: '商品质量很好，物流很快',
    channel: 'APP',
    salesPersonId: 1,
    remarks: '备注信息',
    purchaseTime: '2023-01-01T10:00:00Z',
    paymentTime: '2023-01-01T10:05:00Z',
    shipmentTime: '2023-01-01T14:00:00Z',
    completionTime: '2023-01-02T10:00:00Z',
  },
  {
    id: 2,
    userId: 2,
    orderNumber: 'ORD002',
    productId: 2,
    productName: 'MacBook Pro',
    category: '电子产品',
    brand: 'Apple',
    sku: 'MBP16-512-SLV',
    quantity: 1,
    unitPrice: 18999,
    totalPrice: 18999,
    discountAmount: 0,
    actualPrice: 18999,
    paymentMethod: '微信支付',
    paymentStatus: '已支付',
    orderStatus: '配送中',
    deliveryAddress: '上海市浦东新区xxx路xxx号',
    deliveryMethod: '快递',
    courierCompany: '京东物流',
    trackingNumber: 'JD9876543210',
    couponId: '',
    couponName: '',
    rating: 4,
    review: '性能很好',
    channel: '网站',
    salesPersonId: 2,
    remarks: '',
    purchaseTime: '2023-01-02T09:00:00Z',
    paymentTime: '2023-01-02T09:05:00Z',
    shipmentTime: '2023-01-02T15:00:00Z',
    completionTime: '',
  },
];

describe('PurchaseHistoryManagement', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockPurchaseHistoryApi.getAllPurchases.mockResolvedValue({ data: mockPurchases } as any);
  });

  it('renders purchase history management header', async () => {
    render(<PurchaseHistoryManagement />);
    
    expect(screen.getByText('购买历史管理')).toBeInTheDocument();
    expect(screen.getByText('添加购买记录')).toBeInTheDocument();
  });

  it('shows loading state initially', () => {
    mockPurchaseHistoryApi.getAllPurchases.mockImplementation(() => new Promise(() => {}));
    render(<PurchaseHistoryManagement />);
    
    expect(screen.getByText('加载中...')).toBeInTheDocument();
  });

  it('loads and displays purchases on mount', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(mockPurchaseHistoryApi.getAllPurchases).toHaveBeenCalledTimes(1);
    });
    
    expect(screen.getByText('ORD001')).toBeInTheDocument();
    expect(screen.getByText('iPhone 15 Pro')).toBeInTheDocument();
    expect(screen.getByText('ORD002')).toBeInTheDocument();
    expect(screen.getByText('MacBook Pro')).toBeInTheDocument();
  });

  it('shows error message when fetching purchases fails', async () => {
    const errorMessage = 'Network Error';
    mockPurchaseHistoryApi.getAllPurchases.mockRejectedValue(new Error(errorMessage));
    
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText(`获取购买历史列表失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('shows no data message when purchases list is empty', async () => {
    mockPurchaseHistoryApi.getAllPurchases.mockResolvedValue({ data: [] } as any);
    
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('暂无购买记录')).toBeInTheDocument();
    });
  });

  it('displays purchase data correctly in table', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Check various data fields
    expect(screen.getByText('电子产品')).toBeInTheDocument();
    expect(screen.getByText('Apple')).toBeInTheDocument();
    expect(screen.getByText('¥8999')).toBeInTheDocument();
    expect(screen.getByText('¥8499')).toBeInTheDocument();
    expect(screen.getByText('支付宝')).toBeInTheDocument();
    expect(screen.getByText('已送达')).toBeInTheDocument();
    expect(screen.getByText('APP')).toBeInTheDocument();
  });

  it('displays star ratings correctly', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Check 5-star rating (★★★★★)
    expect(screen.getByText('★★★★★')).toBeInTheDocument();
    // Check 4-star rating (★★★★☆)
    expect(screen.getByText('★★★★☆')).toBeInTheDocument();
  });

  it('formats purchase time correctly', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      // Check that timestamps are formatted (exact format may vary by locale)
      const timestampElements = screen.getAllByText(/2023/);
      expect(timestampElements.length).toBeGreaterThan(0);
    });
  });

  it('truncates long product names with ellipsis', async () => {
    const longProductPurchase = {
      ...mockPurchases[0],
      productName: 'This is a very long product name that should be truncated when displayed in the table because it exceeds the maximum width',
    };
    mockPurchaseHistoryApi.getAllPurchases.mockResolvedValue({ data: [longProductPurchase] } as any);
    
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      const productNameElement = screen.getByTitle('This is a very long product name that should be truncated when displayed in the table because it exceeds the maximum width');
      expect(productNameElement).toBeInTheDocument();
    });
  });

  it('opens add purchase form when add button is clicked', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    fireEvent.click(screen.getByText('添加购买记录'));
    
    expect(screen.getByText('添加购买记录')).toBeInTheDocument();
    expect(screen.getByLabelText('用户ID')).toBeInTheDocument();
    expect(screen.getByLabelText('订单号')).toBeInTheDocument();
    expect(screen.getByLabelText('商品名称')).toBeInTheDocument();
  });

  it('hides search container when form is shown', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Search container should be visible initially
    expect(screen.getByPlaceholderText('搜索订单号、商品名称...')).toBeInTheDocument();
    
    // Open form
    fireEvent.click(screen.getByText('添加购买记录'));
    
    // Search container should be hidden
    expect(screen.queryByPlaceholderText('搜索订单号、商品名称...')).not.toBeInTheDocument();
  });

  it('creates a new purchase successfully', async () => {
    mockPurchaseHistoryApi.createPurchase.mockResolvedValue({ data: { id: 3 } } as any);
    
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加购买记录'));
    
    // Fill required fields
    await userEvent.type(screen.getByLabelText('订单号'), 'ORD003');
    await userEvent.type(screen.getByLabelText('商品名称'), 'iPad Pro');
    await userEvent.type(screen.getByLabelText('单价'), '6999');
    await userEvent.type(screen.getByLabelText('总价'), '6999');
    
    // Submit form
    fireEvent.click(screen.getByText('创建'));
    
    await waitFor(() => {
      expect(mockPurchaseHistoryApi.createPurchase).toHaveBeenCalledWith(
        expect.objectContaining({
          orderNumber: 'ORD003',
          productName: 'iPad Pro',
          unitPrice: 6999,
          totalPrice: 6999,
        })
      );
      expect(mockPurchaseHistoryApi.getAllPurchases).toHaveBeenCalledTimes(2); // Initial load + after create
    });
  });

  it('shows error when creating purchase fails', async () => {
    const errorMessage = 'Validation failed';
    mockPurchaseHistoryApi.createPurchase.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加购买记录'));
    
    // Fill required fields
    await userEvent.type(screen.getByLabelText('订单号'), 'ORD003');
    await userEvent.type(screen.getByLabelText('商品名称'), 'iPad Pro');
    await userEvent.type(screen.getByLabelText('单价'), '6999');
    await userEvent.type(screen.getByLabelText('总价'), '6999');
    
    // Submit form
    fireEvent.click(screen.getByText('创建'));
    
    await waitFor(() => {
      expect(screen.getByText(`保存购买记录失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('opens edit form when edit button is clicked', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    const editButtons = screen.getAllByText('编辑');
    fireEvent.click(editButtons[0]);
    
    expect(screen.getByText('编辑购买记录')).toBeInTheDocument();
    expect(screen.getByDisplayValue('ORD001')).toBeInTheDocument();
    expect(screen.getByDisplayValue('iPhone 15 Pro')).toBeInTheDocument();
    expect(screen.getByDisplayValue('8999')).toBeInTheDocument();
  });

  it('updates purchase successfully', async () => {
    mockPurchaseHistoryApi.updatePurchase.mockResolvedValue({ data: { id: 1 } } as any);
    
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Click edit button
    const editButtons = screen.getAllByText('编辑');
    fireEvent.click(editButtons[0]);
    
    // Update product name
    const productNameInput = screen.getByDisplayValue('iPhone 15 Pro');
    await userEvent.clear(productNameInput);
    await userEvent.type(productNameInput, 'iPhone 15 Pro Max');
    
    // Submit form
    fireEvent.click(screen.getByText('更新'));
    
    await waitFor(() => {
      expect(mockPurchaseHistoryApi.updatePurchase).toHaveBeenCalledWith(
        1,
        expect.objectContaining({
          productName: 'iPhone 15 Pro Max',
        })
      );
      expect(mockPurchaseHistoryApi.getAllPurchases).toHaveBeenCalledTimes(2); // Initial load + after update
    });
  });

  it('cancels form editing', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Open add form
    fireEvent.click(screen.getByText('添加购买记录'));
    expect(screen.getByText('添加购买记录')).toBeInTheDocument();
    
    // Cancel form
    fireEvent.click(screen.getByText('取消'));
    expect(screen.queryByText('添加购买记录')).not.toBeInTheDocument();
    
    // Search container should be visible again
    expect(screen.getByPlaceholderText('搜索订单号、商品名称...')).toBeInTheDocument();
  });

  it('deletes purchase after confirmation', async () => {
    mockConfirm.mockReturnValue(true);
    mockPurchaseHistoryApi.deletePurchase.mockResolvedValue({} as any);
    
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    await waitFor(() => {
      expect(mockConfirm).toHaveBeenCalledWith('确定要删除这条购买记录吗？');
      expect(mockPurchaseHistoryApi.deletePurchase).toHaveBeenCalledWith(1);
      expect(mockPurchaseHistoryApi.getAllPurchases).toHaveBeenCalledTimes(2); // Initial load + after delete
    });
  });

  it('does not delete purchase when confirmation is cancelled', async () => {
    mockConfirm.mockReturnValue(false);
    
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    expect(mockConfirm).toHaveBeenCalledWith('确定要删除这条购买记录吗？');
    expect(mockPurchaseHistoryApi.deletePurchase).not.toHaveBeenCalled();
  });

  it('shows error when deleting purchase fails', async () => {
    mockConfirm.mockReturnValue(true);
    const errorMessage = 'Cannot delete purchase';
    mockPurchaseHistoryApi.deletePurchase.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    const deleteButtons = screen.getAllByText('删除');
    fireEvent.click(deleteButtons[0]);
    
    await waitFor(() => {
      expect(screen.getByText(`删除购买记录失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });

  it('handles form input changes correctly', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Open form
    fireEvent.click(screen.getByText('添加购买记录'));
    
    // Test various form inputs
    await userEvent.type(screen.getByLabelText('商品分类'), '电子产品');
    await userEvent.type(screen.getByLabelText('品牌'), 'Apple');
    await userEvent.type(screen.getByLabelText('SKU'), 'IPH15P-256-BLU');
    await userEvent.type(screen.getByLabelText('数量'), '2');
    await userEvent.type(screen.getByLabelText('优惠金额'), '500');
    await userEvent.type(screen.getByLabelText('实付金额'), '8499');
    await userEvent.selectOptions(screen.getByLabelText('支付方式'), '支付宝');
    await userEvent.selectOptions(screen.getByLabelText('支付状态'), '已支付');
    await userEvent.selectOptions(screen.getByLabelText('订单状态'), '已送达');
    await userEvent.type(screen.getByLabelText('收货地址'), '北京市朝阳区xxx街道xxx号');
    await userEvent.selectOptions(screen.getByLabelText('配送方式'), '快递');
    await userEvent.type(screen.getByLabelText('快递公司'), '顺丰速运');
    await userEvent.type(screen.getByLabelText('快递单号'), 'SF1234567890');
    await userEvent.type(screen.getByLabelText('优惠券ID'), 'COUPON123');
    await userEvent.type(screen.getByLabelText('优惠券名称'), '新用户专享券');
    await userEvent.selectOptions(screen.getByLabelText('评分'), '5');
    await userEvent.type(screen.getByLabelText('评价'), '商品质量很好');
    await userEvent.selectOptions(screen.getByLabelText('购买渠道'), 'APP');
    await userEvent.type(screen.getByLabelText('销售员ID'), '1');
    await userEvent.type(screen.getByLabelText('备注'), '备注信息');
    
    // Verify inputs are updated
    expect(screen.getByDisplayValue('电子产品')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Apple')).toBeInTheDocument();
    expect(screen.getByDisplayValue('IPH15P-256-BLU')).toBeInTheDocument();
    expect(screen.getByDisplayValue('2')).toBeInTheDocument();
    expect(screen.getByDisplayValue('500')).toBeInTheDocument();
    expect(screen.getByDisplayValue('8499')).toBeInTheDocument();
    expect(screen.getByDisplayValue('支付宝')).toBeInTheDocument();
    expect(screen.getByDisplayValue('已支付')).toBeInTheDocument();
    expect(screen.getByDisplayValue('已送达')).toBeInTheDocument();
    expect(screen.getByDisplayValue('北京市朝阳区xxx街道xxx号')).toBeInTheDocument();
    expect(screen.getByDisplayValue('快递')).toBeInTheDocument();
    expect(screen.getByDisplayValue('顺丰速运')).toBeInTheDocument();
    expect(screen.getByDisplayValue('SF1234567890')).toBeInTheDocument();
    expect(screen.getByDisplayValue('COUPON123')).toBeInTheDocument();
    expect(screen.getByDisplayValue('新用户专享券')).toBeInTheDocument();
    expect(screen.getByDisplayValue('5')).toBeInTheDocument();
    expect(screen.getByDisplayValue('商品质量很好')).toBeInTheDocument();
    expect(screen.getByDisplayValue('APP')).toBeInTheDocument();
    expect(screen.getByDisplayValue('1')).toBeInTheDocument();
    expect(screen.getByDisplayValue('备注信息')).toBeInTheDocument();
  });

  it('displays pagination information correctly', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    expect(screen.getByText('显示 1-2 条，共 2 条记录')).toBeInTheDocument();
    expect(screen.getByText('上一页')).toBeInTheDocument();
    expect(screen.getByText('下一页')).toBeInTheDocument();
  });

  it('applies correct CSS classes for order status', async () => {
    render(<PurchaseHistoryManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Check status styling
    const deliveredStatus = screen.getByText('已送达');
    expect(deliveredStatus).toHaveClass('status', 'status-success');
    
    const shippingStatus = screen.getByText('配送中');
    expect(shippingStatus).toHaveClass('status', 'status-info');
  });

  it('searches purchases by order number', async () => {
    const searchResults = [mockPurchases[0]];
    mockPurchaseHistoryApi.searchPurchases.mockResolvedValue({ data: searchResults } as any);
    
    render(<PurchaseHistoryManagement />);

    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Enter search term
    await userEvent.type(screen.getByPlaceholderText('搜索订单号、商品名称...'), 'ORD001');
    
    // Click search button
    fireEvent.click(screen.getByText('搜索'));
    
    await waitFor(() => {
      expect(mockPurchaseHistoryApi.searchPurchases).toHaveBeenCalledWith('ORD001');
    });
  });

  it('resets search and shows all purchases', async () => {
    render(<PurchaseHistoryManagement />);

    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Enter search term
    await userEvent.type(screen.getByPlaceholderText('搜索订单号、商品名称...'), 'ORD001');
    
    // Click reset button
    fireEvent.click(screen.getByText('重置'));
    
    expect(screen.getByPlaceholderText('搜索订单号、商品名称...')).toHaveValue('');
    await waitFor(() => {
      expect(mockPurchaseHistoryApi.getAllPurchases).toHaveBeenCalledTimes(2); // Initial load + after reset
    });
  });

  it('shows error when search fails', async () => {
    const errorMessage = 'Search failed';
    mockPurchaseHistoryApi.searchPurchases.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });
    
    render(<PurchaseHistoryManagement />);

    await waitFor(() => {
      expect(screen.getByText('ORD001')).toBeInTheDocument();
    });
    
    // Enter search term
    await userEvent.type(screen.getByPlaceholderText('搜索订单号、商品名称...'), 'ORD001');
    
    // Click search button
    fireEvent.click(screen.getByText('搜索'));
    
    await waitFor(() => {
      expect(screen.getByText(`搜索失败: ${errorMessage}`)).toBeInTheDocument();
    });
  });
});