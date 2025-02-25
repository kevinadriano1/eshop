https://economic-felicity-kevinadriano1-bee2995b.koyeb.app/

## module 1
### Reflection 1

I applied some of the clean code principles that are being taught in the class such as meaningful naming conventions, ensuring that variable like productID will done its job as the ID of the product. I also followed the Single Responsibility Principle (SRP) by separating concerns between the controller, service, and repository layers. Additionally, I reduced code duplication by reusing logic in service and repository methods. I also implement secure coding practices that are introduced in the class, I implemented confirmation prompts for delete actions. I identified areas for improvement, such as adding input validation using annotations like @NotNull and @Size, and improving error handling with custom error pages. I also plan to enhance security by adding authorization checks to prevent unauthorized access to edit or delete endpoints.

### Reflection 2
After writing unit test I realized that unit test is an important part of software development. I think that the number of unit test depends on the complexity of the function being tested. in a class there should be enough test case to ensure the validity of different scenarios. achieving 100% code coverage does not imply that the code is free of bugs and error, we still have to check all possible input scenarios that might come up. If we were asked to create another functional test suite that verifies the number of items in the product list, I might end up duplicating the setup code from CreateProductFunctionalTest.java. while this might work, this might result in a code redundancy, which can reduce code cleanliness and quality. there are potential clean code issues that might appear, one of the example is the repetition of instance variable (serverPort, testBaseUrl, baseUrl), any changes in one file require adjusting to multiple files. the suggestion to improve this code redundancy is by introducing a new class that contain the common setup logic This class would handle browser setup, server port initialization, and URL management, which can be inherited by all functional test classes.

## module 2
### Reflection
During the exercise, I fixed permission issue in the dockerfile and ci.yml which was solved by adding chmod +x gradlew before running the build command. I also addressed a missing assertion in the test cases by ensuring meaningful validations were added to improve test reliability. I also added SonarCloud to my project, the reason is to analyze code quality and detect potential issues early in the development process. My current CI/CD implementation follows Continuous Integration principles by automatically running tests and analyzing code quality on every push or pull request. I also add Continuous Deployment feature where the workflow automatically deploys the latest changes to Koyeb whenever the main branch is updated. This automation will reduce manual errors, ensuring high code quality, and enabling faster, more reliable deployments.

## module 3
### Reflection
1) Explain what principles you apply to your project!
SRP-Create separate controllers for each entity (ProductController for products and CarController for cars), without inheritance.
OCP-Avoid inheritance here. Use separate controllers that are independent of each other. If shared logic exists, extract it into a common utility or base controller class that explicitly handles common functionality.
LSP-Remove inheritance relationship. Both controllers should independently extend a base abstract controller class or directly use annotations.
DIP-Inject the service via its interface (CarService) rather than the implementation (CarServiceImpl).

2) Explain the advantages of applying SOLID principles to your project with examples.
SRP-Each class has a single responsibility, making the system easier to maintain and less prone to bugs. (example: Splitting the combined CarController and ProductController into two distinct controllers (CarController and ProductController) simplifies debugging and changes.)
OCP-Your classes become open for extension but closed for modification. Adding features becomes straightforward and safer.
```
@Controller
class ProductController {
    // Handles only product logic
}

@Controller
class CarController {
    // Handles car-related endpoints independently
}

```
introducing a new entity (BikeController) becomes as simple as adding a new class, without modifying existing controllers.
LSP-Subclasses can be transparently substituted without affecting program correctness. example: Do not use inheritance when behaviors differ significantly. Use clear abstractions when shared behavior exists.
ISP-Interfaces remain focused and cohesive, preventing classes from implementing irrelevant methods.
example:
```
public interface CarService {
    void create(Car car);
    List<Car> findAll();
    void deleteCarById(String id);
    // All methods are specifically related to Car operations
}

```
Reduces coupling and dependency complexities. Each service or client interacts only with necessary functionalities, simplifying implementation.
DIP-High-level modules depend on abstractions (interfaces), making your system flexible and testable.
example:
```
@Autowired
private CarService carService; // injecting via abstraction
```
Switching implementations (e.g., database or cache) is easy without changing controller or service code.

3) Explain the disadvantages of not applying SOLID principles to your project with examples.
High Coupling and Complex Maintenance (SRP Violation):

One class performing multiple unrelated tasks results in difficulty managing code.
```
class ProductController {
    void createProduct() { }
    void createCar() { }
}
```
Any change in product logic can unintentionally affect car logic, causing unpredictable issues.

Difficulties in Extending Features (OCP Violation):

Modifying existing code frequently for adding new functionality introduces bugs.
If adding BikeController requires modifying existing controller classes rather than extending them, complexity increases:
```
class ProductController {
    void createBike() { } // adding new features modifies the class directly
}

```
Brittle Inheritance (LSP Violation):

Extending unrelated controllers introduces unnecessary methods, causing unintended behavior.
Example (Violation):
```
class CarController extends ProductController {
    // inherits methods unrelated to Car operations
}
```
Misuse of inheritance leads to bugs when methods intended only for Products mistakenly apply to cars.
Rigid Dependency Management (DIP Violation):

Relying on concrete implementations restricts the flexibility of your code.
Example (Violation):
```
@Autowired
CarServiceImpl carService; // concrete dependency
```
Hard to switch implementations (e.g., from MySQL to PostgreSQL) without extensive code changes.