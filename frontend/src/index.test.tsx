import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import reportWebVitals from './reportWebVitals';

// Mock ReactDOM.createRoot
const mockRender = jest.fn();
const mockCreateRoot = jest.fn(() => ({
  render: mockRender,
  unmount: jest.fn(),
}));

jest.mock('react-dom/client', () => ({
  createRoot: mockCreateRoot,
}));

// Mock App component
jest.mock('./App', () => {
  return function MockApp() {
    return <div data-testid="app">Mock App Component</div>;
  };
});

// Mock reportWebVitals
jest.mock('./reportWebVitals', () => jest.fn());

// Mock document.getElementById
const mockRootElement = document.createElement('div');
mockRootElement.id = 'root';
Object.defineProperty(document, 'getElementById', {
  writable: true,
  value: jest.fn().mockReturnValue(mockRootElement),
});

describe('index.tsx', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.resetModules();
  });

  it('should create root and render App component', () => {
    // Import to trigger the module execution
    require('./index');
    
    expect(document.getElementById).toHaveBeenCalledWith('root');
    expect(mockCreateRoot).toHaveBeenCalledWith(mockRootElement);
    expect(mockRender).toHaveBeenCalledWith(
      <React.StrictMode>
        <App />
      </React.StrictMode>
    );
    expect(reportWebVitals).toHaveBeenCalledWith();
  });

  it('should handle missing root element', () => {
    // Mock getElementById to return null
    (document.getElementById as jest.Mock).mockReturnValueOnce(null);
    
    // This should still work due to TypeScript assertion
    expect(() => {
      require('./index');
    }).not.toThrow();
    
    expect(document.getElementById).toHaveBeenCalledWith('root');
    expect(mockCreateRoot).toHaveBeenCalledWith(null);
  });

  it('should call reportWebVitals function', () => {
    require('./index');
    expect(reportWebVitals).toHaveBeenCalledTimes(1);
  });
});