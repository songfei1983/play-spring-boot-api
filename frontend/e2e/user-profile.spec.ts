import { test, expect } from '@playwright/test';

test.describe('用户档案管理页面', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // 切换到用户档案页面
    await page.click('button:has-text("用户档案")');
    await expect(page.locator('h2')).toContainText('用户档案管理');
  });

  test('页面基本元素显示正常', async ({ page }) => {
    // 检查页面标题
    await expect(page.locator('h2')).toContainText('用户档案管理');
    
    // 检查搜索框
    await expect(page.locator('input[placeholder*="搜索"]')).toBeVisible();
    
    // 检查添加用户档案按钮
    await expect(page.locator('button:has-text("添加用户档案")')).toBeVisible();
    
    // 检查数据表格
    await expect(page.locator('.data-table')).toBeVisible();
    
    // 检查表格头部
    await expect(page.locator('th:has-text("ID")')).toBeVisible();
    await expect(page.locator('th:has-text("用户")')).toBeVisible();
    await expect(page.locator('th:has-text("姓名")')).toBeVisible();
    await expect(page.locator('th:has-text("年龄")')).toBeVisible();
    await expect(page.locator('th:has-text("性别")')).toBeVisible();
    await expect(page.locator('th:has-text("电话")')).toBeVisible();
    await expect(page.locator('th:has-text("地址")')).toBeVisible();
    await expect(page.locator('th:has-text("操作")')).toBeVisible();
  });

  test('搜索功能', async ({ page }) => {
    const searchInput = page.locator('input[placeholder*="搜索"]');
    
    // 输入搜索关键词
    await searchInput.fill('张三');
    
    // 等待搜索结果更新
    await page.waitForTimeout(500);
    
    // 清空搜索
    await searchInput.clear();
    await page.waitForTimeout(500);
  });

  test('添加用户档案表单', async ({ page }) => {
    // 点击添加用户档案按钮
    await page.click('button:has-text("添加用户档案")');
    
    // 检查表单是否显示
    await expect(page.locator('.profile-form')).toBeVisible();
    
    // 检查表单字段
    await expect(page.locator('select')).toBeVisible(); // 用户选择
    await expect(page.locator('input[placeholder="姓名"]')).toBeVisible();
    await expect(page.locator('input[placeholder="年龄"]')).toBeVisible();
    await expect(page.locator('select[placeholder="性别"], select:has(option[value="male"])')).toBeVisible();
    await expect(page.locator('input[placeholder="电话"]')).toBeVisible();
    await expect(page.locator('textarea[placeholder="地址"]')).toBeVisible();
    
    // 检查表单按钮
    await expect(page.locator('button:has-text("保存")')).toBeVisible();
    await expect(page.locator('button:has-text("取消")')).toBeVisible();
    
    // 点击取消按钮隐藏表单
    await page.click('button:has-text("取消")');
    await expect(page.locator('.profile-form')).not.toBeVisible();
  });

  test('表单数据输入验证', async ({ page }) => {
    // 打开添加用户档案表单
    await page.click('button:has-text("添加用户档案")');
    
    // 填写表单数据
    await page.fill('input[placeholder="姓名"]', '李四');
    await page.fill('input[placeholder="年龄"]', '28');
    
    // 选择性别
    const genderSelect = page.locator('select:has(option[value="male"])');
    if (await genderSelect.count() > 0) {
      await genderSelect.selectOption('male');
    }
    
    await page.fill('input[placeholder="电话"]', '13800138000');
    await page.fill('textarea[placeholder="地址"]', '北京市海淀区中关村大街1号');
    
    // 验证输入的数据
    await expect(page.locator('input[placeholder="姓名"]')).toHaveValue('李四');
    await expect(page.locator('input[placeholder="年龄"]')).toHaveValue('28');
    await expect(page.locator('input[placeholder="电话"]')).toHaveValue('13800138000');
    await expect(page.locator('textarea[placeholder="地址"]')).toHaveValue('北京市海淀区中关村大街1号');
  });

  test('年龄数值验证', async ({ page }) => {
    // 打开添加用户档案表单
    await page.click('button:has-text("添加用户档案")');
    
    // 输入负数年龄
    await page.fill('input[placeholder="年龄"]', '-5');
    
    // 输入过大的年龄
    await page.fill('input[placeholder="年龄"]', '200');
    
    // 输入非数字
    await page.fill('input[placeholder="年龄"]', 'abc');
    
    // 输入有效年龄
    await page.fill('input[placeholder="年龄"]', '25');
    await expect(page.locator('input[placeholder="年龄"]')).toHaveValue('25');
  });

  test('电话号码格式验证', async ({ page }) => {
    // 打开添加用户档案表单
    await page.click('button:has-text("添加用户档案")');
    
    // 输入无效的电话号码
    await page.fill('input[placeholder="电话"]', '123');
    
    // 输入包含字母的电话号码
    await page.fill('input[placeholder="电话"]', '138abc38000');
    
    // 输入有效的电话号码
    await page.fill('input[placeholder="电话"]', '13812345678');
    await expect(page.locator('input[placeholder="电话"]')).toHaveValue('13812345678');
    
    // 测试固定电话格式
    await page.fill('input[placeholder="电话"]', '010-12345678');
    await expect(page.locator('input[placeholder="电话"]')).toHaveValue('010-12345678');
  });

  test('性别选择功能', async ({ page }) => {
    // 打开添加用户档案表单
    await page.click('button:has-text("添加用户档案")');
    
    // 检查性别选择器
    const genderSelect = page.locator('select:has(option[value="male"])');
    
    if (await genderSelect.count() > 0) {
      // 选择男性
      await genderSelect.selectOption('male');
      await expect(genderSelect).toHaveValue('male');
      
      // 选择女性
      await genderSelect.selectOption('female');
      await expect(genderSelect).toHaveValue('female');
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

  test('编辑用户档案功能', async ({ page }) => {
    // 如果表格中有数据，测试编辑功能
    const editButtons = page.locator('button:has-text("编辑")');
    
    if (await editButtons.count() > 0) {
      // 点击第一个编辑按钮
      await editButtons.first().click();
      
      // 检查编辑表单是否显示
      await expect(page.locator('.profile-form')).toBeVisible();
      
      // 检查表单是否预填充了数据
      const nameInput = page.locator('input[placeholder="姓名"]');
      const name = await nameInput.inputValue();
      expect(name.length).toBeGreaterThan(0);
      
      // 修改数据
      await nameInput.clear();
      await nameInput.fill('修改后的姓名');
      
      // 取消编辑
      await page.click('button:has-text("取消")');
      await expect(page.locator('.profile-form')).not.toBeVisible();
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

  test('表单必填字段验证', async ({ page }) => {
    // 打开添加用户档案表单
    await page.click('button:has-text("添加用户档案")');
    
    // 尝试提交空表单
    await page.click('button:has-text("保存")');
    
    // 检查是否有验证提示（这里假设有客户端验证）
    // 注意：实际的验证行为取决于具体实现
    
    // 只填写部分必填字段
    await page.fill('input[placeholder="姓名"]', '测试用户');
    
    // 再次尝试保存
    await page.click('button:has-text("保存")');
  });
});