import { test, expect } from '@playwright/test';

test.describe('购买历史管理页面', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // 切换到购买历史页面
    await page.click('button:has-text("购买历史")');
    await expect(page.locator('h2')).toContainText('购买历史管理');
  });

  test('页面基本元素显示正常', async ({ page }) => {
    // 检查页面标题
    await expect(page.locator('h2')).toContainText('购买历史管理');
    
    // 检查搜索框
    await expect(page.locator('input[placeholder*="搜索订单号、商品名称"]')).toBeVisible();
    
    // 检查添加购买记录按钮
    await expect(page.locator('button:has-text("添加购买记录")')).toBeVisible();
    
    // 检查数据表格
    await expect(page.locator('.data-table')).toBeVisible();
    
    // 检查表格头部
    await expect(page.locator('th').filter({ hasText: /^ID$/ })).toBeVisible();
    await expect(page.locator('th:has-text("用户ID")')).toBeVisible();
    await expect(page.locator('th:has-text("商品名称")')).toBeVisible();
    await expect(page.locator('th:has-text("数量")')).toBeVisible();
    await expect(page.locator('th:has-text("单价")')).toBeVisible();
    await expect(page.locator('th:has-text("总价")')).toBeVisible();
    await expect(page.locator('th:has-text("购买时间")')).toBeVisible();
    await expect(page.locator('th:has-text("操作")')).toBeVisible();
  });

  test('搜索功能', async ({ page }) => {
    const searchInput = page.locator('input[placeholder*="搜索订单号、商品名称"]');
    
    // 检查搜索框可见
    await expect(searchInput).toBeVisible();
    
    // 检查搜索按钮
    await expect(page.locator('button:has-text("搜索")')).toBeVisible();
    
    // 检查重置按钮
    await expect(page.locator('button:has-text("重置")')).toBeVisible();
  });

  test('添加购买记录表单', async ({ page }) => {
    // 点击添加购买记录按钮
    await page.click('button:has-text("添加购买记录")');
    
    // 检查表单是否显示
    await expect(page.locator('.form-container')).toBeVisible();
    
    // 检查表单字段
    await expect(page.locator('input[name="userId"]')).toBeVisible();
    await expect(page.locator('input[name="orderNumber"]')).toBeVisible();
    await expect(page.locator('input[name="productName"]')).toBeVisible();
    await expect(page.locator('input[name="quantity"]')).toBeVisible();
    await expect(page.locator('input[name="unitPrice"]')).toBeVisible();
    
    // 检查表单按钮
    await expect(page.locator('button:has-text("创建")')).toBeVisible();
    await expect(page.locator('button:has-text("取消")')).toBeVisible();
    
    // 点击取消按钮隐藏表单
    await page.click('button:has-text("取消")');
    await expect(page.locator('.form-container')).not.toBeVisible();
  });

  test('表单数据输入', async ({ page }) => {
    // 打开添加购买记录表单
    await page.click('button:has-text("添加购买记录")');
    
    // 填写表单数据
    await page.fill('input[name="userId"]', '1');
    await page.fill('input[name="orderNumber"]', 'TEST001');
    await page.fill('input[name="productName"]', '测试产品');
    await page.fill('input[name="quantity"]', '2');
    await page.fill('input[name="unitPrice"]', '99.99');
    await page.fill('input[name="totalPrice"]', '199.98');
    
    // 验证输入值
    await expect(page.locator('input[name="productName"]')).toHaveValue('测试产品');
    await expect(page.locator('input[name="quantity"]')).toHaveValue('2');
  });

  test('表格操作按钮', async ({ page }) => {
    // 检查操作按钮容器
    const actionCells = page.locator('.actions-cell');
    
    if (await actionCells.count() > 0) {
      const firstActionCell = actionCells.first();
      
      // 检查编辑按钮
      await expect(firstActionCell.locator('button:has-text("编辑")')).toBeVisible();
      
      // 检查删除按钮
      await expect(firstActionCell.locator('button:has-text("删除")')).toBeVisible();
    }
  });

  test('表格布局', async ({ page }) => {
    // 检查表格容器
    await expect(page.locator('.table-container')).toBeVisible();
    
    // 检查数据表格
    await expect(page.locator('.data-table')).toBeVisible();
  });

  test('页面加载', async ({ page }) => {
    // 等待页面加载完成
    await page.waitForLoadState('networkidle');
    
    // 检查页面标题是否正确显示
    await expect(page.locator('h2')).toContainText('购买历史管理');
  });

  test('编辑功能', async ({ page }) => {
    // 如果表格中有数据，测试编辑功能
    const editButtons = page.locator('button:has-text("编辑")');
    
    if (await editButtons.count() > 0) {
      // 点击第一个编辑按钮
      await editButtons.first().click();
      
      // 检查编辑表单是否显示
      await expect(page.locator('.form-container')).toBeVisible();
      
      // 取消编辑
      await page.click('button:has-text("取消")');
      await expect(page.locator('.form-container')).not.toBeVisible();
    }
  });
});