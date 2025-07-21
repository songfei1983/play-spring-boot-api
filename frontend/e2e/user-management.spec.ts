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
    
    // 检查添加用户按钮
    await expect(page.locator('button:has-text("添加用户")')).toBeVisible();
    
    // 检查数据表格
    await expect(page.locator('.data-table')).toBeVisible();
    
    // 检查表格头部
    await expect(page.locator('th:has-text("ID")')).toBeVisible();
    await expect(page.locator('th:has-text("姓名")')).toBeVisible();
    await expect(page.locator('th:has-text("邮箱")')).toBeVisible();
    await expect(page.locator('th:has-text("操作")')).toBeVisible();
  });

  test('用户列表显示', async ({ page }) => {
    // 等待页面加载完成
    await page.waitForLoadState('networkidle');
    
    // 检查表格容器是否可见
    await expect(page.locator('.table-container')).toBeVisible();
    
    // 等待加载状态结束（如果存在）
    await page.waitForFunction(() => {
      const loadingElement = document.querySelector('.loading');
      return !loadingElement || !loadingElement.textContent;
    }, { timeout: 10000 });
    
    // 再次等待网络请求完成
    await page.waitForTimeout(1000);
    
    // 检查是否显示了用户数据或无数据提示
    const hasData = await page.locator('.data-table tbody tr').count() > 0;
    const hasNoDataMessage = await page.locator('.no-data').isVisible();
    
    // 如果既没有数据也没有无数据提示，打印调试信息
    if (!hasData && !hasNoDataMessage) {
      console.log('Debug: No data rows found and no "no-data" message visible');
      console.log('Table container HTML:', await page.locator('.table-container').innerHTML());
    }
    
    expect(hasData || hasNoDataMessage).toBeTruthy();
  });

  test('添加用户表单显示和隐藏', async ({ page }) => {
    // 点击添加用户按钮
    await page.click('button:has-text("添加用户")');
    
    // 检查表单是否显示
    await expect(page.locator('.form-container')).toBeVisible();
    
    // 检查表单字段
    await expect(page.locator('input#name')).toBeVisible();
    await expect(page.locator('input#email')).toBeVisible();
    
    // 检查表单按钮
    await expect(page.locator('button:has-text("创建")')).toBeVisible();
    await expect(page.locator('button:has-text("取消")')).toBeVisible();
    
    // 点击取消按钮隐藏表单
    await page.click('button:has-text("取消")');
    await expect(page.locator('.form-container')).not.toBeVisible();
  });

  test('表单验证', async ({ page }) => {
    // 打开添加用户表单
    await page.click('button:has-text("添加用户")');
    
    // 检查表单字段是否为必填项
    await expect(page.locator('input#name')).toHaveAttribute('required');
    await expect(page.locator('input#email')).toHaveAttribute('required');
    
    // 填写表单字段
    await page.fill('input#name', 'testuser');
    await page.fill('input#email', 'test@example.com');
    
    // 检查字段值是否正确填入
    await expect(page.locator('input#name')).toHaveValue('testuser');
    await expect(page.locator('input#email')).toHaveValue('test@example.com');
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