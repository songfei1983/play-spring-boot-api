# E2E Tests

This directory contains end-to-end tests using Playwright.

## GitHub Actions Integration

E2E tests are automatically run in GitHub Actions as part of the CI/CD pipeline:

### Main CI Pipeline
- **Trigger**: Push to `master`/`develop` branches or PRs to these branches
- **Location**: `.github/workflows/ci.yml`
- **Job**: `e2e-tests`
- **Dependencies**: Runs after unit tests pass
- **Artifacts**: Uploads test reports and results

### Standalone E2E Workflow
- **Trigger**: Manual dispatch, version tags, or workflow calls
- **Location**: `.github/workflows/e2e-tests.yml`
- **Use case**: Independent E2E testing without full CI pipeline

## Running Tests Locally

```bash
# Install dependencies
npm install

# Install Playwright browsers
npx playwright install

# Run tests
npm run test:e2e

# Run tests with UI
npm run test:e2e:ui

# Run tests in headed mode
npm run test:e2e:headed

# Debug tests
npm run test:e2e:debug

# Show test report
npm run test:e2e:report
```

## Test Structure

- `app.spec.ts` - Basic application tests
- `user-management.spec.ts` - User management functionality tests
- `user-profile.spec.ts` - User profile management tests
- `activity-tracking.spec.ts` - Activity tracking tests
- `purchase-history.spec.ts` - Purchase history tests
- `test-data.ts` - Shared test data and utilities

## Configuration

Playwright configuration is in `playwright.config.ts` in the frontend root directory.

## CI/CD Pipeline Flow

1. **Unit Tests** - Java backend tests with multiple JDK versions
2. **Code Quality** - SonarCloud analysis (parallel with E2E)
3. **Security Scan** - OWASP dependency check (parallel with E2E)
4. **E2E Tests** - Full application testing with Playwright
5. **Docker Build** - Only after all tests pass (main branch only)

## Test Reports

When tests run in GitHub Actions:
- **Playwright Report**: Available as `playwright-report` artifact
- **Test Results**: Available as `test-results` artifact
- **Retention**: 30 days

## Troubleshooting

### Local Development
- Ensure both backend (port 8080) and frontend (port 3000) are running
- Check that all dependencies are installed
- Verify Playwright browsers are installed

### CI/CD Issues
- Check server startup logs in GitHub Actions
- Verify health check endpoints are responding
- Review uploaded artifacts for detailed error information

## Test Coverage

Current test suites cover:
- **User Management**: CRUD operations, form validation, search functionality
- **User Profile**: Profile creation, editing, validation, responsive design
- **Activity Tracking**: Activity logging and display
- **Purchase History**: Transaction history and filtering
- **Navigation**: Application routing and basic functionality

All tests are designed to run reliably in CI/CD environments with proper setup and teardown procedures.