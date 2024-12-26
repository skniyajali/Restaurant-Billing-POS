# Restaurant POS Application

## Table of Contents
- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Feature Modules](#feature-modules)
- [Getting Started](#getting-started)
- [Build and Deploy](#build-and-deploy)
- [Testing](#testing)
- [Code Quality](#code-quality)
- [Continuous Integration](#continuous-integration)
- [Contributing](#contributing)
- [License](#license)

## Project Overview

The Restaurant POS Application is a comprehensive Android solution designed to streamline restaurant operations. From order management to employee tracking, this app covers all aspects of running a modern restaurant efficiently.

## Key Features

1. **User Authentication and Account Management**
    - Secure login and registration system with email and phone
    - Password recovery and reset functionality
    - User profile management with customizable settings

2. **Order Management System**
    - Intuitive interface for creating and modifying orders
    - Real-time order tracking from kitchen to delivery
    - Support for dine-in, takeout, and delivery order types
    - Order history and status updates
    - Integration with kitchen display systems for seamless communication

3. **Product Catalog and Menu Management**
    - Comprehensive product listings with detailed descriptions and images
    - Easy-to-use interface for adding, editing, and removing menu items
    - Support for categorization and tagging of products
    - Seasonal menu management and special offers

4. **Advanced Shopping Cart**
    - Multiple cart support
    - User-friendly cart system with real-time updates
    - Discount and promotion application

5. **Employee Management Suite**
    - Comprehensive staff profiles and role management
    - Time tracking and shift scheduling
    - Performance metrics and goal setting
    - Payroll integration with automatic calculation of wages, taxes, and deductions
    - Employee communication and task assignment tools

6. **Advanced Reporting and Analytics**
    - Real-time sales dashboards and financial reports
    - Inventory tracking and low stock alerts
    - Customer behavior analysis and preferences tracking
    - Peak hour and seasonal trend identification
    - Customizable report generation for various business metrics

7. **Integrated Printing System**
    - Support for multiple printer types (thermal, laser, etc.)
    - Customizable receipt templates
    - Kitchen order ticket printing
    - Cloud printing capabilities for remote management
    - Automatic reprinting for lost tickets

8. **Offline Mode and Data Synchronization**
   - Core functionalities available without internet connection
   - Automatic data synchronization when connection is restored
   - Conflict resolution for offline changes
   - Local data encryption for security

9. **Customer Relationship Management (CRM)**
   - Customer profiles with order history and preferences
   - Loyalty program integration with points and rewards
   - Automated email and SMS marketing campaigns
   - Feedback collection and management system
   - Birthday and anniversary tracking for personalized offers

10. **Analytics and Business Intelligence**
    - Advanced data visualization tools and interactive charts
    - Predictive analytics for inventory and staffing needs
    - Customizable KPI tracking and goal setting
    - Competitive analysis tools for market positioning
    - Export capabilities for further analysis in external tools

These expanded feature descriptions provide a more comprehensive view of the Restaurant POS Application's capabilities, 
highlighting its robust and versatile nature in managing various aspects of restaurant operations.

## Technology Stack

- **Languages**: Kotlin, Java
- **Build System**: Gradle
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Database**: Room
- **Asynchronous Programming**: Coroutines, Flow
- **Testing**: JUnit, Espresso, Robolectric, Roborazzi
- **Logging**: Timber
- **Analytics and Crash Reporting**: Firebase, Sentry
- **Code Quality**: Detekt, KtLint, Android Lint

## Architecture

The application follows a modular, clean architecture approach:

- **Presentation Layer**: MVVM pattern with Jetpack Compose for UI
- **Domain Layer**: Use cases and business logic
- **Data Layer**: Repositories and data sources
- **DI**: Hilt for dependency injection across modules

## Feature Modules

The application is divided into the following feature modules, each responsible for a specific set of functionalities:

1. **[Account](feature/account)**
    - Manages user authentication and profile information
    - Handles login, registration, and password recovery processes
    - Manages user preferences and settings

2. **[Cart](feature/cart)**
    - Manages the shopping cart functionality
    - Handles adding, removing, and updating items in the cart
    - Calculates subtotals, taxes, and discounts
    - Supports item customization and special instructions
    - Provides real-time updates on cart contents and total

3. **[Order](feature/order)**
    - Manages the entire order lifecycle from creation to fulfillment
    - Handles different order types (dine-in, dine-out)
    - Implements order tracking and status updates
    - Manages order history and allows for easy reordering
    - Integrates with the kitchen display system for order preparation

4. **[Product](feature/product)**
    - Manages the product catalog and menu items
    - Handles product categorization and tagging
    - Manages product variations and customization options
    - Implements search and filtering functionality
    - Handles product availability and inventory integration

5. **[Addon Item](feature/addonitem)**
    - Manages additional items that can be added to products
    - Handles pricing for add-ons and their impact on the total order
    - Implements rules for add-on compatibility with different products
    - Manages add-on categories and groupings

6. **[Address](feature/address)**
    - Manages customer delivery addresses
    - Handles address validation and formatting
    - Integrates with mapping services for location accuracy
    - Manages multiple addresses per user
    - Handles default address selection

7. **[Cart Order](feature/cartorder)**
    - Manages the transition from cart to confirmed order
    - Handles order summary generation
    - Implements order confirmation and receipt generation
    - Handles order cancellation and modification requests

8. **[Category](feature/category)**
    - Manages product categories and subcategories
    - Handles category hierarchy and relationships
    - Implements category-based product filtering and sorting
    - Manages category visibility and seasonal categories
    - Handles category-specific promotions and discounts

9. **[Charges](feature/charges)**
    - Manages additional charges such as taxes, service fees, and delivery fees
    - Implements dynamic charge calculation based on order details
    - Manages special charges for specific products or categories
    - Implements charge overrides and exemptions

10. **[Customer](feature/customer)**
    - Manages customer profiles and information
    - Handles customer segmentation and grouping
    - Implements loyalty program functionality
    - Manages customer preferences and dietary restrictions
    - Handles customer feedback and ratings

11. **[Employee](feature/employee)**
    - Manages employee information and accounts
    - Handles role assignment and permissions
    - Implements employee scheduling and shift management
    - Manages employee performance metrics
    - Handles employee communications and task assignments

12. **[Employee Payment](feature/employee_payment)**
    - Manages employee payroll and compensation
    - Handles salary calculations, including taxes and deductions
    - Implements tip distribution and reporting
    - Manages payment schedules and direct deposit information

13. **[Employee Absent](feature/employee_absent)**
    - Manages employee attendance and leave tracking
    - Handles leave requests and approvals
    - Implements absence reporting and documentation
    - Manages different types of leave (sick, vacation, personal)
    - Integrates with scheduling to manage coverage for absent employees

14. **[Expenses](feature/expenses)**
    - Manages business expense tracking and categorization
    - Handles expense report generation and approval workflows
    - Implements budget tracking and variance analysis
    - Manages receipt capture and storage
    - Integrates with accounting systems for financial reporting

15. **[Home](feature/home)**
    - Provides the main dashboard and application entry point
    - Displays key metrics and notifications
    - Implements quick access to frequently used features
    - Manages user-specific dashboard customization
    - Handles real-time updates of critical information

16. **[Print Order](feature/print_order)**
    - Manages order printing functionality
    - Implements printer management and configuration
    - Handles print queue management and error handling

17. **[Profile](feature/profile)**
    - Manages user profile information and settings
    - Handles profile picture management
    - Implements notification preferences

18. **[Cart Selected](feature/cart_selected)**
    - Manages selected items within the cart for bulk actions
    - Implements multi-item operations (delete, move, duplicate)
    - Handles quantity updates for selected items
    - Manages application of discounts or promotions to selected items
    - Implements undo/redo functionality for bulk actions

19. **[Settings](feature/settings)**
    - Manages application-wide settings and configurations
    - Handles language and localization settings
    - Implements theme and display preferences
    - Manages integration settings for third-party services
    - Handles system maintenance and update management

20. **[Printer Info](feature/printer_info)**
    - Manages printer configurations and status information
    - Handles printer discovery and setup
    - Implements printer status monitoring and error reporting
    - Manages printer groups for specific order types or locations
    - Handles print job history and reprint functionality

21. **[Reports](feature/reports)**
    - Manages generation of various business reports
    - Implements customizable report templates
    - Handles data aggregation and analysis for reporting
    - Manages scheduling and distribution of automated reports
    - Implements export functionality in various formats (PDF, CSV, Excel)

22. **[Chart](feature/chart)**
    - Manages data visualization and charting functionality
    - Implements various chart types (bar, line, pie, etc.)
    - Handles real-time data updates for live charts
    - Manages chart customization and styling options
    - Implements interactive features like zooming and data point inspection

23. **[Market](feature/market)**
    - Manages marketplace functionality for multi-vendor scenarios
    - Handles vendor onboarding and management
    - Implements commission calculations and payouts
    - Manages vendor ratings and reviews
    - Handles cross-vendor order fulfillment and tracking

24. **[Printer](feature/printer)**
    - Manages printer operations and maintenance
    - Handles print job creation and queuing
    - Implements printer-specific formatting and layout
    - Handles printer troubleshooting and diagnostic tools

Each module is designed to be self-contained yet able to communicate with other modules as needed, 
promoting a modular and maintainable architecture. This structure allows for easier testing, updating, and scaling of individual components of the application.

## Getting Started

To get started with the project:

1. Clone the repository:
   ```
   git clone https://github.com/skniyajali/PoposRoom.git
   ```
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Set up your local.properties file with necessary API keys

## Build and Deploy

The project uses Gradle for building and deployment:

- **Debug Build**: `./gradlew assembleDebug`
- **Release Build**: `./gradlew assembleRelease`
- **Run Tests**: `./gradlew test`
- **Deploy to Firebase**: `./gradlew appDistributionUploadRelease`

## Testing

The project emphasizes thorough testing:

- **Unit Tests**: JUnit for logic and ViewModel testing
- **UI Tests**: Espresso and Compose UI Testing
- **Integration Tests**: End-to-end testing of feature flows
- **Screenshot Tests**: Roborazzi for UI regression testing

Run all tests with: `./gradlew test`

## Code Quality

We maintain high code quality standards using:

- **Detekt**: Static code analysis for Kotlin
- **KtLint**: Kotlin linter and formatter
- **Android Lint**: Custom lint rules for Android-specific checks

Run quality checks with: `./gradlew check`

## Continuous Integration

The project uses GitHub Actions for CI/CD:

- Automated builds and tests on pull requests
- Code quality checks
- Weekly Beta Release
- Monthly Production Release
- Automated deployment to Amazon App Distribution
- Automated deployment to Sentry
- Deployment to Firebase App Distribution for beta testing

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

Please ensure your code adheres to our coding standards and is well-tested.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE) file for details.

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fskniyajali%2FRestaurant-Billing-POS.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fskniyajali%2FRestaurant-Billing-POS?ref=badge_large)

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fskniyajali%2FRestaurant-Billing-POS.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fskniyajali%2FRestaurant-Billing-POS?ref=badge_shield)

## Screenshots
| ![1](docs/mockup/1.png)   | ![2](docs/mockup/2.png)   | ![3](docs/mockup/3.png)   |
|---------------------------|---------------------------|---------------------------|
| ![4](docs/mockup/4.png)   | ![5](docs/mockup/5.png)   | ![6](docs/mockup/6.png)   |
| ![7](docs/mockup/7.png)   | ![8](docs/mockup/8.png)   | ![9](docs/mockup/9.png)   |
| ![10](docs/mockup/10.png) | ![11](docs/mockup/11.png) | ![12](docs/mockup/12.png) |
| ![13](docs/mockup/13.png) | ![14](docs/mockup/14.png) | ![15](docs/mockup/15.png) |
| ![16](docs/mockup/16.png) | ![17](docs/mockup/17.png) | ![18](docs/mockup/18.png) |
| ![19](docs/mockup/19.png) | ![20](docs/mockup/20.png) | ![21](docs/mockup/21.png) |
| ![22](docs/mockup/22.png) | ![23](docs/mockup/23.png) | ![24](docs/mockup/24.png) |
| ![25](docs/mockup/25.png) | ![26](docs/mockup/26.png) |                           |
