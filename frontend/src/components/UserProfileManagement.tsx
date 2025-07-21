import React, { useState, useEffect } from 'react';
import { selectedUserProfileApi as userProfileApi, selectedUserApi as userApi, UserProfile, User } from '../services/api';

const UserProfileManagement: React.FC = () => {
  const [profiles, setProfiles] = useState<UserProfile[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingProfile, setEditingProfile] = useState<UserProfile | null>(null);
  const [searchAddress, setSearchAddress] = useState('');
  const [formData, setFormData] = useState<UserProfile>({
    userId: 0,
    age: undefined,
    gender: '',
    birthday: '',
    phoneNumber: '',
    address: '',
    occupation: '',
    bio: ''
  });

  // 获取所有用户档案
  const fetchProfiles = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await userProfileApi.getAllProfiles();
      setProfiles(response.data);
    } catch (err: any) {
      setError('获取用户档案列表失败: ' + (err.response?.data?.message || err.message));
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

  // 按地址搜索
  const handleSearch = async () => {
    if (!searchAddress.trim()) {
      await fetchProfiles();
      return;
    }
    
    setLoading(true);
    setError(null);
    try {
      const response = await userProfileApi.searchProfilesByAddress(searchAddress);
      setProfiles(response.data);
    } catch (err: any) {
      setError('搜索失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 创建或更新用户档案
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    
    try {
      if (editingProfile) {
        await userProfileApi.updateProfile(editingProfile.id!, formData);
      } else {
        await userProfileApi.createProfile(formData);
      }
      
      setShowForm(false);
      setEditingProfile(null);
      setFormData({ userId: 0, age: undefined, gender: '', birthday: '', phoneNumber: '', address: '', occupation: '', bio: '' });
      await fetchProfiles();
    } catch (err: any) {
      setError('操作失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 删除用户档案
  const handleDelete = async (id: number) => {
    if (!window.confirm('确定要删除这个用户档案吗？')) return;
    
    setLoading(true);
    setError(null);
    
    try {
      await userProfileApi.deleteProfile(id);
      await fetchProfiles();
    } catch (err: any) {
      setError('删除用户档案失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 编辑用户档案
  const handleEdit = (profile: UserProfile) => {
    setEditingProfile(profile);
    setFormData({
      userId: profile.userId,
      age: profile.age,
      gender: profile.gender || '',
      birthday: profile.birthday || '',
      phoneNumber: profile.phoneNumber || '',
      address: profile.address || '',
      occupation: profile.occupation || '',
      bio: profile.bio || ''
    });
    setShowForm(true);
  };

  // 取消编辑
  const handleCancel = () => {
    setShowForm(false);
    setEditingProfile(null);
    setFormData({ userId: 0, age: undefined, gender: '', birthday: '', phoneNumber: '', address: '', occupation: '', bio: '' });
  };

  // 获取用户名称
  const getUserName = (userId: number) => {
    const user = users.find(u => u.id === userId);
    return user ? user.name : `用户ID: ${userId}`;
  };

  useEffect(() => {
    fetchProfiles();
    fetchUsers();
  }, []);

  return (
    <div className="management-container">
      <div className="management-header">
        <h2>用户档案管理</h2>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
          disabled={loading}
        >
          添加用户档案
        </button>
      </div>

      {/* 搜索功能 */}
      <div className="search-container">
        <div className="search-group">
          <input
            type="text"
            placeholder="按地址搜索..."
            value={searchAddress}
            onChange={(e) => setSearchAddress(e.target.value)}
          />
          <button className="btn btn-secondary" onClick={handleSearch} disabled={loading}>
            搜索
          </button>
          <button className="btn btn-secondary" onClick={() => { setSearchAddress(''); fetchProfiles(); }}>
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
          <h3>{editingProfile ? '编辑用户档案' : '添加用户档案'}</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="userId">用户:</label>
              <select
                id="userId"
                value={formData.userId}
                onChange={(e) => setFormData({ ...formData, userId: parseInt(e.target.value) })}
                required
                disabled={!!editingProfile}
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
              <label htmlFor="age">年龄:</label>
              <input
                type="number"
                id="age"
                value={formData.age || ''}
                onChange={(e) => setFormData({ ...formData, age: e.target.value ? parseInt(e.target.value) : undefined })}
                min="1"
                max="120"
              />
            </div>
            <div className="form-group">
              <label htmlFor="gender">性别:</label>
              <select
                id="gender"
                value={formData.gender}
                onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
              >
                <option value="">请选择性别</option>
                <option value="男">男</option>
                <option value="女">女</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div className="form-group">
              <label htmlFor="birthday">生日:</label>
              <input
                type="date"
                id="birthday"
                value={formData.birthday}
                onChange={(e) => setFormData({ ...formData, birthday: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label htmlFor="address">地址:</label>
              <input
                type="text"
                id="address"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="phoneNumber">电话号码:</label>
              <input
                type="tel"
                id="phoneNumber"
                value={formData.phoneNumber}
                onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label htmlFor="occupation">职业:</label>
              <input
                type="text"
                id="occupation"
                value={formData.occupation}
                onChange={(e) => setFormData({ ...formData, occupation: e.target.value })}
                placeholder="例如：产品经理"
              />
            </div>
            <div className="form-group">
              <label htmlFor="bio">个人简介:</label>
              <textarea
                id="bio"
                value={formData.bio}
                onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                placeholder="例如：专注用户体验设计"
                rows={3}
              />
            </div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? '处理中...' : (editingProfile ? '更新' : '创建')}
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
          
          {!loading && profiles.length === 0 && (
            <div className="no-data">暂无用户档案数据</div>
          )}
          
          {profiles.length > 0 && (
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户</th>
                  <th>年龄</th>
                  <th>性别</th>
                  <th>生日</th>
                  <th>电话</th>
                  <th>地址</th>
                  <th>职业</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {profiles.map((profile) => (
                  <tr key={profile.id}>
                    <td>{profile.id}</td>
                    <td>{getUserName(profile.userId)}</td>
                    <td>{profile.age || '-'}</td>
                    <td>{profile.gender || '-'}</td>
                    <td>{profile.birthday || '-'}</td>
                    <td>{profile.phoneNumber || '-'}</td>
                    <td>{profile.address || '-'}</td>
                    <td>{profile.occupation || '-'}</td>
                    <td className="actions-cell">
                      <div className="action-buttons">
                        <button 
                          className="btn btn-sm btn-primary"
                          onClick={() => handleEdit(profile)}
                          disabled={loading}
                        >
                          编辑
                        </button>
                        <button 
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(profile.id!)}
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

export default UserProfileManagement;