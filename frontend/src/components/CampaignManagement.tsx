import React, { useState, useEffect } from 'react';
import { selectedCampaignApi as campaignApi, Campaign } from '../services/api';
import { logger } from '../config/environment';

interface CampaignFormData {
  campaignId: string;
  advertiserId: string;
  name: string;
  status: string;
  budget?: {
    totalBudget?: number;
    dailyBudget?: number;
    currency?: string;
  };
  schedule?: {
    startDate?: string;
    endDate?: string;
    timezone?: string;
  };
  bidding?: {
    bidStrategy?: string;
    maxBid?: number;
    baseBid?: number;
  };
}

const CampaignManagement: React.FC = () => {
  const [campaigns, setCampaigns] = useState<Campaign[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingCampaign, setEditingCampaign] = useState<Campaign | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [statistics, setStatistics] = useState<{total: number, active: number, paused: number, completed: number} | null>(null);
  
  const [formData, setFormData] = useState<CampaignFormData>({
    campaignId: '',
    advertiserId: '',
    name: '',
    status: 'active',
    budget: {
      totalBudget: 0,
      dailyBudget: 0,
      currency: 'USD'
    },
    schedule: {
      startDate: '',
      endDate: '',
      timezone: 'UTC'
    },
    bidding: {
      bidStrategy: 'cpm',
      maxBid: 0,
      baseBid: 0
    }
  });

  // 获取广告活动列表
  const fetchCampaigns = async (page: number = 0) => {
    setLoading(true);
    setError(null);
    try {
      const response = await campaignApi.getAllCampaigns(page, 10);
      setCampaigns(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
      setCurrentPage(page);
      logger.info('获取广告活动列表成功', response.data);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || '获取广告活动列表失败';
      setError(errorMessage);
      logger.error('获取广告活动列表失败:', err);
    } finally {
      setLoading(false);
    }
  };

  // 获取统计信息
  const fetchStatistics = async () => {
    try {
      const response = await campaignApi.getCampaignStatistics();
      setStatistics(response.data);
      logger.info('获取统计信息成功', response.data);
    } catch (err: any) {
      logger.error('获取统计信息失败:', err);
    }
  };

  // 搜索广告活动
  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      fetchCampaigns(0);
      return;
    }
    
    setLoading(true);
    setError(null);
    try {
      const response = await campaignApi.searchCampaigns(searchQuery);
      setCampaigns(response.data);
      setTotalPages(1);
      setTotalElements(response.data.length);
      setCurrentPage(0);
      logger.info('搜索广告活动成功', response.data);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || '搜索广告活动失败';
      setError(errorMessage);
      logger.error('搜索广告活动失败:', err);
    } finally {
      setLoading(false);
    }
  };

  // 按状态筛选
  const handleStatusFilter = async (status: string) => {
    setStatusFilter(status);
    if (status === 'all') {
      fetchCampaigns(0);
      return;
    }
    
    setLoading(true);
    setError(null);
    try {
      const response = await campaignApi.getCampaignsByStatus(status);
      setCampaigns(response.data);
      setTotalPages(1);
      setTotalElements(response.data.length);
      setCurrentPage(0);
      logger.info(`按状态筛选广告活动成功: ${status}`, response.data);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || '筛选广告活动失败';
      setError(errorMessage);
      logger.error('筛选广告活动失败:', err);
    } finally {
      setLoading(false);
    }
  };

  // 创建或更新广告活动
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    
    try {
      const campaignData: Campaign = {
        ...formData,
        schedule: {
          ...formData.schedule,
          startDate: formData.schedule?.startDate ? new Date(formData.schedule.startDate).toISOString() : undefined,
          endDate: formData.schedule?.endDate ? new Date(formData.schedule.endDate).toISOString() : undefined
        }
      };
      
      if (editingCampaign) {
        await campaignApi.updateCampaign(editingCampaign.campaignId, campaignData);
        logger.info('更新广告活动成功', campaignData);
      } else {
        await campaignApi.createCampaign(campaignData);
        logger.info('创建广告活动成功', campaignData);
      }
      
      setShowForm(false);
      setEditingCampaign(null);
      resetForm();
      fetchCampaigns(currentPage);
      fetchStatistics();
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || '保存广告活动失败';
      setError(errorMessage);
      logger.error('保存广告活动失败:', err);
    } finally {
      setLoading(false);
    }
  };

  // 删除广告活动
  const handleDelete = async (campaignId: string) => {
    if (!window.confirm('确定要删除这个广告活动吗？')) {
      return;
    }
    
    setLoading(true);
    setError(null);
    try {
      await campaignApi.deleteCampaign(campaignId);
      logger.info('删除广告活动成功', campaignId);
      fetchCampaigns(currentPage);
      fetchStatistics();
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || '删除广告活动失败';
      setError(errorMessage);
      logger.error('删除广告活动失败:', err);
    } finally {
      setLoading(false);
    }
  };

  // 更新广告活动状态
  const handleStatusUpdate = async (campaignId: string, newStatus: string) => {
    setLoading(true);
    setError(null);
    try {
      await campaignApi.updateCampaignStatus(campaignId, newStatus);
      logger.info('更新广告活动状态成功', { campaignId, newStatus });
      fetchCampaigns(currentPage);
      fetchStatistics();
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || '更新状态失败';
      setError(errorMessage);
      logger.error('更新状态失败:', err);
    } finally {
      setLoading(false);
    }
  };

  // 编辑广告活动
  const handleEdit = (campaign: Campaign) => {
    setEditingCampaign(campaign);
    setFormData({
      campaignId: campaign.campaignId,
      advertiserId: campaign.advertiserId,
      name: campaign.name,
      status: campaign.status,
      budget: {
        totalBudget: campaign.budget?.totalBudget || 0,
        dailyBudget: campaign.budget?.dailyBudget || 0,
        currency: campaign.budget?.currency || 'USD'
      },
      schedule: {
        startDate: campaign.schedule?.startDate ? new Date(campaign.schedule.startDate).toISOString().split('T')[0] : '',
        endDate: campaign.schedule?.endDate ? new Date(campaign.schedule.endDate).toISOString().split('T')[0] : '',
        timezone: campaign.schedule?.timezone || 'UTC'
      },
      bidding: {
        bidStrategy: campaign.bidding?.bidStrategy || 'cpm',
        maxBid: campaign.bidding?.maxBid || 0,
        baseBid: campaign.bidding?.baseBid || 0
      }
    });
    setShowForm(true);
  };

  // 重置表单
  const resetForm = () => {
    setFormData({
      campaignId: '',
      advertiserId: '',
      name: '',
      status: 'active',
      budget: {
        totalBudget: 0,
        dailyBudget: 0,
        currency: 'USD'
      },
      schedule: {
        startDate: '',
        endDate: '',
        timezone: 'UTC'
      },
      bidding: {
        bidStrategy: 'cpm',
        maxBid: 0,
        baseBid: 0
      }
    });
  };

  // 取消编辑
  const handleCancel = () => {
    setShowForm(false);
    setEditingCampaign(null);
    resetForm();
  };

  // 格式化日期
  const formatDate = (dateString?: string) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString('zh-CN');
  };

  // 格式化金额
  const formatCurrency = (amount?: number, currency?: string) => {
    if (amount === undefined || amount === null) return '-';
    return `${currency || 'USD'} ${amount.toLocaleString()}`;
  };

  // 获取状态显示样式
  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'active': return 'status-badge status-active';
      case 'paused': return 'status-badge status-paused';
      case 'completed': return 'status-badge status-completed';
      default: return 'status-badge';
    }
  };

  // 获取状态中文名称
  const getStatusText = (status: string) => {
    switch (status) {
      case 'active': return '活跃';
      case 'paused': return '暂停';
      case 'completed': return '完成';
      default: return status;
    }
  };

  useEffect(() => {
    fetchCampaigns(0);
    fetchStatistics();
  }, []);

  return (
    <div className="campaign-management">
      <div className="management-header">
        <h2>广告活动管理</h2>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
          disabled={loading}
        >
          创建广告活动
        </button>
      </div>

      {/* 统计信息 */}
      {statistics && (
        <div className="statistics-cards">
          <div className="stat-card">
            <h3>总数</h3>
            <p className="stat-number">{statistics.total}</p>
          </div>
          <div className="stat-card">
            <h3>活跃</h3>
            <p className="stat-number stat-active">{statistics.active}</p>
          </div>
          <div className="stat-card">
            <h3>暂停</h3>
            <p className="stat-number stat-paused">{statistics.paused}</p>
          </div>
          <div className="stat-card">
            <h3>完成</h3>
            <p className="stat-number stat-completed">{statistics.completed}</p>
          </div>
        </div>
      )}

      {/* 搜索和筛选 */}
      <div className="search-filters">
        <div className="search-box">
          <input
            type="text"
            placeholder="搜索广告活动名称..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <button onClick={handleSearch} disabled={loading}>
            搜索
          </button>
        </div>
        
        <div className="status-filters">
          <button 
            className={`filter-btn ${statusFilter === 'all' ? 'active' : ''}`}
            onClick={() => handleStatusFilter('all')}
          >
            全部
          </button>
          <button 
            className={`filter-btn ${statusFilter === 'active' ? 'active' : ''}`}
            onClick={() => handleStatusFilter('active')}
          >
            活跃
          </button>
          <button 
            className={`filter-btn ${statusFilter === 'paused' ? 'active' : ''}`}
            onClick={() => handleStatusFilter('paused')}
          >
            暂停
          </button>
          <button 
            className={`filter-btn ${statusFilter === 'completed' ? 'active' : ''}`}
            onClick={() => handleStatusFilter('completed')}
          >
            完成
          </button>
        </div>
      </div>

      {error && (
        <div className="error-message">
          <p>错误: {error}</p>
        </div>
      )}

      {loading && (
        <div className="loading">
          <p>加载中...</p>
        </div>
      )}

      {/* 广告活动列表 */}
      <div className="campaigns-table">
        <table>
          <thead>
            <tr>
              <th>活动ID</th>
              <th>活动名称</th>
              <th>广告主ID</th>
              <th>状态</th>
              <th>总预算</th>
              <th>日预算</th>
              <th>竞价策略</th>
              <th>开始时间</th>
              <th>结束时间</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {campaigns.map((campaign) => (
              <tr key={campaign.id || campaign.campaignId}>
                <td>{campaign.campaignId}</td>
                <td>{campaign.name}</td>
                <td>{campaign.advertiserId}</td>
                <td>
                  <span className={getStatusBadgeClass(campaign.status)}>
                    {getStatusText(campaign.status)}
                  </span>
                </td>
                <td>{formatCurrency(campaign.budget?.totalBudget, campaign.budget?.currency)}</td>
                <td>{formatCurrency(campaign.budget?.dailyBudget, campaign.budget?.currency)}</td>
                <td>{campaign.bidding?.bidStrategy?.toUpperCase() || '-'}</td>
                <td>{formatDate(campaign.schedule?.startDate)}</td>
                <td>{formatDate(campaign.schedule?.endDate)}</td>
                <td>{formatDate(campaign.createdAt)}</td>
                <td>
                  <div className="action-buttons">
                    <button 
                      className="btn btn-sm btn-secondary"
                      onClick={() => handleEdit(campaign)}
                      disabled={loading}
                    >
                      编辑
                    </button>
                    {campaign.status === 'active' ? (
                      <button 
                        className="btn btn-sm btn-warning"
                        onClick={() => handleStatusUpdate(campaign.campaignId, 'paused')}
                        disabled={loading}
                      >
                        暂停
                      </button>
                    ) : campaign.status === 'paused' ? (
                      <button 
                        className="btn btn-sm btn-success"
                        onClick={() => handleStatusUpdate(campaign.campaignId, 'active')}
                        disabled={loading}
                      >
                        激活
                      </button>
                    ) : null}
                    <button 
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(campaign.campaignId)}
                      disabled={loading}
                    >
                      删除
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
        {campaigns.length === 0 && !loading && (
          <div className="no-data">
            <p>暂无广告活动数据</p>
          </div>
        )}
      </div>

      {/* 分页 */}
      {totalPages > 1 && (
        <div className="pagination">
          <button 
            onClick={() => fetchCampaigns(currentPage - 1)}
            disabled={currentPage === 0 || loading}
          >
            上一页
          </button>
          <span>第 {currentPage + 1} 页，共 {totalPages} 页 (总计 {totalElements} 条)</span>
          <button 
            onClick={() => fetchCampaigns(currentPage + 1)}
            disabled={currentPage >= totalPages - 1 || loading}
          >
            下一页
          </button>
        </div>
      )}

      {/* 创建/编辑表单 */}
      {showForm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3>{editingCampaign ? '编辑广告活动' : '创建广告活动'}</h3>
              <button className="close-btn" onClick={handleCancel}>×</button>
            </div>
            
            <form onSubmit={handleSubmit} className="campaign-form">
              <div className="form-section">
                <h4>基本信息</h4>
                <div className="form-row">
                  <div className="form-group">
                    <label>活动ID *</label>
                    <input
                      type="text"
                      name="campaignId"
                      value={formData.campaignId}
                      onChange={(e) => setFormData({...formData, campaignId: e.target.value})}
                      required
                      disabled={!!editingCampaign}
                    />
                  </div>
                  <div className="form-group">
                    <label>广告主ID *</label>
                    <input
                      type="text"
                      name="advertiserId"
                      value={formData.advertiserId}
                      onChange={(e) => setFormData({...formData, advertiserId: e.target.value})}
                      required
                    />
                  </div>
                </div>
                
                <div className="form-row">
                  <div className="form-group">
                    <label>活动名称 *</label>
                    <input
                      type="text"
                      name="name"
                      value={formData.name}
                      onChange={(e) => setFormData({...formData, name: e.target.value})}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>状态</label>
                    <select
                      name="status"
                      value={formData.status}
                      onChange={(e) => setFormData({...formData, status: e.target.value})}
                    >
                      <option value="active">活跃</option>
                      <option value="paused">暂停</option>
                      <option value="completed">完成</option>
                    </select>
                  </div>
                </div>
              </div>

              <div className="form-section">
                <h4>预算设置</h4>
                <div className="form-row">
                  <div className="form-group">
                    <label>总预算</label>
                    <input
                      type="number"
                      name="totalBudget"
                      step="0.01"
                      value={formData.budget?.totalBudget || ''}
                      onChange={(e) => setFormData({
                        ...formData, 
                        budget: {...formData.budget, totalBudget: parseFloat(e.target.value) || 0}
                      })}
                    />
                  </div>
                  <div className="form-group">
                    <label>日预算</label>
                    <input
                      type="number"
                      name="dailyBudget"
                      step="0.01"
                      value={formData.budget?.dailyBudget || ''}
                      onChange={(e) => setFormData({
                        ...formData, 
                        budget: {...formData.budget, dailyBudget: parseFloat(e.target.value) || 0}
                      })}
                    />
                  </div>
                  <div className="form-group">
                    <label>货币</label>
                    <select
                      name="currency"
                      value={formData.budget?.currency || 'USD'}
                      onChange={(e) => setFormData({
                        ...formData, 
                        budget: {...formData.budget, currency: e.target.value}
                      })}
                    >
                      <option value="USD">USD</option>
                      <option value="CNY">CNY</option>
                      <option value="EUR">EUR</option>
                    </select>
                  </div>
                </div>
              </div>

              <div className="form-section">
                <h4>竞价设置</h4>
                <div className="form-row">
                  <div className="form-group">
                    <label>竞价策略</label>
                    <select
                      name="bidStrategy"
                      value={formData.bidding?.bidStrategy || 'cpm'}
                      onChange={(e) => setFormData({
                        ...formData, 
                        bidding: {...formData.bidding, bidStrategy: e.target.value}
                      })}
                    >
                      <option value="cpm">CPM</option>
                      <option value="cpc">CPC</option>
                      <option value="cpa">CPA</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label>最高出价</label>
                    <input
                      type="number"
                      name="maxBid"
                      step="0.01"
                      value={formData.bidding?.maxBid || ''}
                      onChange={(e) => setFormData({
                        ...formData, 
                        bidding: {...formData.bidding, maxBid: parseFloat(e.target.value) || 0}
                      })}
                    />
                  </div>
                  <div className="form-group">
                    <label>基础出价</label>
                    <input
                      type="number"
                      name="baseBid"
                      step="0.01"
                      value={formData.bidding?.baseBid || ''}
                      onChange={(e) => setFormData({
                        ...formData, 
                        bidding: {...formData.bidding, baseBid: parseFloat(e.target.value) || 0}
                      })}
                    />
                  </div>
                </div>
              </div>

              <div className="form-section">
                <h4>时间安排</h4>
                <div className="form-row">
                  <div className="form-group">
                    <label>开始日期</label>
                    <input
                      type="date"
                      name="startDate"
                      value={formData.schedule?.startDate || ''}
                      onChange={(e) => setFormData({
                        ...formData, 
                        schedule: {...formData.schedule, startDate: e.target.value}
                      })}
                    />
                  </div>
                  <div className="form-group">
                    <label>结束日期</label>
                    <input
                      type="date"
                      name="endDate"
                      value={formData.schedule?.endDate || ''}
                      onChange={(e) => setFormData({
                        ...formData, 
                        schedule: {...formData.schedule, endDate: e.target.value}
                      })}
                    />
                  </div>
                  <div className="form-group">
                    <label>时区</label>
                    <select
                      name="timezone"
                      value={formData.schedule?.timezone || 'UTC'}
                      onChange={(e) => setFormData({
                        ...formData, 
                        schedule: {...formData.schedule, timezone: e.target.value}
                      })}
                    >
                      <option value="UTC">UTC</option>
                      <option value="Asia/Shanghai">Asia/Shanghai</option>
                      <option value="America/New_York">America/New_York</option>
                      <option value="Europe/London">Europe/London</option>
                    </select>
                  </div>
                </div>
              </div>

              <div className="form-actions">
                <button type="button" onClick={handleCancel} disabled={loading}>
                  取消
                </button>
                <button type="submit" disabled={loading}>
                  {loading ? '保存中...' : (editingCampaign ? '更新' : '创建')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default CampaignManagement;