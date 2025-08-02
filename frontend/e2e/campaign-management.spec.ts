import { test, expect } from '@playwright/test';

test.describe('Campaign Management', () => {
  test.beforeEach(async ({ page }) => {
    // 访问应用首页
    await page.goto('http://localhost:3000');
    
    // 等待页面加载完成
    await page.waitForLoadState('networkidle');
    
    // 点击广告活动标签页
    await page.click('button:has-text("广告活动")');
    
    // 等待广告活动页面加载
    await page.waitForSelector('.campaign-management');
  });

  test('应该能够创建新的广告活动', async ({ page }) => {
    // 点击创建广告活动按钮
    await page.click('button:has-text("创建广告活动")');
    
    // 等待模态框出现
    await page.waitForSelector('.modal-overlay');
    
    // 填写基本信息
    await page.fill('input[name="campaignId"]', 'CAMP001');
    await page.fill('input[name="name"]', '测试广告活动');
    await page.fill('input[name="advertiserId"]', 'ADV001');
    await page.selectOption('select[name="status"]', 'active');
    
    // 填写预算信息
    await page.fill('input[name="totalBudget"]', '10000');
    await page.fill('input[name="dailyBudget"]', '1000');
    await page.selectOption('select[name="currency"]', 'USD');
    
    // 填写竞价信息
    await page.selectOption('select[name="bidStrategy"]', 'cpc');
    await page.fill('input[name="maxBid"]', '0.5');
    await page.fill('input[name="baseBid"]', '0.3');
    
    // 填写时间安排
    const startDate = new Date();
    const endDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000); // 7天后
    
    await page.fill('input[name="startDate"]', startDate.toISOString().slice(0, 10));
    await page.fill('input[name="endDate"]', endDate.toISOString().slice(0, 10));
    await page.selectOption('select[name="timezone"]', 'UTC');
    
    // 提交表单
    await page.click('button[type="submit"]');
    
    // 等待模态框关闭
    await page.waitForSelector('.modal-overlay', { state: 'detached' });
    
    // 验证新创建的广告活动出现在列表中
    await expect(page.locator('text=测试广告活动')).toBeVisible();
    await expect(page.locator('text=ADV001')).toBeVisible();
    await expect(page.locator('.status-active')).toBeVisible();
  });

  test('应该能够搜索广告活动', async ({ page }) => {
    // 先创建一个广告活动用于搜索测试
    await page.click('button:has-text("创建广告活动")');
    await page.waitForSelector('.modal-overlay');
    
    await page.fill('input[name="campaignId"]', 'SEARCH001');
    await page.fill('input[name="name"]', '搜索测试广告活动');
    await page.fill('input[name="advertiserId"]', 'ADV002');
    await page.selectOption('select[name="status"]', 'active');
    await page.fill('input[name="totalBudget"]', '5000');
    await page.fill('input[name="dailyBudget"]', '500');
    await page.selectOption('select[name="currency"]', 'USD');
    await page.selectOption('select[name="bidStrategy"]', 'cpm');
    await page.fill('input[name="maxBid"]', '2.0');
    await page.fill('input[name="baseBid"]', '1.5');
    
    const startDate = new Date();
    const endDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
    await page.fill('input[name="startDate"]', startDate.toISOString().slice(0, 10));
    await page.fill('input[name="endDate"]', endDate.toISOString().slice(0, 10));
    await page.selectOption('select[name="timezone"]', 'UTC');
    
    await page.click('button[type="submit"]');
    await page.waitForSelector('.modal-overlay', { state: 'detached' });
    
    // 测试搜索功能
    await page.fill('input[placeholder*="搜索"]', '搜索测试');
    await page.click('button:has-text("搜索")');
    
    // 验证搜索结果
    await expect(page.locator('text=搜索测试广告活动')).toBeVisible();
  });

  test('应该能够按状态过滤广告活动', async ({ page }) => {
    // 创建不同状态的广告活动
    const campaigns = [
      { name: '活跃广告活动', status: 'active' },
      { name: '暂停广告活动', status: 'paused' },
      { name: '完成广告活动', status: 'completed' }
    ];
    
    for (const campaign of campaigns) {
      await page.click('button:has-text("创建广告活动")');
      await page.waitForSelector('.modal-overlay');
      
      await page.fill('input[name="campaignId"]', `CAMP${Math.random().toString(36).substr(2, 6)}`);
      await page.fill('input[name="name"]', campaign.name);
      await page.fill('input[name="advertiserId"]', `ADV${Math.random().toString(36).substr(2, 6)}`);
      await page.selectOption('select[name="status"]', campaign.status);
      await page.fill('input[name="totalBudget"]', '10000');
      await page.fill('input[name="dailyBudget"]', '1000');
      await page.selectOption('select[name="currency"]', 'USD');
      await page.selectOption('select[name="bidStrategy"]', 'cpc');
      await page.fill('input[name="maxBid"]', '1.0');
      await page.fill('input[name="baseBid"]', '0.8');
      
      const startDate = new Date();
      const endDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
      await page.fill('input[name="startDate"]', startDate.toISOString().slice(0, 10));
      await page.fill('input[name="endDate"]', endDate.toISOString().slice(0, 10));
      await page.selectOption('select[name="timezone"]', 'UTC');
      
      await page.click('button[type="submit"]');
      await page.waitForSelector('.modal-overlay', { state: 'detached' });
      
      // 等待一下避免操作过快
      await page.waitForTimeout(500);
    }
    
    // 测试状态过滤
    await page.click('.filter-btn:has-text("活跃")');
    await expect(page.locator('text=活跃广告活动')).toBeVisible();
    
    await page.click('.filter-btn:has-text("暂停")');
    await expect(page.locator('text=暂停广告活动')).toBeVisible();
    
    await page.click('.filter-btn:has-text("完成")');
    await expect(page.locator('text=完成广告活动')).toBeVisible();
  });

  test('应该能够编辑广告活动', async ({ page }) => {
    // 先创建一个广告活动
    await page.click('button:has-text("创建广告活动")');
    await page.waitForSelector('.modal-overlay');
    
    await page.fill('input[name="campaignId"]', 'EDIT001');
    await page.fill('input[name="name"]', '待编辑广告活动');
    await page.fill('input[name="advertiserId"]', 'ADV003');
    await page.selectOption('select[name="status"]', 'active');
    await page.fill('input[name="totalBudget"]', '8000');
    await page.fill('input[name="dailyBudget"]', '800');
    await page.selectOption('select[name="currency"]', 'USD');
    await page.selectOption('select[name="bidStrategy"]', 'cpc');
    await page.fill('input[name="maxBid"]', '0.8');
    await page.fill('input[name="baseBid"]', '0.6');
    
    const startDate = new Date();
    const endDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
    await page.fill('input[name="startDate"]', startDate.toISOString().slice(0, 10));
    await page.fill('input[name="endDate"]', endDate.toISOString().slice(0, 10));
    await page.selectOption('select[name="timezone"]', 'UTC');
    
    await page.click('button[type="submit"]');
    await page.waitForSelector('.modal-overlay', { state: 'detached' });
    
    // 点击编辑按钮
    await page.click('tr:has-text("待编辑广告活动") .btn-secondary:has-text("编辑")');
    await page.waitForSelector('.modal-overlay');
    
    // 修改广告活动名称
    await page.fill('input[name="name"]', '已编辑广告活动');
    await page.fill('input[name="dailyBudget"]', '1200');
    
    // 保存修改
    await page.click('button[type="submit"]');
    await page.waitForSelector('.modal-overlay', { state: 'detached' });
    
    // 验证修改结果
    await expect(page.locator('text=已编辑广告活动')).toBeVisible();
    await expect(page.locator('text=待编辑广告活动')).not.toBeVisible();
  });

  test('应该能够更新广告活动状态', async ({ page }) => {
    // 先创建一个活跃的广告活动
    await page.click('button:has-text("创建广告活动")');
    await page.waitForSelector('.modal-overlay');
    
    await page.fill('input[name="campaignId"]', 'STATUS001');
    await page.fill('input[name="name"]', '状态测试广告活动');
    await page.fill('input[name="advertiserId"]', 'ADV004');
    await page.selectOption('select[name="status"]', 'active');
    await page.fill('input[name="totalBudget"]', '6000');
    await page.fill('input[name="dailyBudget"]', '600');
    await page.selectOption('select[name="currency"]', 'USD');
    await page.selectOption('select[name="bidStrategy"]', 'cpm');
    await page.fill('input[name="maxBid"]', '1.5');
    await page.fill('input[name="baseBid"]', '1.2');
    
    const startDate = new Date();
    const endDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
    await page.fill('input[name="startDate"]', startDate.toISOString().slice(0, 10));
    await page.fill('input[name="endDate"]', endDate.toISOString().slice(0, 10));
    await page.selectOption('select[name="timezone"]', 'UTC');
    
    await page.click('button[type="submit"]');
    await page.waitForSelector('.modal-overlay', { state: 'detached' });
    
    // 验证初始状态
    await expect(page.locator('tr:has-text("状态测试广告活动") .status-active')).toBeVisible();
    
    // 暂停广告活动
    await page.click('tr:has-text("状态测试广告活动") .btn-warning:has-text("暂停")');
    
    // 验证状态已更新
    await expect(page.locator('tr:has-text("状态测试广告活动") .status-paused')).toBeVisible();
    
    // 重新激活广告活动
    await page.click('tr:has-text("状态测试广告活动") .btn-success:has-text("激活")');
    
    // 验证状态已更新回活跃
    await expect(page.locator('tr:has-text("状态测试广告活动") .status-active')).toBeVisible();
  });

  test('应该能够删除广告活动', async ({ page }) => {
    // 先创建一个广告活动用于删除
    await page.click('button:has-text("创建广告活动")');
    await page.waitForSelector('.modal-overlay');
    
    await page.fill('input[name="campaignId"]', 'DELETE001');
    await page.fill('input[name="name"]', '待删除广告活动');
    await page.fill('input[name="advertiserId"]', 'ADV005');
    await page.selectOption('select[name="status"]', 'active');
    await page.fill('input[name="totalBudget"]', '5000');
    await page.fill('input[name="dailyBudget"]', '500');
    await page.selectOption('select[name="currency"]', 'USD');
    await page.selectOption('select[name="bidStrategy"]', 'cpa');
    await page.fill('input[name="maxBid"]', '10.0');
    await page.fill('input[name="baseBid"]', '8.0');
    
    const startDate = new Date();
    const endDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
    await page.fill('input[name="startDate"]', startDate.toISOString().slice(0, 10));
    await page.fill('input[name="endDate"]', endDate.toISOString().slice(0, 10));
    await page.selectOption('select[name="timezone"]', 'UTC');
    
    await page.click('button[type="submit"]');
    await page.waitForSelector('.modal-overlay', { state: 'detached' });
    
    // 验证广告活动已创建
    await expect(page.locator('text=待删除广告活动')).toBeVisible();
    
    // 删除广告活动
    await page.click('tr:has-text("待删除广告活动") .btn-danger:has-text("删除")');
    
    // 确认删除（如果有确认对话框）
    page.on('dialog', dialog => dialog.accept());
    
    // 验证广告活动已被删除
    await expect(page.locator('text=待删除广告活动')).not.toBeVisible();
  });

  test('应该显示统计信息', async ({ page }) => {
    // 验证统计卡片存在
    await expect(page.locator('.statistics-cards')).toBeVisible();
    await expect(page.locator('.stat-card')).toHaveCount(4);
    
    // 验证统计标题
    await expect(page.locator('text=总广告活动')).toBeVisible();
    await expect(page.locator('text=活跃广告活动')).toBeVisible();
    await expect(page.locator('text=暂停广告活动')).toBeVisible();
    await expect(page.locator('text=完成广告活动')).toBeVisible();
  });

  test('应该支持分页功能', async ({ page }) => {
    // 创建多个广告活动以测试分页
    for (let i = 1; i <= 15; i++) {
      await page.click('button:has-text("创建广告活动")');
      await page.waitForSelector('.modal-overlay');
      
      await page.fill('input[name="campaignId"]', `PAGE${i.toString().padStart(3, '0')}`);
      await page.fill('input[name="name"]', `分页测试广告活动${i}`);
      await page.fill('input[name="advertiserId"]', `ADV${i.toString().padStart(3, '0')}`);
      await page.selectOption('select[name="status"]', 'active');
      await page.fill('input[name="totalBudget"]', '1000');
      await page.fill('input[name="dailyBudget"]', '100');
      await page.selectOption('select[name="currency"]', 'USD');
      await page.selectOption('select[name="bidStrategy"]', 'cpc');
      await page.fill('input[name="maxBid"]', '0.1');
      await page.fill('input[name="baseBid"]', '0.08');
      
      const startDate = new Date();
      const endDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
      await page.fill('input[name="startDate"]', startDate.toISOString().slice(0, 10));
      await page.fill('input[name="endDate"]', endDate.toISOString().slice(0, 10));
      await page.selectOption('select[name="timezone"]', 'UTC');
      
      await page.click('button[type="submit"]');
      await page.waitForSelector('.modal-overlay', { state: 'detached' });
      
      // 等待避免操作过快
      await page.waitForTimeout(200);
    }
    
    // 验证分页控件存在
    await expect(page.locator('.pagination')).toBeVisible();
    
    // 如果有下一页按钮，测试分页
    const nextButton = page.locator('.pagination button:has-text("下一页")');
    if (await nextButton.isVisible()) {
      await nextButton.click();
      await page.waitForTimeout(500);
      
      // 验证页面已切换
      await expect(page.locator('.pagination')).toBeVisible();
    }
  });
});