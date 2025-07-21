import React, { useState, useEffect } from 'react';
import { selectedActivityTrackApi as activityTrackApi, selectedUserApi as userApi, ActivityTrack, User } from '../services/api';

const ActivityTrackManagement: React.FC = () => {
  const [activities, setActivities] = useState<ActivityTrack[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingActivity, setEditingActivity] = useState<ActivityTrack | null>(null);
  const [searchUserId, setSearchUserId] = useState<number | null>(null);
  const [formData, setFormData] = useState<ActivityTrack>({
    userId: 0,
    activityType: '',
    description: '',
    longitude: undefined,
    latitude: undefined,
    location: '',
    ipAddress: '',
    deviceType: '',
    operatingSystem: '',
    browser: '',
    sessionId: '',
    pageUrl: '',
    duration: undefined,
    createdAt: new Date().toISOString().slice(0, 16) // 格式化为 datetime-local 输入格式
  });

  // 获取所有活动跟踪
  const fetchActivities = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await activityTrackApi.getAllActivities();
      setActivities(response.data);
    } catch (err: any) {
      setError('获取活动跟踪列表失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 获取所有用户（用于下拉选择）
  const fetchUsers = async () => {
    try {
      const response = await userApi.getAllUsers();
      setUsers(response.data);
    } catch (err: any) {
      console.error('获取用户列表失败:', err);
    }
  };

  // 按用户ID搜索
  const handleSearch = async () => {
    if (!searchUserId) {
      await fetchActivities();
      return;
    }
    
    setLoading(true);
    setError(null);
    try {
      const response = await activityTrackApi.getActivitiesByUserId(searchUserId);
      setActivities(response.data);
    } catch (err: any) {
      setError('搜索失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 创建或更新活动跟踪
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    
    try {
      // 转换时间格式为 ISO 字符串
      const submitData = {
        ...formData,
        createdAt: formData.createdAt ? new Date(formData.createdAt).toISOString() : undefined
      };
      
      if (editingActivity) {
        await activityTrackApi.updateActivity(editingActivity.id!, submitData);
      } else {
        await activityTrackApi.createActivity(submitData);
      }
      
      setShowForm(false);
      setEditingActivity(null);
      setFormData({
        userId: 0,
        activityType: '',
        description: '',
        longitude: undefined,
        latitude: undefined,
        location: '',
        ipAddress: '',
        deviceType: '',
        operatingSystem: '',
        browser: '',
        sessionId: '',
        pageUrl: '',
        duration: undefined,
        createdAt: new Date().toISOString().slice(0, 16)
      });
      await fetchActivities();
    } catch (err: any) {
      setError('操作失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 删除活动跟踪
  const handleDelete = async (id: number) => {
    if (!window.confirm('确定要删除这个活动跟踪吗？')) return;
    
    setLoading(true);
    setError(null);
    
    try {
      await activityTrackApi.deleteActivity(id);
      await fetchActivities();
    } catch (err: any) {
      setError('删除活动跟踪失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 编辑活动跟踪
  const handleEdit = (activity: ActivityTrack) => {
    setEditingActivity(activity);
    setFormData({
      userId: activity.userId,
      activityType: activity.activityType,
      description: activity.description,
      longitude: activity.longitude,
      latitude: activity.latitude,
      location: activity.location || '',
      ipAddress: activity.ipAddress || '',
      deviceType: activity.deviceType || '',
      operatingSystem: activity.operatingSystem || '',
      browser: activity.browser || '',
      sessionId: activity.sessionId || '',
      pageUrl: activity.pageUrl || '',
      duration: activity.duration,
      createdAt: activity.createdAt ? new Date(activity.createdAt).toISOString().slice(0, 16) : new Date().toISOString().slice(0, 16)
    });
    setShowForm(true);
  };

  // 取消编辑
  const handleCancel = () => {
    setShowForm(false);
    setEditingActivity(null);
    setFormData({
      userId: 0,
      activityType: '',
      description: '',
      longitude: undefined,
      latitude: undefined,
      location: '',
      ipAddress: '',
      deviceType: '',
      operatingSystem: '',
      browser: '',
      sessionId: '',
      pageUrl: '',
      duration: undefined,
      createdAt: new Date().toISOString().slice(0, 16)
    });
  };

  // 获取用户名称
  const getUserName = (userId: number) => {
    const user = users.find(u => u.id === userId);
    return user ? user.name : `用户ID: ${userId}`;
  };

  // 格式化时间显示
  const formatTimestamp = (createdAt: string) => {
    if (!createdAt) return '未设置';
    
    const date = new Date(createdAt);
    if (isNaN(date.getTime())) {
      return '时间格式错误';
    }
    
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  useEffect(() => {
    fetchActivities();
    fetchUsers();
  }, []);

  return (
    <div className="management-container">
      <div className="management-header">
        <h2>活动跟踪管理</h2>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
          disabled={loading}
        >
          添加活动跟踪
        </button>
      </div>

      {/* 搜索功能 */}
      <div className="search-container">
        <div className="search-group">
          <select
            value={searchUserId || ''}
            onChange={(e) => setSearchUserId(e.target.value ? parseInt(e.target.value) : null)}
          >
            <option value="">选择用户进行搜索...</option>
            {users.map((user) => (
              <option key={user.id} value={user.id}>
                {user.name} ({user.email})
              </option>
            ))}
          </select>
          <button className="btn btn-secondary" onClick={handleSearch} disabled={loading}>
            搜索
          </button>
          <button className="btn btn-secondary" onClick={() => { setSearchUserId(null); fetchActivities(); }}>
            重置
          </button>
        </div>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {showForm && (
        <div className="form-container">
          <h3>{editingActivity ? '编辑活动跟踪' : '添加活动跟踪'}</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="userId">用户:</label>
              <select
                id="userId"
                value={formData.userId}
                onChange={(e) => setFormData({ ...formData, userId: parseInt(e.target.value) })}
                required
              >
                <option value={0}>请选择用户</option>
                {users.map((user) => (
                  <option key={user.id} value={user.id}>
                    {user.name} ({user.email})
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label htmlFor="activityType">活动类型:</label>
              <input
                type="text"
                id="activityType"
                value={formData.activityType}
                onChange={(e) => setFormData({ ...formData, activityType: e.target.value })}
                placeholder="例如：登录、购买、浏览等"
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="description">描述:</label>
              <textarea
                id="description"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="活动详细描述"
                rows={3}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="longitude">经度:</label>
              <input
                type="number"
                id="longitude"
                value={formData.longitude || ''}
                onChange={(e) => setFormData({ ...formData, longitude: e.target.value ? parseFloat(e.target.value) : undefined })}
                step="0.000001"
                placeholder="例如：116.404"
              />
            </div>
            <div className="form-group">
              <label htmlFor="latitude">纬度:</label>
              <input
                type="number"
                id="latitude"
                value={formData.latitude || ''}
                onChange={(e) => setFormData({ ...formData, latitude: e.target.value ? parseFloat(e.target.value) : undefined })}
                step="0.000001"
                placeholder="例如：39.915"
              />
            </div>
            <div className="form-group">
              <label htmlFor="location">位置:</label>
              <input
                type="text"
                id="location"
                value={formData.location}
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                placeholder="例如：北京市朝阳区"
              />
            </div>
            <div className="form-group">
              <label htmlFor="ipAddress">IP地址:</label>
              <input
                type="text"
                id="ipAddress"
                value={formData.ipAddress}
                onChange={(e) => setFormData({ ...formData, ipAddress: e.target.value })}
                placeholder="例如：192.168.1.100"
              />
            </div>
            <div className="form-group">
              <label htmlFor="deviceType">设备类型:</label>
              <select
                id="deviceType"
                value={formData.deviceType}
                onChange={(e) => setFormData({ ...formData, deviceType: e.target.value })}
              >
                <option value="">请选择设备类型</option>
                <option value="手机">手机</option>
                <option value="电脑">电脑</option>
                <option value="平板">平板</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div className="form-group">
              <label htmlFor="operatingSystem">操作系统:</label>
              <input
                type="text"
                id="operatingSystem"
                value={formData.operatingSystem}
                onChange={(e) => setFormData({ ...formData, operatingSystem: e.target.value })}
                placeholder="例如：iOS 15.0"
              />
            </div>
            <div className="form-group">
              <label htmlFor="browser">浏览器:</label>
              <input
                type="text"
                id="browser"
                value={formData.browser}
                onChange={(e) => setFormData({ ...formData, browser: e.target.value })}
                placeholder="例如：Safari"
              />
            </div>
            <div className="form-group">
              <label htmlFor="sessionId">会话ID:</label>
              <input
                type="text"
                id="sessionId"
                value={formData.sessionId}
                onChange={(e) => setFormData({ ...formData, sessionId: e.target.value })}
                placeholder="例如：session_001"
              />
            </div>
            <div className="form-group">
              <label htmlFor="pageUrl">页面URL:</label>
              <input
                type="text"
                id="pageUrl"
                value={formData.pageUrl}
                onChange={(e) => setFormData({ ...formData, pageUrl: e.target.value })}
                placeholder="例如：/login"
              />
            </div>
            <div className="form-group">
              <label htmlFor="duration">持续时间(秒):</label>
              <input
                type="number"
                id="duration"
                value={formData.duration || ''}
                onChange={(e) => setFormData({ ...formData, duration: e.target.value ? parseInt(e.target.value) : undefined })}
                min="0"
                placeholder="例如：30"
              />
            </div>
            <div className="form-group">
              <label htmlFor="createdAt">时间:</label>
              <input
                type="datetime-local"
                id="createdAt"
                value={formData.createdAt || ''}
                onChange={(e) => setFormData({ ...formData, createdAt: e.target.value })}
                required
              />
            </div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? '处理中...' : (editingActivity ? '更新' : '创建')}
              </button>
              <button type="button" className="btn btn-secondary" onClick={handleCancel}>
                取消
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="table-container">
        <div style={{flex: 1, overflow: 'auto'}}>
          {loading && !showForm && <div className="loading">加载中...</div>}
          
          {!loading && activities.length === 0 && (
            <div className="no-data">暂无活动跟踪数据</div>
          )}
          
          {activities.length > 0 && (
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户</th>
                  <th>活动类型</th>
                  <th>描述</th>
                  <th>位置</th>
                  <th>设备</th>
                  <th>IP地址</th>
                  <th>持续时间</th>
                  <th>时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {activities.map((activity) => (
                  <tr key={activity.id}>
                    <td>{activity.id}</td>
                    <td>{getUserName(activity.userId)}</td>
                    <td>{activity.activityType}</td>
                    <td title={activity.description}>
                      {activity.description.length > 30 
                        ? activity.description.substring(0, 30) + '...' 
                        : activity.description
                      }
                    </td>
                    <td title={activity.location}>
                      {activity.location ? (
                        activity.location.length > 15 
                          ? activity.location.substring(0, 15) + '...' 
                          : activity.location
                      ) : '-'}
                    </td>
                    <td>{activity.deviceType || '-'}</td>
                    <td>{activity.ipAddress || '-'}</td>
                    <td>{activity.duration ? `${activity.duration}秒` : '-'}</td>
                    <td>{formatTimestamp(activity.createdAt || '')}</td>
                    <td className="actions-cell">
                      <div className="action-buttons">
                        <button 
                          className="btn btn-sm btn-primary"
                          onClick={() => handleEdit(activity)}
                          disabled={loading}
                        >
                          编辑
                        </button>
                        <button 
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(activity.id!)}
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
          )}
        </div>
      </div>
    </div>
  );
};

export default ActivityTrackManagement;