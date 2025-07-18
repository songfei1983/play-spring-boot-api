import reportWebVitals from './reportWebVitals';
import { ReportHandler } from 'web-vitals';

// Mock web-vitals module
const mockGetCLS = jest.fn();
const mockGetFID = jest.fn();
const mockGetFCP = jest.fn();
const mockGetLCP = jest.fn();
const mockGetTTFB = jest.fn();

jest.mock('web-vitals', () => ({
  getCLS: mockGetCLS,
  getFID: mockGetFID,
  getFCP: mockGetFCP,
  getLCP: mockGetLCP,
  getTTFB: mockGetTTFB,
}));

describe('reportWebVitals', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call all web vitals functions when onPerfEntry is provided', async () => {
    const mockOnPerfEntry: ReportHandler = jest.fn();
    
    reportWebVitals(mockOnPerfEntry);
    
    // Wait for dynamic import to resolve
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).toHaveBeenCalledWith(mockOnPerfEntry);
    expect(mockGetFID).toHaveBeenCalledWith(mockOnPerfEntry);
    expect(mockGetFCP).toHaveBeenCalledWith(mockOnPerfEntry);
    expect(mockGetLCP).toHaveBeenCalledWith(mockOnPerfEntry);
    expect(mockGetTTFB).toHaveBeenCalledWith(mockOnPerfEntry);
  });

  it('should not call web vitals functions when onPerfEntry is undefined', async () => {
    reportWebVitals();
    
    // Wait for potential dynamic import
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).not.toHaveBeenCalled();
    expect(mockGetFID).not.toHaveBeenCalled();
    expect(mockGetFCP).not.toHaveBeenCalled();
    expect(mockGetLCP).not.toHaveBeenCalled();
    expect(mockGetTTFB).not.toHaveBeenCalled();
  });

  it('should not call web vitals functions when onPerfEntry is null', async () => {
    reportWebVitals(null as any);
    
    // Wait for potential dynamic import
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).not.toHaveBeenCalled();
    expect(mockGetFID).not.toHaveBeenCalled();
    expect(mockGetFCP).not.toHaveBeenCalled();
    expect(mockGetLCP).not.toHaveBeenCalled();
    expect(mockGetTTFB).not.toHaveBeenCalled();
  });

  it('should not call web vitals functions when onPerfEntry is not a function', async () => {
    reportWebVitals('not a function' as any);
    
    // Wait for potential dynamic import
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).not.toHaveBeenCalled();
    expect(mockGetFID).not.toHaveBeenCalled();
    expect(mockGetFCP).not.toHaveBeenCalled();
    expect(mockGetLCP).not.toHaveBeenCalled();
    expect(mockGetTTFB).not.toHaveBeenCalled();
  });

  it('should handle function check correctly for arrow functions', async () => {
    const arrowFunction: ReportHandler = (metric) => {
      console.log(metric);
    };
    
    reportWebVitals(arrowFunction);
    
    // Wait for dynamic import to resolve
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).toHaveBeenCalledWith(arrowFunction);
    expect(mockGetFID).toHaveBeenCalledWith(arrowFunction);
    expect(mockGetFCP).toHaveBeenCalledWith(arrowFunction);
    expect(mockGetLCP).toHaveBeenCalledWith(arrowFunction);
    expect(mockGetTTFB).toHaveBeenCalledWith(arrowFunction);
  });

  it('should handle function check correctly for regular functions', async () => {
    function regularFunction(metric: any) {
      console.log(metric);
    }
    
    reportWebVitals(regularFunction);
    
    // Wait for dynamic import to resolve
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).toHaveBeenCalledWith(regularFunction);
    expect(mockGetFID).toHaveBeenCalledWith(regularFunction);
    expect(mockGetFCP).toHaveBeenCalledWith(regularFunction);
    expect(mockGetLCP).toHaveBeenCalledWith(regularFunction);
    expect(mockGetTTFB).toHaveBeenCalledWith(regularFunction);
  });

  it('should handle objects that are not functions', async () => {
    const notAFunction = { call: 'not a function' };
    
    reportWebVitals(notAFunction as any);
    
    // Wait for potential dynamic import
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).not.toHaveBeenCalled();
    expect(mockGetFID).not.toHaveBeenCalled();
    expect(mockGetFCP).not.toHaveBeenCalled();
    expect(mockGetLCP).not.toHaveBeenCalled();
    expect(mockGetTTFB).not.toHaveBeenCalled();
  });

  it('should handle boolean values', async () => {
    reportWebVitals(true as any);
    
    // Wait for potential dynamic import
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).not.toHaveBeenCalled();
    expect(mockGetFID).not.toHaveBeenCalled();
    expect(mockGetFCP).not.toHaveBeenCalled();
    expect(mockGetLCP).not.toHaveBeenCalled();
    expect(mockGetTTFB).not.toHaveBeenCalled();
  });

  it('should handle number values', async () => {
    reportWebVitals(123 as any);
    
    // Wait for potential dynamic import
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).not.toHaveBeenCalled();
    expect(mockGetFID).not.toHaveBeenCalled();
    expect(mockGetFCP).not.toHaveBeenCalled();
    expect(mockGetLCP).not.toHaveBeenCalled();
    expect(mockGetTTFB).not.toHaveBeenCalled();
  });

  it('should call each web vital function exactly once', async () => {
    const mockOnPerfEntry: ReportHandler = jest.fn();
    
    reportWebVitals(mockOnPerfEntry);
    
    // Wait for dynamic import to resolve
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).toHaveBeenCalledTimes(1);
    expect(mockGetFID).toHaveBeenCalledTimes(1);
    expect(mockGetFCP).toHaveBeenCalledTimes(1);
    expect(mockGetLCP).toHaveBeenCalledTimes(1);
    expect(mockGetTTFB).toHaveBeenCalledTimes(1);
  });

  it('should work with console.log as onPerfEntry', async () => {
    const consoleSpy = jest.spyOn(console, 'log').mockImplementation();
    
    reportWebVitals(console.log);
    
    // Wait for dynamic import to resolve
    await new Promise(resolve => setTimeout(resolve, 0));
    
    expect(mockGetCLS).toHaveBeenCalledWith(console.log);
    expect(mockGetFID).toHaveBeenCalledWith(console.log);
    expect(mockGetFCP).toHaveBeenCalledWith(console.log);
    expect(mockGetLCP).toHaveBeenCalledWith(console.log);
    expect(mockGetTTFB).toHaveBeenCalledWith(console.log);
    
    consoleSpy.mockRestore();
  });
});