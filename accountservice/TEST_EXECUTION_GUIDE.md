# AccountService JUnit Test Execution Guide

## Quick Start

### Run All Tests
```bash
cd K:\Assignment\newSetUp\accountservice
mvnw test
```

### Run Specific Test Class
```bash
mvnw test -Dtest=AccountserviceApplicationTests
```

### Run Specific Test Method
```bash
mvnw test -Dtest=AccountserviceApplicationTests#testPostAccountSuccess
```

## Test Suite Overview

### Total Tests: 20

#### Service Layer Tests (14 tests)
- ✅ POST transaction with metadata
- ✅ POST with null request
- ✅ POST without metadata
- ✅ GET balance with credit
- ✅ GET balance with debit
- ✅ GET balance with mixed transactions
- ✅ GET balance not found
- ✅ GET history success
- ✅ GET history not found
- ✅ Handle null amount
- ✅ Generate UUID for eventId
- ✅ Use provided timestamp
- ✅ Handle null in balance calculation
- ✅ Generate timestamp when not provided

#### Controller Layer Tests (3 tests)
- ✅ POST controller endpoint
- ✅ GET balance controller endpoint
- ✅ GET history controller endpoint

#### Additional Tests (3 tests)
- ✅ Context loads successfully
- ✅ Edge case handling

## Test Categories

### 1. Happy Path Tests
Tests that verify successful scenarios with valid inputs:
- Successfully posting valid transactions
- Retrieving account balance correctly
- Retrieving account history successfully

### 2. Negative Path Tests
Tests that verify proper error handling:
- Null request body handling
- No transactions found scenarios
- Proper error response formatting

### 3. Edge Case Tests
Tests that verify boundary conditions:
- Null amount values
- Missing optional fields (eventId, timestamp)
- Mixed credit/debit transactions
- Zero balance scenarios

## Expected Test Results

All tests should PASS with the following output:
```
BUILD SUCCESS
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
Total time: XX.XXXs
```

## Debugging Failed Tests

### If Tests Fail:

1. **Check Repository Mocks**
   - Verify mocks are initialized: `MockitoAnnotations.openMocks(this)`
   - Check mock setup in test methods

2. **Check Test Data**
   - Verify setUp() initializes all test data correctly
   - Check BigDecimal values use `.scale()` properly

3. **Check Assertions**
   - Review assertion logic for test expectations
   - Verify enum values match (Status.SUCCESS, Status.FAILURE)

4. **Check Service Logic**
   - Review AcountService implementation
   - Verify business logic matches test expectations

### Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| MockitoAnnotations not initialized | Call `MockitoAnnotations.openMocks(this)` in `@BeforeEach` |
| Mock not returning expected value | Verify `when()` clause matches test input |
| BigDecimal comparison fails | Use `.compareTo()` or equals() for precision |
| Null pointer in test | Check test data initialization in setUp() |

## Test Profiles

Tests use `@ActiveProfiles("test")` to load test-specific configuration.

Create `application-test.properties` if needed:
```properties
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

## Code Coverage Analysis

### Using JaCoCo Plugin

Add to pom.xml:
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Generate coverage report:
```bash
mvnw clean test jacoco:report
```

Report location: `target/site/jacoco/index.html`

## IDE Integration

### Eclipse
1. Right-click test class → Run As → JUnit Test
2. View test results in JUnit View
3. Coverage available with EclEmma plugin

### IntelliJ IDEA
1. Right-click test class → Run 'AccountserviceApplicationTests'
2. View results in Run tool window
3. Coverage automatically available with IDE

### VS Code
1. Install Test Explorer extension
2. Run tests from test explorer UI
3. View results inline

## Continuous Integration

### GitHub Actions Example
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: cd accountservice && mvnw test
```

## Test Maintenance

### When Adding New Features:
1. Add corresponding test cases
2. Ensure backward compatibility
3. Run all tests to verify no regression
4. Maintain >80% code coverage

### When Refactoring:
1. Run tests before and after refactoring
2. Ensure all tests still pass
3. Update tests if behavior changes
4. Add tests for new edge cases found

## Performance Benchmarks

Typical test execution times:
- Full suite: ~10-15 seconds
- Single test: ~1-2 seconds
- Context initialization: ~5-7 seconds (first run)

## Troubleshooting

### "No tests found to run"
```bash
# Ensure test class name ends with "Tests"
# Or use correct -Dtest parameter
mvnw test -Dtest=AccountserviceApplicationTests
```

### "Compilation errors in tests"
```bash
# Clean rebuild
mvnw clean compile test
```

### "Mocks not initialized"
```bash
# Ensure @Mock annotation used
# Ensure MockitoAnnotations.openMocks(this) called
```

## Best Practices

✅ DO:
- Use descriptive test names with `@DisplayName`
- Follow Arrange-Act-Assert pattern
- Test one behavior per test method
- Mock external dependencies
- Use meaningful test data
- Verify mock interactions with verify()

❌ DON'T:
- Test multiple behaviors in one test
- Use real database/services in unit tests
- Leave debugging code or print statements
- Skip test setup/teardown
- Ignore test failures
- Over-mock simple objects

## Support & Questions

For issues or questions about tests:
1. Check TEST_DOCUMENTATION.md for detailed explanations
2. Review test comments in code
3. Check assertions in similar test methods
4. Consult Spring Boot Testing Guide

---
Last Updated: June 26, 2026
Test Framework: JUnit 5 + Mockito
Spring Boot: 4.1.0
