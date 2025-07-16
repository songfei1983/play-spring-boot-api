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
    await expect(page.locator('input[placeholder="按地址搜索..."]')).toBeVisible();
    
    // 检查添加用户档案按钮
    await expect(page.locator('button:has-text("添加用户档案")')).toBeVisible();
    
    // 检查数据表格
    await expect(page.locator('.data-table')).toBeVisible();
    
    // 检查表格头部
    await expect(page.locator('th:has-text("ID")')).toBeVisible();
    await expect(page.locator('th:has-text("用户")')).toBeVisible();
    await expect(page.locator('th:has-text("年龄")')).toBeVisible();
    await expect(page.locator('th:has-text("性别")')).toBeVisible();
    await expect(page.locator('th:has-text("生日")')).toBeVisible();
    await expect(page.locator('th:has-text("电话")')).toBeVisible();
    await expect(page.locator('th:has-text("地址")')).toBeVisible();
    await expect(page.locator('th:has-text("职业")')).toBeVisible();
    await expect(page.locator('th:has-text("操作")')).toBeVisible();
  });

  test('搜索功能', async ({ page }) => {
    const searchInput = page.locator('input[placeholder="按地址搜索..."]');
    
    // 输入搜索关键词
    await searchInput.fill('北京');
    
    // 点击搜索按钮
    await page.click('button:has-text("搜索")');
    
    // 等待搜索结果更新
    await page.waitForTimeout(500);
    
    // 点击重置按钮
    await page.click('button:has-text("重置")');
    await page.waitForTimeout(500);
  });

  test('添加用户档案表单', async ({ page }) => {
    // 点击添加用户档案按钮
    await page.click('button:has-text("添加用户档案")');
    
    // 检查表单是否显示
    await expect(page.locator('.form-container')).toBeVisible();
    
    // 检查表单字段
    await expect(page.locator('select#userId')).toBeVisible(); // 用户选择
    await expect(page.locator('input#age')).toBeVisible();
    await expect(page.locator('select#gender')).toBeVisible();
    await expect(page.locator('input#birthday')).toBeVisible();
    await expect(page.locator('input#phoneNumber')).toBeVisible();
    await expect(page.locator('input#address')).toBeVisible();
    await expect(page.locator('input#occupation')).toBeVisible();
    await expect(page.locator('textarea#bio')).toBeVisible();
    
    // 检查表单按钮
    await expect(page.locator('button:has-text("创建")')).toBeVisible();
    await expect(page.locator('button:has-text("取消")')).toBeVisible();
    
    // 点击取消按钮隐藏表单
    await page.click('button:has-text("取消")');
    await expect(page.locator('.form-container')).not.toBeVisible();
  });

  test('表单数据输入验证', async ({ page }) => {
    // 打开添加用户档案表单
    await page.click('button:has-text("添加用户档案")');
    
    // 填写表单数据
    await page.fill('input#age', '28');
    
    // 选择性别
    await page.selectOption('select#gender', '男');
    
    await page.fill('input#phoneNumber', '13800138000');
    await page.fill('input#address', '北京市海淀区中关村大街1号');
    await page.fill('input#occupation', '产品经理');
    await page.fill('textarea#bio', '专注用户体验设计');
    
    // 验证输入的数据
    await expect(page.locator('input#age')).toHaveValue('28');
    await expect(page.locator('select#gender')).toHaveValue('男');
    await expect(page.locator('input#phoneNumber')).toHaveValue('13800138000');
    await expect(page.locator('input#address')).toHaveValue('北京市海淀区中关村大街1号');
    await expect(page.locator('input#occupation')).toHaveValue('产品经理');
    await expect(page.locator('textarea#bio')).toHaveValue('专注用户体验设计');
  });

  test('年龄数值验证', async ({ page }) => {
    // 打开添加用户档案表单
    await page.click('button:has-text("添加用户档案")');
    
    // 测试年龄输入
    const ageInput = page.locator('input#age');
    
    // 输入有效年龄
    await ageInput.fill('25');
    await expect(ageInput).toHaveValue('25');
    
    // 输入无效年龄（负数）
    await ageInput.fill('-5');
    // 验证输入值
    await expect(ageInput).toHaveValue('-5');
    
    // 输入无效年龄（过大）
    await ageInput.fill('200');
    // 验证输入值
    await expect(ageInput).toHaveValue('200');
    
    // 清空字段
    await ageInput.fill('');
    await expect(ageInput).toHaveValue('');
  });

  test('电话号码格式验证', async ({ page }) => {
    // 打开添加用户档案表单
    await page.click('button:has-text("添加用户档案")');
    
    const phoneInput = page.locator('input#phoneNumber');
    
    // 输入无效的电话号码
    await phoneInput.fill('123');
    await expect(phoneInput).toHaveValue('123');
    
    // 输入包含字母的电话号码
    await phoneInput.fill('138abc38000');
    await expect(phoneInput).toHaveValue('138abc38000');
    
    // 输入有效的电话号码
    await phoneInput.fill('13812345678');
    await expect(phoneInput).toHaveValue('13812345678');
    
    // 测试固定电话格式
    await phoneInput.fill('010-12345678');
    await expect(phoneInput).toHaveValue('010-12345678');
  });

  test('性别选择功能', async ({ page }) => {
    // 打开添加用户档案表单
    await page.click('button:has-text("添加用户档案")');
    
    // 检查性别选择器
    const genderSelect = page.locator('select#gender');
    
    // 选择男性
    await genderSelect.selectOption('男');
    await expect(genderSelect).toHaveValue('男');
    
    // 选择女性
    await genderSelect.selectOption('女');
    await expect(genderSelect).toHaveValue('女');
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
      await expect(page.locator('.form-container')).toBeVisible();
      
      // 检查表单字段
      const ageInput = page.locator('input#age');
      const phoneInput = page.locator('input#phoneNumber');
      
      // 修改数据
      await ageInput.fill('35');
      await phoneInput.fill('13900139000');
      
      // 保存修改
      await page.click('button:has-text("更新")');
      
      // 等待保存完成
      await page.waitForTimeout(1000);
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
    
    // 检查表单字段是否存在和可用
    await expect(page.locator('select#userId')).toBeVisible();
    await expect(page.locator('select#gender')).toBeVisible();
    await expect(page.locator('input#age')).toBeVisible();
    
    // 检查年龄字段的约束
    await expect(page.locator('input#age')).toHaveAttribute('min', '1');
    await expect(page.locator('input#age')).toHaveAttribute('max', '120');
    
    // 检查用户选择字段是否有required属性
    await expect(page.locator('select#userId')).toHaveAttribute('required');
    
    // 填写必填字段
    // 选择用户（假设有用户选项）
    const userSelect = page.locator('select#userId');
    const userOptions = await userSelect.locator('option').count();
    if (userOptions > 1) {
      await userSelect.selectOption({ index: 1 });
    }
    
    await page.fill('input#age', '30');
    await page.selectOption('select#gender', '男');
    
    // 验证字段已填写
    await expect(page.locator('input#age')).toHaveValue('30');
    await expect(page.locator('select#gender')).toHaveValue('男');
  });
});