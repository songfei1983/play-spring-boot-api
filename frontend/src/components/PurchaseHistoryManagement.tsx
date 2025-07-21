import React, { useState, useEffect } from 'react';
import { selectedPurchaseHistoryApi as purchaseHistoryApi, PurchaseHistory } from '../services/api';

const PurchaseHistoryManagement: React.FC = () => {
  const [purchases, setPurchases] = useState<PurchaseHistory[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState<PurchaseHistory>({
    userId: 1, // 设置默认用户ID
    orderNumber: '',
    productId: 1, // 设置默认商品ID
    productName: '',
    category: '',
    brand: '',
    sku: '',
    quantity: 1,
    unitPrice: 0,
    totalPrice: 0,
    discountAmount: 0,
    actualPrice: 0,
    paymentMethod: '',
    paymentStatus: '',
    orderStatus: '',
    deliveryAddress: '',
    deliveryMethod: '',
    courierCompany: '',
    trackingNumber: '',
    couponId: '',
    couponName: '',
    rating: 5,
    review: '',
    channel: '',
    salesPersonId: 0,
    remarks: '',
    purchaseTime: '',
    paymentTime: '',
    shipmentTime: '',
    completionTime: ''
  });

  const fetchPurchases = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await purchaseHistoryApi.getAllPurchases();
      setPurchases(response.data);
    } catch (err: any) {
      setError('获取购买历史列表失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPurchases();
  }, []);

  const handleEdit = (purchase: PurchaseHistory) => {
    setFormData({
      ...purchase,
      userId: purchase.userId || 0,
      orderNumber: purchase.orderNumber || '',
      productId: purchase.productId || 0,
      productName: purchase.productName || '',
      category: purchase.category || '',
      brand: purchase.brand || '',
      sku: purchase.sku || '',
      quantity: purchase.quantity || 1,
      unitPrice: purchase.unitPrice || 0,
      totalPrice: purchase.totalPrice || 0,
      discountAmount: purchase.discountAmount || 0,
      actualPrice: purchase.actualPrice || 0,
      paymentMethod: purchase.paymentMethod || '',
      paymentStatus: purchase.paymentStatus || '',
      orderStatus: purchase.orderStatus || '',
      deliveryAddress: purchase.deliveryAddress || '',
      deliveryMethod: purchase.deliveryMethod || '',
      courierCompany: purchase.courierCompany || '',
      trackingNumber: purchase.trackingNumber || '',
      couponId: purchase.couponId || '',
      couponName: purchase.couponName || '',
      rating: purchase.rating || 5,
      review: purchase.review || '',
      channel: purchase.channel || '',
      salesPersonId: purchase.salesPersonId || 0,
      remarks: purchase.remarks || '',
      purchaseTime: purchase.purchaseTime || '',
      paymentTime: purchase.paymentTime || '',
      shipmentTime: purchase.shipmentTime || '',
      completionTime: purchase.completionTime || ''
    });
    setEditingId(purchase.id || null);
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('确定要删除这条购买记录吗？')) {
      try {
        await purchaseHistoryApi.deletePurchase(id);
        fetchPurchases();
      } catch (err: any) {
        setError('删除购买记录失败: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      // 处理表单数据，将空字符串转换为undefined
      const processedData = {
        ...formData,
        paymentMethod: formData.paymentMethod || undefined,
        paymentStatus: formData.paymentStatus || undefined,
        orderStatus: formData.orderStatus || undefined,
        category: formData.category || undefined,
        brand: formData.brand || undefined,
        sku: formData.sku || undefined,
        deliveryAddress: formData.deliveryAddress || undefined,
        deliveryMethod: formData.deliveryMethod || undefined,
        courierCompany: formData.courierCompany || undefined,
        trackingNumber: formData.trackingNumber || undefined,
        couponId: formData.couponId || undefined,
        couponName: formData.couponName || undefined,
        review: formData.review || undefined,
        channel: formData.channel || undefined,
        remarks: formData.remarks || undefined,
        purchaseTime: formData.purchaseTime || undefined,
        paymentTime: formData.paymentTime || undefined,
        shipmentTime: formData.shipmentTime || undefined,
        completionTime: formData.completionTime || undefined
      };
      
      if (editingId) {
        await purchaseHistoryApi.updatePurchase(editingId, processedData);
      } else {
        await purchaseHistoryApi.createPurchase(processedData);
      }
      setShowForm(false);
      setEditingId(null);
      setFormData({
        userId: 1,
        orderNumber: '',
        productId: 1,
        productName: '',
        category: '',
        brand: '',
        sku: '',
        quantity: 1,
        unitPrice: 0,
        totalPrice: 0,
        discountAmount: 0,
        actualPrice: 0,
        paymentMethod: '',
        paymentStatus: '',
        orderStatus: '',
        deliveryAddress: '',
        deliveryMethod: '',
        courierCompany: '',
        trackingNumber: '',
        couponId: '',
        couponName: '',
        rating: 5,
        review: '',
        channel: '',
        salesPersonId: 0,
        remarks: '',
        purchaseTime: '',
        paymentTime: '',
        shipmentTime: '',
        completionTime: ''
      });
      fetchPurchases();
    } catch (err: any) {
      setError('保存购买记录失败: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingId(null);
    setFormData({
      userId: 1,
      orderNumber: '',
      productId: 1,
      productName: '',
      category: '',
      brand: '',
      sku: '',
      quantity: 1,
      unitPrice: 0,
      totalPrice: 0,
      discountAmount: 0,
      actualPrice: 0,
      paymentMethod: '',
      paymentStatus: '',
      orderStatus: '',
      deliveryAddress: '',
      deliveryMethod: '',
      courierCompany: '',
      trackingNumber: '',
      couponId: '',
      couponName: '',
      rating: 5,
      review: '',
      channel: '',
      salesPersonId: 0,
      remarks: '',
      purchaseTime: '',
      paymentTime: '',
      shipmentTime: '',
      completionTime: ''
    });
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'userId' || name === 'productId' || name === 'quantity' || name === 'unitPrice' || 
              name === 'totalPrice' || name === 'discountAmount' || name === 'actualPrice' || 
              name === 'rating' || name === 'salesPersonId' ? Number(value) : value
    }));
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div className="management-container">
      <div className="management-header">
        <h2>购买历史管理</h2>
        <button
          onClick={() => setShowForm(true)}
          className="btn btn-primary"
        >
          添加购买记录
        </button>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {!showForm && (
        <div className="search-container">
          <div className="search-group">
            <input
              type="text"
              placeholder="搜索订单号、商品名称..."
              style={{flex: 2}}
            />
            <select style={{flex: 1}}>
              <option value="">全部状态</option>
              <option value="待确认">待确认</option>
              <option value="已确认">已确认</option>
              <option value="配送中">配送中</option>
              <option value="已送达">已送达</option>
              <option value="已取消">已取消</option>
              <option value="已退货">已退货</option>
            </select>
            <select style={{flex: 1}}>
              <option value="">全部渠道</option>
              <option value="APP">APP</option>
              <option value="网站">网站</option>
              <option value="小程序">小程序</option>
              <option value="线下门店">线下门店</option>
            </select>
            <button className="btn btn-primary">搜索</button>
            <button className="btn btn-secondary">重置</button>
          </div>
        </div>
      )}

      {showForm && (
        <div className="form-container">
          <h3>
            {editingId ? '编辑购买记录' : '添加购买记录'}
          </h3>
          <form onSubmit={handleSubmit} className="form-grid">
            <div className="form-group">
              <label>用户ID</label>
              <input
                type="number"
                name="userId"
                value={formData.userId}
                onChange={handleInputChange}
                required
              />
            </div>
            
            <div className="form-group">
              <label>订单号</label>
              <input
                type="text"
                name="orderNumber"
                value={formData.orderNumber}
                onChange={handleInputChange}
                required
              />
            </div>
            
            <div className="form-group">
              <label>商品ID</label>
              <input
                type="number"
                name="productId"
                value={formData.productId}
                onChange={handleInputChange}
              />
            </div>
            
            <div className="form-group">
              <label>商品名称</label>
              <input
                type="text"
                name="productName"
                value={formData.productName}
                onChange={handleInputChange}
                required
              />
            </div>
            
            <div className="form-group">
              <label>商品分类</label>
              <input
                type="text"
                name="category"
                value={formData.category}
                onChange={handleInputChange}
                placeholder="如：电子产品"
              />
            </div>
            
            <div className="form-group">
              <label>品牌</label>
              <input
                type="text"
                name="brand"
                value={formData.brand}
                onChange={handleInputChange}
                placeholder="如：Apple"
              />
            </div>
            
            <div className="form-group">
              <label>SKU</label>
              <input
                type="text"
                name="sku"
                value={formData.sku}
                onChange={handleInputChange}
                placeholder="如：IPH15P-256-BLU"
              />
            </div>
            
            <div className="form-group">
              <label>数量</label>
              <input
                type="number"
                name="quantity"
                value={formData.quantity}
                onChange={handleInputChange}
                min="1"
                required
              />
            </div>
            
            <div className="form-group">
              <label>单价</label>
              <input
                type="number"
                name="unitPrice"
                value={formData.unitPrice}
                onChange={handleInputChange}
                step="0.01"
                min="0"
                required
              />
            </div>
            
            <div className="form-group">
              <label>总价</label>
              <input
                type="number"
                name="totalPrice"
                value={formData.totalPrice}
                onChange={handleInputChange}
                step="0.01"
                min="0"
                required
              />
            </div>
            
            <div className="form-group">
              <label>优惠金额</label>
              <input
                type="number"
                name="discountAmount"
                value={formData.discountAmount}
                onChange={handleInputChange}
                step="0.01"
                min="0"
              />
            </div>
            
            <div className="form-group">
              <label>实付金额</label>
              <input
                type="number"
                name="actualPrice"
                value={formData.actualPrice}
                onChange={handleInputChange}
                step="0.01"
                min="0"
              />
            </div>
            
            <div className="form-group">
              <label>支付方式</label>
              <select
                name="paymentMethod"
                value={formData.paymentMethod}
                onChange={handleInputChange}
              >
                <option value="">请选择支付方式</option>
                <option value="支付宝">支付宝</option>
                <option value="微信支付">微信支付</option>
                <option value="银行卡">银行卡</option>
                <option value="现金">现金</option>
                <option value="信用卡">信用卡</option>
              </select>
            </div>
            
            <div className="form-group">
              <label>支付状态</label>
              <select
                name="paymentStatus"
                value={formData.paymentStatus}
                onChange={handleInputChange}
              >
                <option value="">请选择支付状态</option>
                <option value="待支付">待支付</option>
                <option value="已支付">已支付</option>
                <option value="支付失败">支付失败</option>
                <option value="已退款">已退款</option>
              </select>
            </div>
            
            <div className="form-group">
              <label>订单状态</label>
              <select
                name="orderStatus"
                value={formData.orderStatus}
                onChange={handleInputChange}
              >
                <option value="">请选择订单状态</option>
                <option value="待确认">待确认</option>
                <option value="已确认">已确认</option>
                <option value="配送中">配送中</option>
                <option value="已送达">已送达</option>
                <option value="已取消">已取消</option>
                <option value="已退货">已退货</option>
              </select>
            </div>
            
            <div className="form-group full-width">
              <label>收货地址</label>
              <input
                type="text"
                name="deliveryAddress"
                value={formData.deliveryAddress}
                onChange={handleInputChange}
                placeholder="如：北京市朝阳区xxx街道xxx号"
              />
            </div>
            
            <div className="form-group">
              <label>配送方式</label>
              <select
                name="deliveryMethod"
                value={formData.deliveryMethod}
                onChange={handleInputChange}
              >
                <option value="">请选择配送方式</option>
                <option value="快递">快递</option>
                <option value="自提">自提</option>
                <option value="同城配送">同城配送</option>
              </select>
            </div>
            
            <div className="form-group">
              <label>快递公司</label>
              <input
                type="text"
                name="courierCompany"
                value={formData.courierCompany}
                onChange={handleInputChange}
                placeholder="如：顺丰速运"
              />
            </div>
            
            <div className="form-group">
              <label>快递单号</label>
              <input
                type="text"
                name="trackingNumber"
                value={formData.trackingNumber}
                onChange={handleInputChange}
                placeholder="如：SF1234567890"
              />
            </div>
            
            <div className="form-group">
              <label>优惠券ID</label>
              <input
                type="text"
                name="couponId"
                value={formData.couponId}
                onChange={handleInputChange}
                placeholder="如：COUPON123"
              />
            </div>
            
            <div className="form-group">
              <label>优惠券名称</label>
              <input
                type="text"
                name="couponName"
                value={formData.couponName}
                onChange={handleInputChange}
                placeholder="如：新用户专享券"
              />
            </div>
            
            <div className="form-group">
              <label>评分</label>
              <select
                name="rating"
                value={formData.rating}
                onChange={handleInputChange}
              >
                <option value={1}>1星</option>
                <option value={2}>2星</option>
                <option value={3}>3星</option>
                <option value={4}>4星</option>
                <option value={5}>5星</option>
              </select>
            </div>
            
            <div className="form-group full-width">
              <label>评价</label>
              <textarea
                name="review"
                value={formData.review}
                onChange={handleInputChange}
                rows={3}
                placeholder="商品质量很好，物流很快"
              />
            </div>
            
            <div className="form-group">
              <label>购买渠道</label>
              <select
                name="channel"
                value={formData.channel}
                onChange={handleInputChange}
              >
                <option value="">请选择购买渠道</option>
                <option value="APP">APP</option>
                <option value="网站">网站</option>
                <option value="小程序">小程序</option>
                <option value="线下门店">线下门店</option>
              </select>
            </div>
            
            <div className="form-group">
              <label>销售员ID</label>
              <input
                type="number"
                name="salesPersonId"
                value={formData.salesPersonId}
                onChange={handleInputChange}
              />
            </div>
            
            <div className="form-group full-width">
              <label>备注</label>
              <textarea
                name="remarks"
                value={formData.remarks}
                onChange={handleInputChange}
                rows={2}
                placeholder="其他备注信息"
              />
            </div>
            
            <div className="form-group">
              <label>购买时间</label>
              <input
                type="datetime-local"
                name="purchaseTime"
                value={formData.purchaseTime}
                onChange={handleInputChange}
              />
            </div>
            
            <div className="form-group">
              <label>支付时间</label>
              <input
                type="datetime-local"
                name="paymentTime"
                value={formData.paymentTime}
                onChange={handleInputChange}
              />
            </div>
            
            <div className="form-group">
              <label>发货时间</label>
              <input
                type="datetime-local"
                name="shipmentTime"
                value={formData.shipmentTime}
                onChange={handleInputChange}
              />
            </div>
            
            <div className="form-group">
              <label>完成时间</label>
              <input
                type="datetime-local"
                name="completionTime"
                value={formData.completionTime}
                onChange={handleInputChange}
              />
            </div>
            
            <div className="form-actions">
              <button
                type="submit"
                className="btn btn-primary"
              >
                {editingId ? '更新' : '创建'}
              </button>
              <button
                type="button"
                onClick={handleCancel}
                className="btn btn-secondary"
              >
                取消
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="table-container">
        <div style={{flex: 1, overflow: 'auto'}}>
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>用户ID</th>
                <th>订单号</th>
                <th>商品名称</th>
                <th>分类</th>
                <th>品牌</th>
                <th>数量</th>
                <th>单价</th>
                <th>总价</th>
                <th>实付金额</th>
                <th>支付方式</th>
                <th>订单状态</th>
                <th>渠道</th>
                <th>评分</th>
                <th>购买时间</th>
                <th>操作</th>
              </tr>
            </thead>
              <tbody>
                 {purchases.map((purchase) => (
                   <tr key={purchase.id}>
                     <td>{purchase.id}</td>
                     <td>{purchase.userId}</td>
                     <td>{purchase.orderNumber}</td>
                     <td>
                       <div style={{maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap'}} title={purchase.productName}>
                         {purchase.productName}
                       </div>
                     </td>
                     <td>{purchase.category}</td>
                     <td>{purchase.brand}</td>
                     <td>{purchase.quantity}</td>
                     <td>¥{purchase.unitPrice}</td>
                     <td>¥{purchase.totalPrice}</td>
                     <td>¥{purchase.actualPrice}</td>
                     <td>{purchase.paymentMethod}</td>
                     <td>
                       <span className={`status ${
                         purchase.orderStatus === '已送达' ? 'status-success' :
                         purchase.orderStatus === '配送中' ? 'status-info' :
                         purchase.orderStatus === '已确认' ? 'status-warning' :
                         purchase.orderStatus === '待确认' ? 'status-pending' :
                         'status-error'
                       }`}>
                         {purchase.orderStatus}
                       </span>
                     </td>
                     <td>{purchase.channel}</td>
                     <td>
                       {'★'.repeat(purchase.rating || 0)}{'☆'.repeat(5 - (purchase.rating || 0))}
                     </td>
                     <td>
                       {purchase.purchaseTime ? new Date(purchase.purchaseTime).toLocaleString() : '-'}
                     </td>
                     <td className="actions-cell">
                       <div className="action-buttons">
                         <button
                           onClick={() => handleEdit(purchase)}
                           className="btn btn-sm btn-primary"
                         >
                           编辑
                         </button>
                         <button
                           onClick={() => handleDelete(purchase.id!)}
                           className="btn btn-sm btn-danger"
                         >
                           删除
                         </button>
                       </div>
                     </td>
                   </tr>
                 ))}
              </tbody>
            </table>
            
            {purchases.length === 0 && (
              <div className="no-data">
                暂无购买记录
              </div>
            )}
          </div>
        
        {purchases.length > 0 && (
          <div className="pagination">
            <div className="pagination-info">
              显示 1-{Math.min(10, purchases.length)} 条，共 {purchases.length} 条记录
            </div>
            <button disabled>上一页</button>
            <button className="active">1</button>
            <button>2</button>
            <button>3</button>
            <button>下一页</button>
          </div>
        )}
      </div>
    </div>
  );
};

export default PurchaseHistoryManagement;