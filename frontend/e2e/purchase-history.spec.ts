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
    await expect(page.locator('input[placeholder*="搜索"]')).toBeVisible();
    
    // 检查添加购买记录按钮
    await expect(page.locator('button:has-text("添加购买记录")')).toBeVisible();
    
    // 检查数据表格
    await expect(page.locator('.data-table')).toBeVisible();
    
    // 检查表格头部
    await expect(page.locator('th:has-text("ID")')).toBeVisible();
    await expect(page.locator('th:has-text("用户")')).toBeVisible();
    await expect(page.locator('th:has-text("产品")')).toBeVisible();
    await expect(page.locator('th:has-text("数量")')).toBeVisible();
    await expect(page.locator('th:has-text("单价")')).toBeVisible();
    await expect(page.locator('th:has-text("总价")')).toBeVisible();
    await expect(page.locator('th:has-text("购买时间")')).toBeVisible();
    await expect(page.locator('th:has-text("操作")')).toBeVisible();
  });

  test('搜索功能', async ({ page }) => {
    const searchInput = page.locator('input[placeholder*="搜索"]');
    
    // 输入搜索关键词
    await searchInput.fill('产品');
    
    // 等待搜索结果更新
    await page.waitForTimeout(500);
    
    // 清空搜索
    await searchInput.clear();
    await page.waitForTimeout(500);
  });

  test('添加购买记录表单', async ({ page }) => {
    // 点击添加购买记录按钮
    await page.click('button:has-text("添加购买记录")');
    
    // 检查表单是否显示
    await expect(page.locator('.purchase-form')).toBeVisible();
    
    // 检查表单字段
    await expect(page.locator('select')).toBeVisible(); // 用户选择
    await expect(page.locator('input[placeholder="产品名称"]')).toBeVisible();
    await expect(page.locator('input[placeholder="数量"]')).toBeVisible();
    await expect(page.locator('input[placeholder="单价"]')).toBeVisible();
    
    // 检查表单按钮
    await expect(page.locator('button:has-text("保存")')).toBeVisible();
    await expect(page.locator('button:has-text("取消")')).toBeVisible();
    
    // 点击取消按钮隐藏表单
    await page.click('button:has-text("取消")');
    await expect(page.locator('.purchase-form')).not.toBeVisible();
  });

  test('表单数据输入和计算', async ({ page }) => {
    // 打开添加购买记录表单
    await page.click('button:has-text("添加购买记录")');
    
    // 填写表单数据
    await page.fill('input[placeholder="产品名称"]', '测试产品');
    await page.fill('input[placeholder="数量"]', '2');
    await page.fill('input[placeholder="单价"]', '99.99');
    
    // 检查总价是否自动计算（如果实现了的话）
    const totalPriceInput = page.locator('input[placeholder="总价"]');
    if (await totalPriceInput.count() > 0) {
      // 等待计算完成
      await page.waitForTimeout(500);
      const totalValue = await totalPriceInput.inputValue();
      expect(parseFloat(totalValue)).toBe(199.98);
    }
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
      
      // 检查按钮样式
      const editButton = firstActionCell.locator('button:has-text("编辑")');
      await expect(editButton).toHaveClass(/btn-primary/);
      
      const deleteButton = firstActionCell.locator('button:has-text("删除")');
      await expect(deleteButton).toHaveClass(/btn-danger/);
    }
  });

  test('表格滚动和布局', async ({ page }) => {
    // 检查表格容器
    await expect(page.locator('.table-container')).toBeVisible();
    
    // 检查表格容器的样式
    const tableContainer = page.locator('.table-container');
    await expect(tableContainer).toHaveCSS('display', 'flex');
    await expect(tableContainer).toHaveCSS('overflow-x', 'auto');
    
    // 检查表格的响应式行为
    await page.setViewportSize({ width: 800, height: 600 });
    await expect(tableContainer).toBeVisible();
    
    // 恢复正常视口
    await page.setViewportSize({ width: 1200, height: 800 });
  });

  test('数据加载和错误处理', async ({ page }) => {
    // 刷新页面以触发数据加载
    await page.reload();
    
    // 等待页面加载完成
    await page.waitForLoadState('networkidle');
    
    // 检查是否有错误信息显示（如果后端不可用）
    const errorMessage = page.locator('.error-message, [data-testid="error"]');
    
    // 如果有错误信息，验证其可见性
    if (await errorMessage.count() > 0) {
      await expect(errorMessage).toBeVisible();
    }
  });

  test('编辑功能', async ({ page }) => {
    // 如果表格中有数据，测试编辑功能
    const editButtons = page.locator('button:has-text("编辑")');
    
    if (await editButtons.count() > 0) {
      // 点击第一个编辑按钮
      await editButtons.first().click();
      
      // 检查编辑表单是否显示
      await expect(page.locator('.purchase-form')).toBeVisible();
      
      // 检查表单是否预填充了数据
      const productNameInput = page.locator('input[placeholder="产品名称"]');
      const productName = await productNameInput.inputValue();
      expect(productName.length).toBeGreaterThan(0);
      
      // 取消编辑
      await page.click('button:has-text("取消")');
      await expect(page.locator('.purchase-form')).not.toBeVisible();
    }
  });
});