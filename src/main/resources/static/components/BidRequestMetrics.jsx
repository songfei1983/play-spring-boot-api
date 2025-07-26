import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  Grid,
  Typography,
  Box,
  CircularProgress,
  Alert,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip
} from '@mui/material';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer
} from 'recharts';
import { Refresh, TrendingUp, Speed, CheckCircle, Error } from '@mui/icons-material';

const BidRequestMetrics = () => {
  const [realTimeStats, setRealTimeStats] = useState(null);
  const [adSlotStats, setAdSlotStats] = useState([]);
  const [dspStats, setDspStats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [dateRange, setDateRange] = useState('7'); // 默认7天
  const [autoRefresh, setAutoRefresh] = useState(true);

  // 颜色配置
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#82CA9D'];

  // 获取实时统计数据
  const fetchRealTimeStats = async () => {
    try {
      const response = await fetch('/api/v1/bid-request-metrics/realtime');
      if (!response.ok) throw new Error('获取实时数据失败');
      const data = await response.json();
      setRealTimeStats(data);
    } catch (err) {
      console.error('获取实时统计失败:', err);
      setError(err.message);
    }
  };

  // 获取广告位类型统计
  const fetchAdSlotStats = async () => {
    try {
      const endDate = new Date();
      const startDate = new Date();
      startDate.setDate(endDate.getDate() - parseInt(dateRange));
      
      const params = new URLSearchParams({
        startDate: startDate.toISOString().split('T')[0],
        endDate: endDate.toISOString().split('T')[0]
      });
      
      const response = await fetch(`/api/v1/bid-request-metrics/ad-slot-types?${params}`);
      if (!response.ok) throw new Error('获取广告位统计失败');
      const data = await response.json();
      setAdSlotStats(data);
    } catch (err) {
      console.error('获取广告位统计失败:', err);
      setError(err.message);
    }
  };

  // 获取DSP来源统计
  const fetchDspStats = async () => {
    try {
      const endDate = new Date();
      const startDate = new Date();
      startDate.setDate(endDate.getDate() - parseInt(dateRange));
      
      const params = new URLSearchParams({
        startDate: startDate.toISOString().split('T')[0],
        endDate: endDate.toISOString().split('T')[0]
      });
      
      const response = await fetch(`/api/v1/bid-request-metrics/dsp-sources?${params}`);
      if (!response.ok) throw new Error('获取DSP统计失败');
      const data = await response.json();
      setDspStats(data);
    } catch (err) {
      console.error('获取DSP统计失败:', err);
      setError(err.message);
    }
  };

  // 加载所有数据
  const loadAllData = async () => {
    setLoading(true);
    setError(null);
    try {
      await Promise.all([
        fetchRealTimeStats(),
        fetchAdSlotStats(),
        fetchDspStats()
      ]);
    } finally {
      setLoading(false);
    }
  };

  // 初始加载和自动刷新
  useEffect(() => {
    loadAllData();
  }, [dateRange]);

  useEffect(() => {
    if (autoRefresh) {
      const interval = setInterval(fetchRealTimeStats, 30000); // 30秒刷新一次实时数据
      return () => clearInterval(interval);
    }
  }, [autoRefresh]);

  // 格式化数字
  const formatNumber = (num) => {
    if (num >= 1000000) {
      return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'K';
    }
    return num?.toString() || '0';
  };

  // 格式化百分比
  const formatPercentage = (num) => {
    return `${(num || 0).toFixed(2)}%`;
  };

  if (loading && !realTimeStats) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      {/* 页面标题和控制按钮 */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Bid Request 统计面板
        </Typography>
        <Box display="flex" gap={2} alignItems="center">
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>时间范围</InputLabel>
            <Select
              value={dateRange}
              label="时间范围"
              onChange={(e) => setDateRange(e.target.value)}
            >
              <MenuItem value="1">最近1天</MenuItem>
              <MenuItem value="7">最近7天</MenuItem>
              <MenuItem value="30">最近30天</MenuItem>
            </Select>
          </FormControl>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={loadAllData}
            disabled={loading}
          >
            刷新
          </Button>
          <Button
            variant={autoRefresh ? "contained" : "outlined"}
            onClick={() => setAutoRefresh(!autoRefresh)}
          >
            {autoRefresh ? '停止自动刷新' : '开启自动刷新'}
          </Button>
        </Box>
      </Box>

      {/* 错误提示 */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* 实时统计卡片 */}
      {realTimeStats && (
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                  <Box>
                    <Typography color="textSecondary" gutterBottom>
                      总请求数
                    </Typography>
                    <Typography variant="h4">
                      {formatNumber(realTimeStats.totalRequests)}
                    </Typography>
                  </Box>
                  <TrendingUp color="primary" sx={{ fontSize: 40 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                  <Box>
                    <Typography color="textSecondary" gutterBottom>
                      今日请求数
                    </Typography>
                    <Typography variant="h4">
                      {formatNumber(realTimeStats.todayRequests)}
                    </Typography>
                  </Box>
                  <TrendingUp color="secondary" sx={{ fontSize: 40 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                  <Box>
                    <Typography color="textSecondary" gutterBottom>
                      成功率
                    </Typography>
                    <Typography variant="h4">
                      {formatPercentage(realTimeStats.successRate)}
                    </Typography>
                  </Box>
                  <CheckCircle color="success" sx={{ fontSize: 40 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                  <Box>
                    <Typography color="textSecondary" gutterBottom>
                      平均响应时间
                    </Typography>
                    <Typography variant="h4">
                      {(realTimeStats.avgResponseTime || 0).toFixed(0)}ms
                    </Typography>
                  </Box>
                  <Speed color="info" sx={{ fontSize: 40 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* 图表区域 */}
      <Grid container spacing={3}>
        {/* 广告位类型统计 */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardHeader title="广告位类型统计" />
            <CardContent>
              {adSlotStats.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={adSlotStats}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="adSlotType" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="totalRequests" fill="#8884d8" name="总请求数" />
                    <Bar dataKey="successCount" fill="#82ca9d" name="成功数" />
                  </BarChart>
                </ResponsiveContainer>
              ) : (
                <Typography color="textSecondary" align="center">
                  暂无数据
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* DSP来源统计 */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardHeader title="DSP来源分布" />
            <CardContent>
              {dspStats.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={dspStats}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ dspSource, percent }) => `${dspSource} ${(percent * 100).toFixed(0)}%`}
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="totalRequests"
                    >
                      {dspStats.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <Typography color="textSecondary" align="center">
                  暂无数据
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* 详细数据表格 */}
        <Grid item xs={12}>
          <Card>
            <CardHeader title="详细统计数据" />
            <CardContent>
              <Grid container spacing={3}>
                {/* 广告位类型表格 */}
                <Grid item xs={12} md={6}>
                  <Typography variant="h6" gutterBottom>
                    广告位类型详情
                  </Typography>
                  <TableContainer component={Paper} variant="outlined">
                    <Table size="small">
                      <TableHead>
                        <TableRow>
                          <TableCell>类型</TableCell>
                          <TableCell align="right">请求数</TableCell>
                          <TableCell align="right">成功数</TableCell>
                          <TableCell align="right">成功率</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {adSlotStats.map((row) => (
                          <TableRow key={row.adSlotType}>
                            <TableCell>
                              <Chip label={row.adSlotType} size="small" />
                            </TableCell>
                            <TableCell align="right">{formatNumber(row.totalRequests)}</TableCell>
                            <TableCell align="right">{formatNumber(row.successCount)}</TableCell>
                            <TableCell align="right">{formatPercentage(row.successRate)}</TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </Grid>

                {/* DSP来源表格 */}
                <Grid item xs={12} md={6}>
                  <Typography variant="h6" gutterBottom>
                    DSP来源详情
                  </Typography>
                  <TableContainer component={Paper} variant="outlined">
                    <Table size="small">
                      <TableHead>
                        <TableRow>
                          <TableCell>来源</TableCell>
                          <TableCell align="right">请求数</TableCell>
                          <TableCell align="right">成功数</TableCell>
                          <TableCell align="right">成功率</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {dspStats.map((row) => (
                          <TableRow key={row.dspSource}>
                            <TableCell>
                              <Chip label={row.dspSource} size="small" color="primary" />
                            </TableCell>
                            <TableCell align="right">{formatNumber(row.totalRequests)}</TableCell>
                            <TableCell align="right">{formatNumber(row.successCount)}</TableCell>
                            <TableCell align="right">{formatPercentage(row.successRate)}</TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* 最后更新时间 */}
      {realTimeStats && (
        <Box mt={3} textAlign="center">
          <Typography variant="caption" color="textSecondary">
            最后更新时间: {new Date(realTimeStats.timestamp).toLocaleString()}
          </Typography>
        </Box>
      )}
    </Box>
  );
};

export default BidRequestMetrics;