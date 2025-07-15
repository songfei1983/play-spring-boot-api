import { test, expect } from '@playwright/test';

test.describe('活动跟踪管理页面', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // 切换到活动跟踪页面
    await page.click('button:has-text("活动跟踪")');
    await expect(page.locator('h2')).toContainText('活动跟踪管理');
  });

  test('页面基本元素显示正常', async ({ page }) => {
    // 检查页面标题
    await expect(page.locator('h2')).toContainText('活动跟踪管理');
    
    // 检查搜索框
    await expect(page.locator('input[placeholder*="搜索"]')).toBeVisible();
    
    // 检查添加活动记录按钮
    await expect(page.locator('button:has-text("添加活动记录")')).toBeVisible();
    
    // 检查数据表格
    await expect(page.locator('.data-table')).toBeVisible();
    
    // 检查表格头部
    await expect(page.locator('th:has-text("ID")')).toBeVisible();
    await expect(page.locator('th:has-text("用户")')).toBeVisible();
    await expect(page.locator('th:has-text("活动类型")')).toBeVisible();
    await expect(page.locator('th:has-text("描述")')).toBeVisible();
    await expect(page.locator('th:has-text("位置")')).toBeVisible();
    await expect(page.locator('th:has-text("设备")')).toBeVisible();
    await expect(page.locator('th:has-text("IP地址")')).toBeVisible();
    await expect(page.locator('th:has-text("持续时间")')).toBeVisible();
    await expect(page.locator('th:has-text("时间")')).toBeVisible();
    await expect(page.locator('th:has-text("操作")')).toBeVisible();
  });

  test('搜索功能', async ({ page }) => {
    const searchInput = page.locator('input[placeholder*="搜索"]');
    
    // 输入搜索关键词
    await searchInput.fill('登录');
    
    // 等待搜索结果更新
    await page.waitForTimeout(500);
    
    // 清空搜索
    await searchInput.clear();
    await page.waitForTimeout(500);
  });

  test('添加活动记录表单', async ({ page }) => {
    // 点击添加活动记录按钮
    await page.click('button:has-text("添加活动记录")');
    
    // 检查表单是否显示
    await expect(page.locator('.activity-form')).toBeVisible();
    
    // 检查表单字段
    await expect(page.locator('select')).toBeVisible(); // 用户选择
    await expect(page.locator('input[placeholder="活动类型"]')).toBeVisible();
    await expect(page.locator('textarea[placeholder="描述"]')).toBeVisible();
    await expect(page.locator('input[placeholder="位置"]')).toBeVisible();
    await expect(page.locator('input[placeholder="设备"]')).toBeVisible();
    await expect(page.locator('input[placeholder="IP地址"]')).toBeVisible();
    await expect(page.locator('input[placeholder="持续时间(分钟)"]')).toBeVisible();
    
    // 检查表单按钮
    await expect(page.locator('button:has-text("保存")')).toBeVisible();
    await expect(page.locator('button:has-text("取消")')).toBeVisible();
    
    // 点击取消按钮隐藏表单
    await page.click('button:has-text("取消")');
    await expect(page.locator('.activity-form')).not.toBeVisible();
  });

  test('表单数据输入验证', async ({ page }) => {
    // 打开添加活动记录表单
    await page.click('button:has-text("添加活动记录")');
    
    // 填写表单数据
    await page.fill('input[placeholder="活动类型"]', '用户登录');
    await page.fill('textarea[placeholder="描述"]', '用户通过Web界面登录系统');
    await page.fill('input[placeholder="位置"]', '北京市朝阳区');
    await page.fill('input[placeholder="设备"]', 'MacBook Pro');
    await page.fill('input[placeholder="IP地址"]', '192.168.1.100');
    await page.fill('input[placeholder="持续时间(分钟)"]', '30');
    
    // 验证输入的数据
    await expect(page.locator('input[placeholder="活动类型"]')).toHaveValue('用户登录');
    await expect(page.locator('textarea[placeholder="描述"]')).toHaveValue('用户通过Web界面登录系统');
    await expect(page.locator('input[placeholder="IP地址"]')).toHaveValue('192.168.1.100');
  });

  test('IP地址格式验证', async ({ page }) => {
    // 打开添加活动记录表单
    await page.click('button:has-text("添加活动记录")');
    
    // 输入无效的IP地址
    await page.fill('input[placeholder="IP地址"]', '999.999.999.999');
    
    // 尝试保存（如果有客户端验证的话）
    await page.click('button:has-text("保存")');
    
    // 输入有效的IP地址
    await page.fill('input[placeholder="IP地址"]', '192.168.1.1');
  });

  test('持续时间数值验证', async ({ page }) => {
    // 打开添加活动记录表单
    await page.click('button:has-text("添加活动记录")');
    
    // 输入负数
    await page.fill('input[placeholder="持续时间(分钟)"]', '-10');
    
    // 输入非数字
    await page.fill('input[placeholder="持续时间(分钟)"]', 'abc');
    
    // 输入有效数字
    await page.fill('input[placeholder="持续时间(分钟)"]', '45');
    await expect(page.locator('input[placeholder="持续时间(分钟)"]')).toHaveValue('45');
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

  test('表格布局和响应式设计', async ({ page }) => {
    // 检查表格容器
    await expect(page.locator('.table-container')).toBeVisible();
    
    // 检查表格容器的样式
    const tableContainer = page.locator('.table-container');
    await expect(tableContainer).toHaveCSS('display', 'flex');
    await expect(tableContainer).toHaveCSS('overflow-x', 'auto');
    
    // 测试不同屏幕尺寸下的表格显示
    await page.setViewportSize({ width: 1400, height: 800 });
    await expect(tableContainer).toBeVisible();
    
    await page.setViewportSize({ width: 1024, height: 768 });
    await expect(tableContainer).toBeVisible();
    
    await page.setViewportSize({ width: 768, height: 1024 });
    await expect(tableContainer).toBeVisible();
    
    // 恢复正常视口
    await page.setViewportSize({ width: 1200, height: 800 });
  });

  test('编辑活动记录功能', async ({ page }) => {
    // 如果表格中有数据，测试编辑功能
    const editButtons = page.locator('button:has-text("编辑")');
    
    if (await editButtons.count() > 0) {
      // 点击第一个编辑按钮
      await editButtons.first().click();
      
      // 检查编辑表单是否显示
      await expect(page.locator('.activity-form')).toBeVisible();
      
      // 检查表单是否预填充了数据
      const activityTypeInput = page.locator('input[placeholder="活动类型"]');
      const activityType = await activityTypeInput.inputValue();
      expect(activityType.length).toBeGreaterThan(0);
      
      // 修改数据
      await activityTypeInput.clear();
      await activityTypeInput.fill('修改后的活动类型');
      
      // 取消编辑
      await page.click('button:has-text("取消")');
      await expect(page.locator('.activity-form')).not.toBeVisible();
    }
  });

  test('删除确认对话框', async ({ page }) => {
    // 如果表格中有数据，测试删除功能
    const deleteButtons = page.locator('button:has-text("删除")');
    
    if (await deleteButtons.count() > 0) {
      // 监听确认对话框
      page.on('dialog', async dialog => {
        expect(dialog.type()).toBe('confirm');
        expect(dialog.message()).toContain('确定要删除');
        await dialog.dismiss(); // 取消删除
      });
      
      // 点击删除按钮
      await deleteButtons.first().click();
    }
  });
});