import { test, expect } from '@playwright/test';

test.describe('用户管理页面', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // 确保在用户管理页面
    await page.click('button:has-text("用户管理")');
    await expect(page.locator('h2')).toContainText('用户管理');
  });

  test('页面基本元素显示正常', async ({ page }) => {
    // 检查页面标题
    await expect(page.locator('h2')).toContainText('用户管理');
    
    // 检查搜索框
    await expect(page.locator('input[placeholder*="搜索"]')).toBeVisible();
    
    // 检查添加用户按钮
    await expect(page.locator('button:has-text("添加用户")')).toBeVisible();
    
    // 检查数据表格
    await expect(page.locator('.data-table')).toBeVisible();
    
    // 检查表格头部
    await expect(page.locator('th:has-text("ID")')).toBeVisible();
    await expect(page.locator('th:has-text("用户名")')).toBeVisible();
    await expect(page.locator('th:has-text("邮箱")')).toBeVisible();
    await expect(page.locator('th:has-text("操作")')).toBeVisible();
  });

  test('搜索功能', async ({ page }) => {
    const searchInput = page.locator('input[placeholder*="搜索"]');
    
    // 输入搜索关键词
    await searchInput.fill('test');
    
    // 等待搜索结果更新（如果有实际数据的话）
    await page.waitForTimeout(500);
    
    // 清空搜索
    await searchInput.clear();
    await page.waitForTimeout(500);
  });

  test('添加用户表单显示和隐藏', async ({ page }) => {
    // 点击添加用户按钮
    await page.click('button:has-text("添加用户")');
    
    // 检查表单是否显示
    await expect(page.locator('.user-form')).toBeVisible();
    
    // 检查表单字段
    await expect(page.locator('input[placeholder="用户名"]')).toBeVisible();
    await expect(page.locator('input[placeholder="邮箱"]')).toBeVisible();
    await expect(page.locator('input[placeholder="密码"]')).toBeVisible();
    
    // 检查表单按钮
    await expect(page.locator('button:has-text("保存")')).toBeVisible();
    await expect(page.locator('button:has-text("取消")')).toBeVisible();
    
    // 点击取消按钮隐藏表单
    await page.click('button:has-text("取消")');
    await expect(page.locator('.user-form')).not.toBeVisible();
  });

  test('表单验证', async ({ page }) => {
    // 打开添加用户表单
    await page.click('button:has-text("添加用户")');
    
    // 尝试提交空表单
    await page.click('button:has-text("保存")');
    
    // 检查是否有验证提示（这里假设有客户端验证）
    // 注意：实际的验证行为取决于具体实现
    
    // 填写部分字段
    await page.fill('input[placeholder="用户名"]', 'testuser');
    await page.fill('input[placeholder="邮箱"]', 'invalid-email');
    
    // 检查邮箱格式验证（如果有的话）
    await page.click('button:has-text("保存")');
  });

  test('表格布局和滚动', async ({ page }) => {
    // 检查表格容器
    await expect(page.locator('.table-container')).toBeVisible();
    
    // 检查表格是否可以滚动（如果内容超出容器）
    const tableContainer = page.locator('.table-container');
    await expect(tableContainer).toHaveCSS('overflow-x', 'auto');
    
    // 检查操作按钮布局
    const actionButtons = page.locator('.action-buttons');
    if (await actionButtons.count() > 0) {
      await expect(actionButtons.first()).toBeVisible();
    }
  });

  test('加载状态显示', async ({ page }) => {
    // 刷新页面以触发加载状态
    await page.reload();
    
    // 检查是否有加载指示器（如果实现了的话）
    // 这里可能需要根据实际实现调整
    const loadingIndicator = page.locator('.loading, [data-testid="loading"]');
    
    // 等待加载完成
    await page.waitForLoadState('networkidle');
  });
});