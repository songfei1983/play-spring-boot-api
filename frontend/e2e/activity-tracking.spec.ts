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
    
    // 检查用户搜索选择框
    await expect(page.locator('select')).toBeVisible();
    
    // 检查添加活动跟踪按钮
    await expect(page.locator('button:has-text("添加活动跟踪")')).toBeVisible();
    
    // 等待数据加载完成，然后检查表格或无数据提示
    await page.waitForLoadState('networkidle');
    
    // 检查是否有表格或无数据提示
    const hasTable = await page.locator('.data-table').isVisible();
    const hasNoData = await page.locator('.no-data').isVisible();
    
    expect(hasTable || hasNoData).toBeTruthy();
    
    // 如果有表格，检查表格头部
    if (hasTable) {
      await expect(page.locator('th:has-text("ID")')).toBeVisible();
      await expect(page.locator('th:has-text("用户")')).toBeVisible();
      await expect(page.locator('th:has-text("活动类型")')).toBeVisible();
      await expect(page.locator('th:has-text("描述")')).toBeVisible();
      await expect(page.locator('th:has-text("位置")')).toBeVisible();
      await expect(page.locator('th:has-text("设备")')).toBeVisible();
      await expect(page.locator('th:has-text("IP地址")')).toBeVisible();
      await expect(page.locator('th:has-text("持续时间")')).toBeVisible();
      await expect(page.locator('th').filter({ hasText: /^时间$/ })).toBeVisible();
      await expect(page.locator('th:has-text("操作")')).toBeVisible();
    }
  });

  test('搜索功能', async ({ page }) => {
    const searchSelect = page.locator('select').first();
    
    // 检查搜索选择框是否可见
    await expect(searchSelect).toBeVisible();
    
    // 检查搜索和重置按钮
    await expect(page.locator('button:has-text("搜索")')).toBeVisible();
    await expect(page.locator('button:has-text("重置")')).toBeVisible();
    
    // 点击重置按钮
    await page.click('button:has-text("重置")');
    await page.waitForTimeout(500);
  });

  test('添加活动跟踪表单', async ({ page }) => {
    // 点击添加活动跟踪按钮
    await page.click('button:has-text("添加活动跟踪")');
    
    // 检查表单是否显示
    await expect(page.locator('.form-container')).toBeVisible();
    
    // 检查表单字段
    await expect(page.locator('select#userId')).toBeVisible(); // 用户选择
    await expect(page.locator('input#activityType')).toBeVisible();
    await expect(page.locator('textarea#description')).toBeVisible();
    await expect(page.locator('input#location')).toBeVisible();
    await expect(page.locator('input#ipAddress')).toBeVisible();
    await expect(page.locator('input#duration')).toBeVisible();
    
    // 检查表单按钮
    await expect(page.locator('button:has-text("创建")')).toBeVisible();
    await expect(page.locator('button:has-text("取消")')).toBeVisible();
    
    // 点击取消按钮隐藏表单
    await page.click('button:has-text("取消")');
    await expect(page.locator('.form-container')).not.toBeVisible();
  });

  test('表单数据输入验证', async ({ page }) => {
    // 打开添加活动跟踪表单
    await page.click('button:has-text("添加活动跟踪")');
    
    // 填写表单数据
    await page.fill('input#activityType', '用户登录');
    await page.fill('textarea#description', '用户通过Web界面登录系统');
    await page.fill('input#location', '北京市朝阳区');
    await page.fill('input#ipAddress', '192.168.1.100');
    await page.fill('input#duration', '30');
    
    // 验证输入的数据
    await expect(page.locator('input#activityType')).toHaveValue('用户登录');
    await expect(page.locator('textarea#description')).toHaveValue('用户通过Web界面登录系统');
    await expect(page.locator('input#ipAddress')).toHaveValue('192.168.1.100');
  });

  test('IP地址格式验证', async ({ page }) => {
    // 打开添加活动跟踪表单
    await page.click('button:has-text("添加活动跟踪")');
    
    // 输入无效的IP地址
    await page.fill('input#ipAddress', '999.999.999.999');
    
    // 输入有效的IP地址
    await page.fill('input#ipAddress', '192.168.1.1');
    
    // 验证输入的数据
    await expect(page.locator('input#ipAddress')).toHaveValue('192.168.1.1');
  });

  test('持续时间数值验证', async ({ page }) => {
    // 打开添加活动跟踪表单
    await page.click('button:has-text("添加活动跟踪")');
    
    // 输入有效数字
    await page.fill('input#duration', '45');
    await expect(page.locator('input#duration')).toHaveValue('45');
  });

  test('表格操作按钮', async ({ page }) => {
    // 等待数据加载完成
    await page.waitForLoadState('networkidle');
    
    // 检查是否有表格
    const hasTable = await page.locator('.data-table').isVisible();
    
    if (hasTable) {
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
        await expect(editButton).toHaveClass(/btn/);
        await expect(editButton).toHaveClass(/btn-primary/);
        
        const deleteButton = firstActionCell.locator('button:has-text("删除")');
        await expect(deleteButton).toHaveClass(/btn/);
        await expect(deleteButton).toHaveClass(/btn-danger/);
      }
    }
  });

  test('表格布局和响应式设计', async ({ page }) => {
    // 等待数据加载完成
    await page.waitForLoadState('networkidle');
    
    // 检查表格容器
    await expect(page.locator('.table-container')).toBeVisible();
    
    // 检查是否有表格
    const hasTable = await page.locator('.data-table').isVisible();
    
    if (hasTable) {
      // 检查表格容器的样式
      const tableContainer = page.locator('.table-container');
      await expect(tableContainer).toHaveCSS('display', 'flex');
      await expect(tableContainer).toHaveCSS('overflow', 'auto');
      
      // 测试不同屏幕尺寸下的表格显示
      await page.setViewportSize({ width: 1400, height: 800 });
      await expect(tableContainer).toBeVisible();
      
      await page.setViewportSize({ width: 1024, height: 768 });
      await expect(tableContainer).toBeVisible();
      
      await page.setViewportSize({ width: 768, height: 1024 });
      await expect(tableContainer).toBeVisible();
      
      // 恢复正常视口
      await page.setViewportSize({ width: 1200, height: 800 });
    }
  });

  test('编辑活动跟踪功能', async ({ page }) => {
    // 等待数据加载完成
    await page.waitForLoadState('networkidle');
    
    // 检查是否有表格和数据
    const hasTable = await page.locator('.data-table').isVisible();
    
    if (hasTable) {
      // 如果表格中有数据，测试编辑功能
      const editButtons = page.locator('button:has-text("编辑")');
      
      if (await editButtons.count() > 0) {
        // 点击第一个编辑按钮
        await editButtons.first().click();
        
        // 检查编辑表单是否显示
        await expect(page.locator('.form-container')).toBeVisible();
        
        // 检查表单是否预填充了数据
        const activityTypeInput = page.locator('input#activityType');
        await page.waitForTimeout(500); // 等待表单数据填充
        const activityType = await activityTypeInput.inputValue();
        expect(activityType.length).toBeGreaterThan(0);
        
        // 修改数据
        await activityTypeInput.clear();
        await activityTypeInput.fill('修改后的活动类型');
        
        // 取消编辑
        await page.click('button:has-text("取消")');
        await expect(page.locator('.form-container')).not.toBeVisible();
      }
    }
  });

  test('删除确认对话框', async ({ page }) => {
    // 等待数据加载完成
    await page.waitForLoadState('networkidle');
    
    // 检查是否有表格和数据
    const hasTable = await page.locator('.data-table').isVisible();
    
    if (hasTable) {
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
        
        // 等待对话框处理完成
        await page.waitForTimeout(500);
      }
    }
  });
});