import React, { useState, useEffect } from 'react';
import { selectedUserApi as userApi, User } from '../services/api';

const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [formData, setFormData] = useState<User>({ name: '', email: '' });

  // 获取所有用户
  const fetchUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await userApi.getAllUsers();
      setUsers(response.data);
    } catch (err: any) {
      setError('获取用户列表失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 创建或更新用户
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    
    try {
      if (editingUser) {
        await userApi.updateUser(editingUser.id!, formData);
      } else {
        await userApi.createUser(formData);
      }
      
      setShowForm(false);
      setEditingUser(null);
      setFormData({ name: '', email: '' });
      await fetchUsers();
    } catch (err: any) {
      setError('操作失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 删除用户
  const handleDelete = async (id: number) => {
    if (!window.confirm('确定要删除这个用户吗？')) return;
    
    setLoading(true);
    setError(null);
    
    try {
      await userApi.deleteUser(id);
      await fetchUsers();
    } catch (err: any) {
      setError('删除用户失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  // 编辑用户
  const handleEdit = (user: User) => {
    setEditingUser(user);
    setFormData({ name: user.name, email: user.email });
    setShowForm(true);
  };

  // 取消编辑
  const handleCancel = () => {
    setShowForm(false);
    setEditingUser(null);
    setFormData({ name: '', email: '' });
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <div className="management-container">
      <div className="management-header">
        <h2>用户管理</h2>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
          disabled={loading}
        >
          添加用户
        </button>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {showForm && (
        <div className="form-container">
          <h3>{editingUser ? '编辑用户' : '添加用户'}</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="name">姓名:</label>
              <input
                type="text"
                id="name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="email">邮箱:</label>
              <input
                type="email"
                id="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                required
              />
            </div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? '处理中...' : (editingUser ? '更新' : '创建')}
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
          
          {!loading && users.length === 0 && (
            <div className="no-data">暂无用户数据</div>
          )}
          
          {users.length > 0 && (
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>姓名</th>
                  <th>邮箱</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id}>
                    <td>{user.id}</td>
                    <td>{user.name}</td>
                    <td>{user.email}</td>
                    <td className="actions-cell">
                      <div className="action-buttons">
                        <button 
                          className="btn btn-sm btn-primary"
                          onClick={() => handleEdit(user)}
                          disabled={loading}
                        >
                          编辑
                        </button>
                        <button 
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(user.id!)}
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

export default UserManagement;