Cursor Agent Development Guidelines
This document outlines the principles and best practices that must be followed for all development tasks. The goal is to ensure we create code that is clean, maintainable, scalable, and robust.

Core Principles
These are the non-negotiable rules for every piece of code written.

1. Single Responsibility Principle (SRP) & Brevity
One Job Only: Every function, class, or component must have one, and only one, reason to change. It should do one thing and do it well.

Keep it Small: Write the smallest possible unit of code that fulfills a specific requirement. Functions should be short and focused. If a function is getting long, it's a sign it's doing too much and should be broken down.

Incremental Development: Implement features in small, logical, and testable increments. Avoid "big bang" changes.

2. Test-Driven Development (TDD) as a Standard
Test First (or Immediately After): For every piece of functionality you write, you must write corresponding tests. This ensures that the code behaves as expected and protects it from future regressions.

Secure Functionality: The primary goal of testing is to secure the code's functionality. Cover the happy path, edge cases, and potential error conditions.

Types of Tests: At a minimum, unit tests are required. For more complex interactions, integration tests should be considered.

Senior Developer Best Practices
In addition to the core principles, follow these guidelines to ensure the highest quality of work.

Code Quality & Style
Follow Style Guides: Adhere strictly to the established style guide for the language/framework of the project (e.g., PEP 8 for Python, Airbnb Style Guide for JavaScript).

Consistent Formatting: Use automated formatters like Prettier or Black to ensure uniform code style across the entire project.

Linter Enforcement: Use a linter (e.g., ESLint, Pylint) to catch common errors and enforce coding standards. Address all linter warnings before committing.

Descriptive Naming: Use clear, descriptive, and unambiguous names for variables, functions, and classes. Avoid abbreviations. Good naming makes code self-documenting.

Code Hygiene
DRY (Don't Repeat Yourself): Avoid duplicating code. If you find yourself writing the same logic in multiple places, abstract it into a reusable function or component.

No Commented-Out Code: Dead or unused code should be deleted, not commented out. Version control is the source of truth for historical code.

YAGNI (You Ain't Gonna Need It): Do not add functionality or create abstractions until they are actually necessary. Avoid premature optimization and over-engineering.

The Boy Scout Rule: Leave the codebase cleaner than you found it. If you touch a file, take a moment to make a small improvement—clarify a variable name, remove a redundant comment, or break down a complex function.

Modularity and Architecture
Component-Based Design: Break down complex UIs and features into smaller, reusable components.

Clear Separation of Concerns: Keep business logic, data access, and UI rendering separate. For example, don't mix API calls directly into UI components.

Configuration Management: Do not hardcode configuration values (API keys, URLs, ports). Store them in environment variables or dedicated configuration files.

Version Control (Git)
Atomic Commits: Each commit should represent a single logical change. Avoid bundling unrelated changes into one commit.

Conventional Commit Messages: Follow the Conventional Commits specification. This creates an explicit and easily readable commit history.

Format: feat: add user authentication endpoint or fix: resolve issue with form validation

Body (Optional): Use the commit body to explain the 'what' and 'why' of the change, not the 'how'.

Documentation & Comments
Code Over Comments: Strive to write code that is so clear it doesn't need comments.

Justify the 'Why': When comments are necessary, they should explain why something is being done, not what is being done. The code itself shows the 'what'.

Document Complex Logic: For any particularly complex algorithms, business rules, or non-obvious code, provide clear documentation.

API Documentation: For public functions or API endpoints, use standard documentation formats (e.g., JSDoc, Python Docstrings).

Error Handling & Reliability
Graceful Failure: Anticipate potential errors (e.g., network failures, invalid input) and handle them gracefully. The application should not crash.

Informative Error Messages: Provide clear, user-friendly error messages that help with debugging and inform the user what went wrong.

No Swallowed Errors: Do not ignore caught exceptions. Log them or handle them appropriately.

Security
Sanitize Inputs: Never trust user input. Always sanitize and validate data to prevent common vulnerabilities like XSS and SQL injection.

Principle of Least Privilege: Code should only have the permissions it needs to perform its task.

Dependency Audits: Regularly check project dependencies for known security vulnerabilities.