/**
 * 测试数据工厂
 * 用于生成E2E测试中使用的模拟数据
 */

export class TestDataFactory {
  /**
   * 生成随机用户数据
   */
  static generateUser() {
    const timestamp = Date.now();
    return {
      username: `testuser_${timestamp}`,
      email: `test_${timestamp}@example.com`,
      password: 'Test123456!'
    };
  }

  /**
   * 生成随机用户档案数据
   */
  static generateUserProfile() {
    const timestamp = Date.now();
    const names = ['张三', '李四', '王五', '赵六', '钱七'];
    const genders = ['male', 'female'];
    const cities = ['北京市', '上海市', '广州市', '深圳市', '杭州市'];
    
    return {
      name: names[Math.floor(Math.random() * names.length)] + timestamp.toString().slice(-3),
      age: Math.floor(Math.random() * 50) + 18, // 18-67岁
      gender: genders[Math.floor(Math.random() * genders.length)],
      phone: `138${Math.floor(Math.random() * 100000000).toString().padStart(8, '0')}`,
      address: cities[Math.floor(Math.random() * cities.length)] + '某某区某某街道' + Math.floor(Math.random() * 999) + '号'
    };
  }

  /**
   * 生成随机活动跟踪数据
   */
  static generateActivityTrack() {
    const timestamp = Date.now();
    const activityTypes = ['用户登录', '页面访问', '数据查询', '文件下载', '系统设置'];
    const devices = ['MacBook Pro', 'iPhone 12', 'Windows PC', 'Android Phone', 'iPad'];
    const locations = ['北京市朝阳区', '上海市浦东新区', '广州市天河区', '深圳市南山区', '杭州市西湖区'];
    
    return {
      activityType: activityTypes[Math.floor(Math.random() * activityTypes.length)],
      description: `测试活动描述_${timestamp}`,
      location: locations[Math.floor(Math.random() * locations.length)],
      device: devices[Math.floor(Math.random() * devices.length)],
      ipAddress: this.generateRandomIP(),
      duration: Math.floor(Math.random() * 120) + 1 // 1-120分钟
    };
  }

  /**
   * 生成随机购买历史数据
   */
  static generatePurchaseHistory() {
    const timestamp = Date.now();
    const products = ['笔记本电脑', '智能手机', '平板电脑', '智能手表', '无线耳机'];
    
    const quantity = Math.floor(Math.random() * 5) + 1; // 1-5个
    const unitPrice = Math.floor(Math.random() * 5000) + 100; // 100-5099元
    
    return {
      product: products[Math.floor(Math.random() * products.length)] + '_' + timestamp.toString().slice(-4),
      quantity: quantity,
      unitPrice: unitPrice,
      totalPrice: quantity * unitPrice
    };
  }

  /**
   * 生成随机IP地址
   */
  private static generateRandomIP(): string {
    return [
      Math.floor(Math.random() * 255) + 1,
      Math.floor(Math.random() * 255),
      Math.floor(Math.random() * 255),
      Math.floor(Math.random() * 255)
    ].join('.');
  }

  /**
   * 生成随机邮箱地址
   */
  static generateEmail(): string {
    const domains = ['example.com', 'test.com', 'demo.org', 'sample.net'];
    const timestamp = Date.now();
    return `test_${timestamp}@${domains[Math.floor(Math.random() * domains.length)]}`;
  }

  /**
   * 生成随机电话号码
   */
  static generatePhoneNumber(): string {
    const prefixes = ['138', '139', '158', '159', '188', '189'];
    const prefix = prefixes[Math.floor(Math.random() * prefixes.length)];
    const suffix = Math.floor(Math.random() * 100000000).toString().padStart(8, '0');
    return prefix + suffix;
  }

  /**
   * 等待指定时间
   */
  static async wait(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  /**
   * 生成随机字符串
   */
  static generateRandomString(length: number = 8): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  }

  /**
   * 生成随机数字
   */
  static generateRandomNumber(min: number = 1, max: number = 100): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }
}

/**
 * 页面对象模型基类
 */
export class BasePage {
  constructor(protected page: any) {}

  /**
   * 等待页面加载完成
   */
  async waitForPageLoad(): Promise<void> {
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * 等待元素可见
   */
  async waitForElement(selector: string, timeout: number = 5000): Promise<void> {
    await this.page.waitForSelector(selector, { state: 'visible', timeout });
  }

  /**
   * 点击元素并等待
   */
  async clickAndWait(selector: string, waitTime: number = 500): Promise<void> {
    await this.page.click(selector);
    await TestDataFactory.wait(waitTime);
  }

  /**
   * 填写表单字段
   */
  async fillForm(fields: Record<string, string>): Promise<void> {
    for (const [selector, value] of Object.entries(fields)) {
      await this.page.fill(selector, value);
      await TestDataFactory.wait(100); // 短暂等待
    }
  }

  /**
   * 截图用于调试
   */
  async takeScreenshot(name: string): Promise<void> {
    await this.page.screenshot({ path: `test-results/${name}-${Date.now()}.png` });
  }
}