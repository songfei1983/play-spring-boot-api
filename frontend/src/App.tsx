import React, { useState, useEffect } from 'react';
import './App.css';
import UserManagement from './components/UserManagement';
import UserProfileManagement from './components/UserProfileManagement';
import ActivityTrackManagement from './components/ActivityTrackManagement';
import PurchaseHistoryManagement from './components/PurchaseHistoryManagement';
import CampaignManagement from './components/CampaignManagement';
import './components/CampaignManagement.css';

function App() {
  const [activeTab, setActiveTab] = useState(() => {
    // 从localStorage获取上次选中的标签页，默认为'users'
    return localStorage.getItem('activeTab') || 'users';
  });

  // 当activeTab改变时，保存到localStorage
  useEffect(() => {
    localStorage.setItem('activeTab', activeTab);
  }, [activeTab]);

  const renderActiveComponent = () => {
    switch (activeTab) {
      case 'users':
        return <UserManagement />;
      case 'profiles':
        return <UserProfileManagement />;
      case 'activities':
        return <ActivityTrackManagement />;
      case 'purchases':
        return <PurchaseHistoryManagement />;
      case 'campaigns':
        return <CampaignManagement />;
      default:
        return <UserManagement />;
    }
  };

  return (
    <div className="App">
      <header className="app-header">
        <h1>Spring Boot API 管理系统</h1>
        <nav className="nav-tabs">
          <button 
            className={`nav-tab ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            用户管理
          </button>
          <button 
            className={`nav-tab ${activeTab === 'profiles' ? 'active' : ''}`}
            onClick={() => setActiveTab('profiles')}
          >
            用户档案
          </button>
          <button 
            className={`nav-tab ${activeTab === 'activities' ? 'active' : ''}`}
            onClick={() => setActiveTab('activities')}
          >
            活动跟踪
          </button>
          <button 
            className={`nav-tab ${activeTab === 'purchases' ? 'active' : ''}`}
            onClick={() => setActiveTab('purchases')}
          >
            购买历史
          </button>
          <button 
            className={`nav-tab ${activeTab === 'campaigns' ? 'active' : ''}`}
            onClick={() => setActiveTab('campaigns')}
          >
            广告活动
          </button>
        </nav>
      </header>
      <main className="main-content">
        {renderActiveComponent()}
      </main>
      <footer className="app-footer">
        <p>API 文档: <a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noopener noreferrer">Swagger UI</a></p>
      </footer>
    </div>
  );
}

export default App;
