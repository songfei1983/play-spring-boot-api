import { test, expect } from '@playwright/test';

test.describe('管理系统应用', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('应用正常加载并显示默认页面', async ({ page }) => {
    // 检查页面标题
    await expect(page).toHaveTitle(/React App/);
    
    // 检查导航栏是否存在
    await expect(page.locator('.nav-tabs')).toBeVisible();
    
    // 检查默认显示用户管理页面
    await expect(page.locator('.main-content')).toBeVisible();
    await expect(page.locator('h2')).toContainText('用户管理');
  });

  test('页面导航功能正常', async ({ page }) => {
    // 测试用户档案页面导航
    await page.click('button:has-text("用户档案")');
    await expect(page.locator('h2')).toContainText('用户档案管理');
    
    // 测试活动跟踪页面导航
    await page.click('button:has-text("活动跟踪")');
    await expect(page.locator('h2')).toContainText('活动跟踪管理');
    
    // 测试购买历史页面导航
    await page.click('button:has-text("购买历史")');
    await expect(page.locator('h2')).toContainText('购买历史管理');
    
    // 返回用户管理页面
    await page.click('button:has-text("用户管理")');
    await expect(page.locator('h2')).toContainText('用户管理');
  });

  test('页面状态持久化功能', async ({ page }) => {
    // 切换到购买历史页面
    await page.click('button:has-text("购买历史")');
    await expect(page.locator('h2')).toContainText('购买历史管理');
    
    // 刷新页面
    await page.reload();
    
    // 验证页面状态保持在购买历史页面
    await expect(page.locator('h2')).toContainText('购买历史管理');
    await expect(page.locator('button:has-text("购买历史")')).toHaveClass(/active/);
  });

  test('响应式布局检查', async ({ page }) => {
    // 检查桌面视图
    await page.setViewportSize({ width: 1200, height: 800 });
    await expect(page.locator('.main-content')).toBeVisible();
    
    // 检查平板视图
    await page.setViewportSize({ width: 768, height: 1024 });
    await expect(page.locator('.main-content')).toBeVisible();
    
    // 检查移动视图
    await page.setViewportSize({ width: 375, height: 667 });
    await expect(page.locator('.main-content')).toBeVisible();
  });
});